# C-1 バックエンド移行設計：Render → Google Cloud Run

## 概要

Render の無料プランはスリープ問題（15分無操作でスリープ → 初回アクセスに30〜60秒）がある。
Google Cloud Run へ移行することで、スリープ問題を解消する。

**関連タスク**: C-1
**移行先**: Google Cloud Run (us-central1)
**作成日**: 2026-04-18

---

## 現状と問題

| 項目 | 現状 |
|---|---|
| サービス | Render Free |
| スリープ | 15 分無操作でスリープ |
| コールドスタート遅延 | 30〜60 秒 |
| デプロイ方法 | GitHub 連携（自動） |
| ビルドコマンド | `npm run build && npm start` |

---

## 移行先：Google Cloud Run

### なぜ Cloud Run か

| 特長 | 詳細 |
|---|---|
| コンテナベース | Docker イメージとして動作 |
| スケールゼロ対応 | `min-instances=0`（無料）or `min-instances=1`（月$5〜） |
| 無料枠 | リクエスト 200万回/月、vCPU 360,000秒/月 |
| HTTPS 自動 | Cloud Run 提供の URL で即時 HTTPS |
| CI/CD 連携 | GitHub Actions からデプロイ可能 |

---

## コスト比較

| 構成 | 月額 | スリープ | 備考 |
|---|---|---|---|
| Render Free（現状） | $0 | あり（15分） | コールドスタート 30〜60秒 |
| Cloud Run + min=0 + ping | $0 | なし（ping で回避） | UptimeRobot で5分毎 ping |
| Cloud Run + min=1 | ~$5〜8 | なし（常時起動） | 最もシンプル |
| Render Starter | $7 | なし | コード変更不要だが割高 |

**推奨**: `min-instances=0` + UptimeRobot ping（完全無料・コールドスタートなし）

---

## 実装内容

### 1. `Dockerfile`（新規作成）

`~/dev/ts-memo-api/Dockerfile`

```dockerfile
# ── ビルドステージ ──────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# ── 実行ステージ ────────────────────────────────
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY package*.json ./
RUN npm ci --omit=dev
COPY --from=builder /app/dist ./dist
EXPOSE 8080
CMD ["node", "dist/index.js"]
```

**ポート注意**: Cloud Run はデフォルトで `8080` を使用。
`src/index.ts` の `PORT` 環境変数が `process.env.PORT` を参照していれば対応済み（Cloud Run が `PORT=8080` を自動注入）。

---

### 2. `.dockerignore`（新規作成）

```
node_modules
dist
.env*
.git
```

---

### 3. GitHub Actions ワークフロー（新規作成）

`.github/workflows/deploy_cloud_run.yml`

```yaml
name: Deploy to Cloud Run

on:
  push:
    branches: [main]

env:
  PROJECT_ID: ${{ secrets.GCP_PROJECT_ID }}
  REGION: us-central1
  SERVICE: ts-memo-api

jobs:
  deploy:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      id-token: write   # Workload Identity Federation 用

    steps:
      - uses: actions/checkout@v4

      - name: Authenticate to Google Cloud
        uses: google-github-actions/auth@v2
        with:
          workload_identity_provider: ${{ secrets.GCP_WORKLOAD_IDENTITY_PROVIDER }}
          service_account: ${{ secrets.GCP_SERVICE_ACCOUNT }}

      - name: Set up Cloud SDK
        uses: google-github-actions/setup-gcloud@v2

      - name: Build and push Docker image
        run: |
          gcloud builds submit \
            --tag "gcr.io/$PROJECT_ID/$SERVICE:$GITHUB_SHA" \
            --project "$PROJECT_ID"

      - name: Deploy to Cloud Run
        run: |
          gcloud run deploy "$SERVICE" \
            --image "gcr.io/$PROJECT_ID/$SERVICE:$GITHUB_SHA" \
            --region "$REGION" \
            --platform managed \
            --min-instances 0 \
            --max-instances 3 \
            --memory 512Mi \
            --cpu 1 \
            --timeout 30s \
            --set-secrets "DATABASE_URL=DATABASE_URL:latest,JWT_SECRET=JWT_SECRET:latest" \
            --set-env-vars "NODE_ENV=production" \
            --allow-unauthenticated \
            --project "$PROJECT_ID"
```

---

### 4. src/index.ts のポート変更確認

Cloud Run は `PORT` 環境変数（デフォルト `8080`）を自動設定する。
現状のコードが以下の形式なら変更不要：

```typescript
const PORT = process.env.PORT ?? 3000;
app.listen(PORT, ...);
```

`3000` のハードコードの場合のみ `process.env.PORT ?? 3000` に変更が必要。

---

### 5. UptimeRobot 設定（コールドスタート回避）

min-instances=0 の場合、UptimeRobot（無料）で定期 ping を設定。

| 設定項目 | 値 |
|---|---|
| URL | `https://<cloud-run-url>/api/v1/health` |
| 監視間隔 | 5分 |
| アラート | 不要（開発用のため） |

**ヘルスチェックエンドポイントの追加が必要**:

```typescript
app.get("/api/v1/health", (_req, res) => {
  res.status(200).json({ status: "ok" });
});
```

---

## 環境変数の移行

現在 Render ダッシュボードで管理している環境変数を GCP Secret Manager に移行。

| 変数名 | 移行先 |
|---|---|
| `DATABASE_URL` | Secret Manager |
| `JWT_SECRET` | Secret Manager |
| `CORS_ORIGINS` | Cloud Run 環境変数 |
| `NODE_ENV` | Cloud Run 環境変数（`production`） |

---

## 作業手順（実装時）

1. GCP プロジェクト作成・課金設定（無料枠内）
2. Cloud Run API / Artifact Registry API / Secret Manager API を有効化
3. GitHub Actions 用 Service Account 作成・Workload Identity Federation 設定
4. Secret Manager に環境変数を登録
5. `Dockerfile` + `.dockerignore` 作成
6. `deploy_cloud_run.yml` 作成
7. ヘルスチェックエンドポイント追加
8. `src/index.ts` のポート確認・修正
9. 初回手動デプロイでテスト
10. DNS/URL を Android アプリの `BASE_URL` に更新
11. UptimeRobot 設定
12. Render サービス停止

---

## Android アプリへの影響

`core/network/NetworkModule.kt` の BASE_URL を変更するのみ。

```kotlin
// 変更前
private const val BASE_URL = "https://ts-memo-api-1.onrender.com/"

// 変更後
private const val BASE_URL = "https://<new-cloud-run-url>/"
```

---

## リスク・注意事項

| リスク | 対策 |
|---|---|
| コールドスタート（min=0時） | UptimeRobot で5分毎 ping |
| GCP 無料枠超過 | 個人アプリ規模では超過しない見込み |
| DB 接続先は変更なし | `DATABASE_URL` をそのまま移行 |
| Render の旧 URL が使えなくなる | アプリの BASE_URL 更新が必要 |

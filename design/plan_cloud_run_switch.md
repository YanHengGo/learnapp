# Cloud Run 移行設計：エンドポイント切り替え

## 概要

**問題**: Render 無料プランはスリープ → 初回アクセスに 30〜60 秒かかる
**対策**: Google Cloud Run へ移行（スリープなし）
**DB**: Render PostgreSQL をそのまま流用（変更なし）
**切り替え**: ビルドタイプ（debug / release）で Render ↔ Cloud Run を切り替え可能

---

## アーキテクチャ

```
┌─────────────────────────────────────────────────────────┐
│                    Android アプリ                        │
│                                                          │
│  debug ビルド  →  Render API (ts-memo-api-1.onrender.com)│
│  release ビルド → Cloud Run API (<cloud-run-url>)         │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP
          ┌────────────┴────────────┐
          ▼                         ▼
   Render API (旧)          Cloud Run API (新)
   [スリープあり]            [常時起動]
          │                         │
          └─────────┬───────────────┘
                    ▼
             Render PostgreSQL
              （DB は共通）
```

---

## 変更内容

### 1. `app/build.gradle.kts`

`buildConfigField` で `BASE_URL` をビルドタイプごとに定義する。

```kotlin
android {
    buildFeatures {
        compose = true
        buildConfig = true  // 追加
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://ts-memo-api-1.onrender.com/\""  // Render（既存）
            )
        }
        release {
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://<cloud-run-url>/\""  // Cloud Run（デプロイ後に確定）
            )
            isMinifyEnabled = true
            // ... 既存設定はそのまま
        }
    }
}
```

---

### 2. `core/network/di/NetworkModule.kt`

ハードコードされた URL を `BuildConfig.BASE_URL` に変更。

```kotlin
// 変更前
.baseUrl("https://ts-memo-api-1.onrender.com/")

// 変更後
.baseUrl(com.learn.app.BuildConfig.BASE_URL)
```

---

### 3. バックエンド（Cloud Run）の環境変数

Cloud Run の Secret Manager に以下を設定する。

| 変数名 | 値 | 設定方法 |
|---|---|---|
| `DATABASE_URL` | Render PostgreSQL の接続文字列（既存の値をコピー） | Secret Manager |
| `JWT_SECRET` | 既存の値をコピー | Secret Manager |
| `NODE_ENV` | `production` | Cloud Run 環境変数 |
| `CORS_ORIGINS` | Cloud Run URL + Vercel URL | Cloud Run 環境変数 |

**ポイント**: `DATABASE_URL` は Render PostgreSQL の接続文字列をそのまま使う。DB 移行不要。

---

## 切り替えフロー

```
┌──────────────────────────────────────────────────────────┐
│ 開発中（debug ビルド）                                    │
│   → Render API を使用                                    │
│   → スリープはあるが、開発用途なので許容                 │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│ 本番（release ビルド / Play Store 配布）                  │
│   → Cloud Run API を使用                                 │
│   → スリープなし、常時応答                               │
└──────────────────────────────────────────────────────────┘
```

---

## 実装手順

### Phase 1: Android アプリ修正（コード変更）

1. `app/build.gradle.kts` に `buildConfig = true` を追加
2. `buildTypes` に `debug` ブロックと `BASE_URL` を追加
3. `release` ブロックにも `BASE_URL` を追加（Cloud Run URL は仮で OK）
4. `NetworkModule.kt` の `baseUrl()` を `BuildConfig.BASE_URL` に変更

### Phase 2: Cloud Run デプロイ（GCP 作業）

1. GCP プロジェクト作成・API 有効化
2. Render PostgreSQL の `DATABASE_URL` を Secret Manager に登録
3. `JWT_SECRET` を Secret Manager に登録
4. GitHub Actions の Secrets を設定
   - `GCP_PROJECT_ID`
   - `GCP_WORKLOAD_IDENTITY_PROVIDER`
   - `GCP_SERVICE_ACCOUNT`
5. `main` ブランチに push → 自動デプロイ
6. Cloud Run の URL を取得

### Phase 3: URL 確定・反映

1. Cloud Run の URL（`https://xxx-uc.a.run.app`）を確認
2. `app/build.gradle.kts` の release `BASE_URL` に設定
3. `CORS_ORIGINS` に Cloud Run URL を追加してバックエンドを再デプロイ

### Phase 4: 動作確認

1. debug ビルド → Render API が呼ばれることを確認
2. release ビルド → Cloud Run API が呼ばれることを確認（ログで URL を確認）

---

## 修正ファイル一覧

| ファイル | 種別 | 内容 |
|---|---|---|
| `app/build.gradle.kts` | 修正 | `buildConfig = true`・`BASE_URL` 定義 |
| `core/network/di/NetworkModule.kt` | 修正 | `BuildConfig.BASE_URL` 参照に変更 |
| `ts-memo-api/` Dockerfile 等 | 作成済み | 前回の設計・実装で完了 |

---

## 注意事項

- `BuildConfig` は `app` モジュールに生成される。`core:network` モジュールから参照するため、`applicationId` を指定する必要がある（`com.learn.app.BuildConfig`）
- Cloud Run の URL は初回デプロイ後に確定するため、`release BASE_URL` は Phase 3 で更新する
- Render API は debug 用として残す（すぐに廃止しない）

---

**作成日**: 2026-04-19

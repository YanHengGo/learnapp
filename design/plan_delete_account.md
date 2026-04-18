# アカウント削除機能 設計

## 概要

Play Store 審査要件（2024年〜必須）に対応するため、ユーザーが自分のアカウントをアプリ内から削除できる機能を実装する。

- **対象**: LearnApp (com.yanheng.learnapp)
- **関連タスク**: B-1

---

## 削除対象データ

アカウント削除時に即時削除するデータ:

| テーブル | 内容 |
|---|---|
| `study_logs` | 日々の学習記録 |
| `tasks` | タスク定義 |
| `children` | 子ども情報 |
| `users` | ユーザーアカウント（メールアドレス等）|

**削除順序**: 外部キー制約（ON DELETE CASCADE なし）のため手動で順序を守る。
```
study_logs → tasks → children → users
```

---

## 修正箇所一覧

### 1. バックエンド (`~/dev/ts-memo-api`)

| 対象 | 内容 |
|---|---|
| `src/index.ts` | `DELETE /api/v1/me` エンドポイント追加（要 authMiddleware） |

**実装方針**:
- `authMiddleware` で認証済みユーザーの `userId` を取得
- トランザクション内で以下の順に DELETE:
  1. `DELETE FROM study_logs WHERE user_id = $1`
  2. `DELETE FROM tasks WHERE user_id = $1`
  3. `DELETE FROM children WHERE user_id = $1`
  4. `DELETE FROM users WHERE id = $1`
- 成功時: `204 No Content` を返す

---

### 2. Android アプリ (`~/dev/learnapp`)

| 対象ファイル | 変更内容 |
|---|---|
| `core/network/.../LearnApiService.kt` | `@DELETE("api/v1/me") suspend fun deleteAccount()` を追加 |
| `core/domain/.../AuthRepository.kt` | `suspend fun deleteAccount()` を追加 |
| `core/data/.../AuthRepositoryImpl.kt` | `deleteAccount()` の実装を追加（API 呼び出し → トークン削除） |
| `core/domain/usecase/DeleteAccountUseCase.kt` | 新規作成 |
| `feature/home/` または `feature/auth/` | アカウント削除 UI（確認ダイアログ）を追加 |

**UI 配置案**: `HomeScreen` のハンバーガーメニュー or 設定エリアに「アカウントを削除」ボタンを追加。
タップ時に確認ダイアログを表示し、承認後にアカウント削除 → ログアウト → AuthScreen へ遷移。

---

### 3. Web フロントエンド (`~/dev/learning-app-web`) ※オプション

| 対象 | 内容 |
|---|---|
| `src/lib/api.ts` | `deleteAccount()` 関数追加 |
| 設定ページ（新規） | アカウント削除 UI 追加 |

Play Store 審査要件は Android アプリのみ対象だが、整合性のために追加することも可能。

---

## API 仕様

```
DELETE /api/v1/me
Authorization: Bearer <token>

Response:
  204 No Content  （成功）
  401 Unauthorized（未認証）
  500 Internal Server Error（DB エラー）
```

---

## 実装順序

1. バックエンド: `DELETE /api/v1/me` エンドポイント実装・デプロイ
2. Android: `LearnApiService` → `AuthRepository` → `DeleteAccountUseCase` → UI
3. （オプション）Web フロントエンド

---

**作成日**: 2026-04-18

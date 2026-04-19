# タスクバックログ

最終更新: 2026-04-19

---

## カテゴリ A — ストア公開準備（優先度：高）

| # | 内容 | 状態 |
|---|---|---|
| A-1 | `applicationId` を `com.yanheng.learnapp` に変更 | ✅ 完了 |
| A-2 | アプリアイコンの作成・設定 | ✅ 完了 |
| A-3 | リリースビルド設定（`signingConfig`、ProGuard）| ✅ 完了 |
| A-4 | Play Store データ安全フォームの記入 | 🔲 未対応（設計済み・手作業）|
| A-5 | プライバシーポリシーの外部 URL 公開 | ✅ 完了（Notion）|

---

## カテゴリ B — 機能追加（優先度：中）

| # | 内容 | 状態 |
|---|---|---|
| B-1 | アカウント削除機能 | ✅ 完了 |
| B-2 | タスクの並び替え | ✅ 完了 |
| B-3 | オフライン時のエラーメッセージ改善 | ✅ 完了 |

---

## カテゴリ C — コード品質（優先度：低）

| # | 内容 | 状態 |
|---|---|---|
| C-1 | バックエンドの Cloud Run 移行・エンドポイント切り替え | ✅ 完了 |

---

## カテゴリ D — インフラ（優先度：検討）

| # | 内容 | 状態 | 備考 |
|---|---|---|---|
| D-1 | CI でのビルド自動化（GitHub Actions） | 🔲 未対応 | 現状は Claude Code Review のみ |
| D-2 | バックエンド API のログアウトエンドポイント追加 | 🔲 未対応 | 現状はクライアント側のトークン削除のみ |

---

## 完了済み（詳細）

| カテゴリ | 内容 |
|---|---|
| 基盤 | マルチモジュール構成、Gradle設定、.gitignore |
| 基盤 | SDK・Java バージョンを libs.versions.toml に一元管理 |
| core:model | 全モデル（Child, Task, DailyView, CalendarSummary, Summary 等）|
| core:network | 全DTO、LearnApiService、NetworkModule、AuthInterceptor |
| core:network | BuildConfig.BASE_URL でビルドタイプ別エンドポイント切り替え |
| core:datastore | TokenDataStore（JWT保存）|
| core:data | 全Repository実装、Mapper、DataModule |
| core:domain | 全Repositoryインターフェース、全UseCase |
| core:common | Throwable.toErrorMessage()（オフライン・サーバーエラー判別）|
| feature:splash | スプラッシュ画面・トークンチェック |
| feature:auth | ログイン・サインアップ・プライバシーポリシー |
| feature:children | 子ども一覧・追加・編集・削除・アカウント削除 |
| feature:home | BottomNavigation・子ども切り替え・ログアウト確認 |
| feature:tasks | タスク一覧・追加・編集・アーカイブ・並び替え |
| feature:daily | 日々の記録・チェック・時間入力・保存 |
| feature:summary | カレンダー集計・科目別統計 |
| テスト | 全画面 Compose UI テスト（instrumented）|
| テスト | 全画面 @Preview（Android Studio プレビュー）|
| テスト | Maestro E2E テスト・スクリーンショット自動保存 |
| CI/CD | Claude Code Review（PR自動レビュー）|
| バックエンド | DELETE /api/v1/me アカウント削除エンドポイント |
| バックエンド | Dockerfile・GitHub Actions で Cloud Run 自動デプロイ |
| バックエンド | GET /api/v1/health ヘルスチェックエンドポイント |
| 設計 | design/ フォルダ・全設計書 |

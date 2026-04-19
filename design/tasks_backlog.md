# タスクバックログ

最終更新: 2026-04-18

---

## カテゴリ A — ストア公開準備（優先度：高）

| # | 内容 | 状態 |
|---|---|---|
| A-1 | `applicationId` を `com.yanheng.learnapp` に変更 | ✅ 完了 |
| A-2 | アプリアイコンの作成・設定 | ✅ 完了 |
| A-3 | リリースビルド設定（`signingConfig`、ProGuard）| ✅ 完了 |
| A-4 | Play Store データ安全フォームの記入 | 🔲 未対応（設計済み）|
| A-5 | プライバシーポリシーの外部 URL 公開 | ✅ 完了（Notion）|

---

## カテゴリ B — 機能追加（優先度：中）

| # | 内容 | 状態 | 理由 |
|---|---|---|---|
| B-1 | アカウント削除機能 | ✅ 完了 | Play Store 審査要件（2024年〜必須）|
| B-2 | タスクの並び替え | ✅ 完了 | UseCase・APIは実装済み。UIが未実装 |
| B-3 | オフライン時のエラーメッセージ改善 | ✅ 完了 | ネットワークエラーがそのまま表示される可能性あり |

---

## カテゴリ C — コード品質（優先度：低）

| # | 内容 | 状態 | 理由 |
|---|---|---|---|
| C-1 | バックエンドのフリープラン移行対策 | ✅ 完了 | Render の無料プランはスリープあり → 有料化 or 移行 |

---

## カテゴリ D — インフラ（優先度：検討）

| # | 内容 | 状態 | 理由 |
|---|---|---|---|
| D-1 | CI でのビルド自動化（GitHub Actions）| 🔲 未対応 | 現状は Claude Code Review のみ |
| D-2 | バックエンド API のログアウトエンドポイント追加 | 🔲 未対応 | 現状はクライアント側のトークン削除のみ |

---

## 完了済み

| カテゴリ | 内容 |
|---|---|
| 基盤 | マルチモジュール構成、Gradle設定、.gitignore |
| core:model | 全モデル（Child, Task, DailyView, CalendarSummary, Summary 等）|
| core:network | 全DTO、LearnApiService、NetworkModule、AuthInterceptor |
| core:datastore | TokenDataStore（JWT保存）|
| core:data | 全Repository実装、Mapper、DataModule |
| core:domain | 全Repositoryインターフェース、全UseCase |
| feature:splash | スプラッシュ画面・トークンチェック |
| feature:auth | ログイン・サインアップ・プライバシーポリシー |
| feature:children | 子ども一覧・追加・編集・削除 |
| feature:home | BottomNavigation・子ども切り替え・ログアウト確認 |
| feature:tasks | タスク一覧・追加・編集・アーカイブ |
| feature:daily | 日々の記録・チェック・時間入力・保存 |
| feature:summary | カレンダー集計・科目別統計 |
| テスト | 全画面 Compose UI テスト（instrumented）|
| テスト | 全画面 @Preview（Android Studio プレビュー）|
| テスト | Maestro E2E テスト |
| テスト | スクリーンショット自動保存 |
| CI/CD | Claude Code Review（PR自動レビュー）|
| 設計 | design/ フォルダ・命名規則整備 |
| A-1 | applicationId → com.yanheng.learnapp に変更 |
| A-2 | アプリアイコン設定（PNG、各密度）|
| A-3 | リリースビルド設定（signingConfig・ProGuard・AAB）|
| A-5 | プライバシーポリシー Notion 公開 |
| B-1 | アカウント削除機能（バックエンド + Android）|

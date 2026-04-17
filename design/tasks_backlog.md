# タスクバックログ

最終更新: 2026-04-17

---

## カテゴリ A — ストア公開準備（優先度：高）

| # | 内容 | 理由 |
|---|---|---|
| A-1 | `applicationId` を `com.learn.app` → 正式なIDに変更 | Play Store 登録前に必須。後から変えると既存ユーザーのデータが消える |
| A-2 | アプリアイコンの作成・設定 | 審査・ストア掲載に必要 |
| A-3 | リリースビルド設定（`signingConfig`、ProGuard）| APK/AAB 署名なしでは公開不可 |
| A-4 | Play Store データ安全フォームの記入 | 2023年以降必須。どのデータを収集するか申告 |

---

## カテゴリ B — 機能追加（優先度：中）

| # | 内容 | 理由 |
|---|---|---|
| B-1 | アカウント削除機能 | Play Store 審査要件（2024年〜、アカウント削除手段の提供が必須）|
| B-2 | タスクの並び替え（`ReorderTasksUseCase` は実装済み）| UIが未実装。UseCase・APIは既にある |
| B-3 | オフライン時のエラーメッセージ改善 | 現状はネットワークエラーがそのまま表示される可能性あり |

---

## カテゴリ C — コード品質（優先度：低）

| # | 内容 | 理由 |
|---|---|---|
| C-1 | バックエンドのフリープラン移行対策 | Render の無料プランはスリープあり → 有料化 or 移行を検討 |

---

## カテゴリ D — インフラ（優先度：検討）

| # | 内容 | 理由 |
|---|---|---|
| D-1 | CI でのビルド自動化（GitHub Actions）| 現状は Claude Code Review のみ。ビルド・テストの自動実行がない |
| D-2 | バックエンド API のログアウトエンドポイント追加 | 現状のログアウトはクライアント側のトークン削除のみ |

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

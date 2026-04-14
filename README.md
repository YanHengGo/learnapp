# LearnApp

子どもの学習を管理するAndroidアプリケーションです。複数の子どもの学習タスク・日々の記録・成績サマリーを一元管理できます。

## 機能

- **認証** — サインアップ / ログイン / ログアウト
- **子ども管理** — 複数の子どもの登録・編集・削除、切り替え
- **タスク管理** — 曜日・期間指定のタスク作成・編集・アーカイブ
- **日々の記録** — 日別の学習ログ記録・チェック
- **集計** — カレンダー形式の学習成績サマリー・科目別統計

## スクリーンショット

<!-- スクリーンショットを追加してください -->

## 技術スタック

| カテゴリ | ライブラリ |
|---|---|
| 言語 | Kotlin |
| UI | Jetpack Compose, Material3 |
| ナビゲーション | Navigation Compose |
| 状態管理 | ViewModel, Compose State |
| DI | Hilt |
| ネットワーク | Retrofit, OkHttp |
| ローカルストレージ | DataStore Preferences |
| 非同期処理 | Kotlin Coroutines |

## アーキテクチャ

Clean Architecture + MVVM パターンを採用したマルチモジュール構成です。

```
LearnApp
├── app                  エントリーポイント・ナビゲーション
├── core
│   ├── model            データモデル
│   ├── domain           ユースケース・リポジトリインターフェース
│   ├── data             リポジトリ実装
│   ├── network          Retrofit API クライアント
│   ├── datastore        ローカルデータ永続化
│   └── ui               共有UIコンポーネント
└── feature
    ├── splash           スプラッシュ画面
    ├── auth             認証画面
    ├── children         子ども一覧画面
    ├── home             ホーム画面（BottomNavigation）
    ├── tasks            タスク管理画面
    ├── daily            日々の記録画面
    └── summary          集計画面
```

## 動作環境

- Android 9.0 (API 28) 以上

## セットアップ

### 1. リポジトリをクローン

```bash
git clone https://github.com/<your-username>/learnapp.git
cd learnapp
```

### 2. APIエンドポイントの設定

`core/network/src/main/java/com/learn/app/core/network/di/NetworkModule.kt` のベースURLを環境に合わせて変更してください。

### 3. ビルド

```bash
./gradlew assembleDebug
```

## API

バックエンドAPIとの通信には JWT 認証を使用します。

| メソッド | エンドポイント | 概要 |
|---|---|---|
| POST | `/api/v1/auth/signup` | ユーザー登録 |
| POST | `/api/v1/auth/login` | ログイン |
| GET | `/api/v1/children` | 子ども一覧取得 |
| POST | `/api/v1/children` | 子ども作成 |
| GET | `/api/v1/children/{childId}/tasks` | タスク一覧取得 |
| POST | `/api/v1/children/{childId}/tasks` | タスク作成 |
| GET | `/api/v1/children/{childId}/daily-view` | 日次ビュー取得 |
| PUT | `/api/v1/children/{childId}/daily` | 日次ログ更新 |
| GET | `/api/v1/children/{childId}/calendar-summary` | カレンダーサマリー取得 |
| GET | `/api/v1/children/{childId}/summary` | 学習サマリー取得 |

## テスト

### Compose UI テスト

各 feature モジュールに Jetpack Compose の instrumented テストを実装しています。モック不要の `internal` Composable 関数に対して UI の表示・操作をテストします。

**対象モジュール**

| モジュール | テストファイル |
|---|---|
| feature:auth | `AuthScreenTest.kt` |
| feature:children | `ChildrenScreenTest.kt` |
| feature:home | `HomeScreenTest.kt` |
| feature:splash | `SplashScreenTest.kt` |
| feature:tasks | `TasksScreenTest.kt` |
| feature:daily | `DailyScreenTest.kt` |
| feature:summary | `SummaryScreenTest.kt` |

**実行方法**

```bash
# 全モジュールのUIテストを実行（接続済みデバイス/エミュレーターが必要）
./gradlew connectedAndroidTest

# 特定モジュールのみ
./gradlew :feature:tasks:connectedAndroidTest
```

---

### Maestro テスト（E2E）

[Maestro](https://maestro.mobile.dev/) を使ったE2Eテストです。実機またはエミュレーター上で実際のアプリを操作して動作確認を行います。

**前提条件**

```bash
# Maestro インストール
curl -Ls "https://get.maestro.mobile.dev" | bash

# バージョン確認
maestro --version
```

**テスト用アカウント設定**

`maestro/.env` にテスト用の認証情報を設定してください：

```
TEST_EMAIL=your-test@example.com
TEST_PASSWORD=yourpassword
```

**全テスト一括実行**

```bash
# デフォルト（テスト間 5 秒待機）
./maestro/run_all_tests.sh

# 待機時間を変更
./maestro/run_all_tests.sh --wait 10

# 特定フローをスキップ（例: フロー 02 をスキップ）
./maestro/run_all_tests.sh --skip 02

# ドライラン（実行せずにコマンド確認）
./maestro/run_all_tests.sh --dry-run
```

**個別フロー実行**

```bash
maestro test maestro/flows/01_auth_login.yaml --env TEST_EMAIL=xxx --env TEST_PASSWORD=yyy
```

**フロー一覧**

| ファイル | 内容 | ログイン前提 |
|---|---|---|
| `01_auth_login.yaml` | ログイン / ログアウト | 不要 |
| `02_auth_signup.yaml` | 新規サインアップ | 不要 |
| `03_children_list.yaml` | 子ども一覧・選択・戻る | 必要 |
| `04_children_crud.yaml` | 子どもの追加・編集・削除 | 必要 |
| `05_home_navigation.yaml` | BottomNav タブ切り替え・切り替えダイアログ | 必要 |
| `06_tasks_crud.yaml` | タスクの追加・編集・アーカイブ | 必要 |
| `07_daily_record.yaml` | 日々の記録のチェック・時間入力・保存 | 必要 |
| `09_full_flow.yaml` | ログイン〜タスク追加〜記録〜集計の全体フロー | 不要（条件付きログイン）|

テスト結果（JUnit XML）は `maestro/results/` に出力されます（`.gitignore` 対象）。

---

## CI/CD

PR作成・更新時に [Claude Code Action](https://github.com/anthropics/claude-code-action) による自動コードレビューが実行されます。

実行にはリポジトリの Settings > Secrets に `ANTHROPIC_API_KEY` の設定が必要です。

## ライセンス

MIT

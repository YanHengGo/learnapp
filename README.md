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

## CI/CD

PR作成・更新時に [Claude Code Action](https://github.com/anthropics/claude-code-action) による自動コードレビューが実行されます。

実行にはリポジトリの Settings > Secrets に `ANTHROPIC_API_KEY` の設定が必要です。

## ライセンス

MIT

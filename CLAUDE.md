# LearnApp - Claude Code ガイド

## プロジェクト概要

子どもの学習を管理するAndroidアプリ。Kotlin + Jetpack Compose で実装したマルチモジュール構成。

バックエンドAPI: `https://ts-memo-api-1.onrender.com/api/v1`

## アーキテクチャ

Clean Architecture + MVVM。依存方向: `feature → core:domain → core:data → core:network`

```
app/                        エントリーポイント・NavGraph
feature/
  splash/                   スプラッシュ・トークンチェック
  auth/                     ログイン・サインアップ
  children/                 子ども一覧
  home/                     BottomNavigation ホーム
  tasks/                    タスク管理
  daily/                    日々の記録
  summary/                  集計・カレンダー
core/
  model/                    データモデル（Child, Task など）
  domain/                   UseCase・Repositoryインターフェース
  data/                     Repository実装・Hilt Module
  network/                  Retrofit APIクライアント・DTO
  datastore/                TokenDataStore（JWT保存）
  ui/                       共有UIコンポーネント
```

## 技術スタック

- **言語**: Kotlin / minSdk 28
- **UI**: Jetpack Compose + Material3
- **DI**: Hilt（`@HiltViewModel` + `SavedStateHandle`）
- **ナビゲーション**: Navigation Compose（2階層 NavHost）
- **ネットワーク**: Retrofit + OkHttp + AuthInterceptor（Bearer JWT）
- **ローカル保存**: DataStore Preferences
- **非同期**: Kotlin Coroutines + `Result<T>` パターン

## コーディング規約

### 新しい画面を追加する場合

1. `feature/<name>/` モジュールを作成
2. `feature/<name>/build.gradle.kts` を追加（`feature/tasks/build.gradle.kts` を参考）
3. `settings.gradle.kts` に `include(":feature:<name>")` を追加
4. `app/build.gradle.kts` に `implementation(project(":feature:<name>"))` を追加
5. `NavGraph.kt` にルートを追加

### ViewModel

```kotlin
@HiltViewModel
class XxxViewModel @Inject constructor(
    private val xxxUseCase: XxxUseCase,
) : ViewModel() {
    var uiState by mutableStateOf(XxxUiState())
        private set
}
```

### UseCase

```kotlin
class XxxUseCase @Inject constructor(
    private val repository: XxxRepository,
) {
    suspend operator fun invoke(...): Result<T> = runCatching {
        repository.xxx(...)
    }
}
```

### 内部 NavHost のTopAppBar

HomeScreen 内の各タブ画面は二重インセットを避けるため必須設定:

```kotlin
TopAppBar(
    windowInsets = WindowInsets(0, 0, 0, 0),
    modifier = Modifier.height(48.dp),
)
```

## ビルド

```bash
./gradlew assembleDebug     # デバッグビルド
./gradlew :feature:xxx:assembleDebug  # 特定モジュールのみ
```

## カスタムスキル

| コマンド | 内容 |
|---|---|
| `/ship` | コミットメッセージ自動生成 → コミット → プッシュ → PRコメント |

## 注意事項

- `app/build.gradle.kts` には `core:data` / `core:network` / `core:datastore` が必要（Hilt コンポーネントグラフの組み立てのため）
- Compose BOM `2024.09.00` では `FlowRow` の `FlowRowOverflow` が未対応 → `Row` + `horizontalScroll` を使う
- 内部 NavHost の画面に `onBack` を渡す際は `HomeScreen` の `onBack` をそのまま転送する

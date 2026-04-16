# 起動時トークンチェック 設計書

## 目的

アプリ再起動のたびにログイン画面を表示するのではなく、
DataStore に保存済みのトークンがあれば子ども一覧に直接遷移する。

---

## 起動フロー

```
アプリ起動
  ↓
SplashScreen 表示（ロゴ + インジケーター）
  ↓
SplashViewModel が TokenDataStore.token.first() を取得
  ├── token あり → "children" へ遷移（Splash をバックスタックから除去）
  └── token なし → "auth" へ遷移（Splash をバックスタックから除去）
```

### トークン期限切れの扱い

- 起動時は「存在するか」だけを確認（ネットワーク検証なし）
- 期限切れトークンは最初の API 呼び出し時に 401 エラーで発覚する
- その場合は別途 `③ ログアウト` 機能と合わせて対応（今回のスコープ外）

---

## 変更影響範囲

| ファイル | 変更種別 | 内容 |
|---|---|---|
| `app/SplashViewModel.kt` | 新規 | トークン確認・遷移先の決定 |
| `app/SplashScreen.kt` | 新規 | ロゴ + ローディング表示 |
| `app/NavGraph.kt` | 変更 | startDestination を `"splash"` に変更、splash ルートを追加 |

---

## 新規ファイル詳細

### `app/SplashViewModel.kt`

```kotlin
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {

    sealed interface Destination {
        object Auth : Destination
        object Children : Destination
    }

    var destination by mutableStateOf<Destination?>(null)
        private set

    init {
        viewModelScope.launch {
            val token = tokenDataStore.token.first()
            destination = if (token != null) Destination.Children else Destination.Auth
        }
    }
}
```

- `TokenDataStore.token` は `Flow<String?>` なので `.first()` で初回値を1回だけ取得
- `mutableStateOf` で Compose と直接連携（StateFlow 不要）
- ViewModel init で即実行するため画面表示と並列で処理される

---

### `app/SplashScreen.kt`

```kotlin
@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToChildren: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val destination = viewModel.destination

    LaunchedEffect(destination) {
        when (destination) {
            SplashViewModel.Destination.Auth     -> onNavigateToAuth()
            SplashViewModel.Destination.Children -> onNavigateToChildren()
            null -> { /* 確認中 */ }
        }
    }

    // ロゴ + ローディング
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("学習管理アプリ", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }
    }
}
```

- `destination` が null の間はローディングを表示
- 決定次第 `LaunchedEffect` が即発火 → 画面遷移
- DataStore の読み取りは通常 数ms〜十数ms なので実質スプラッシュはほぼ一瞬

---

## 変更ファイル詳細

### `app/NavGraph.kt`

```
変更前:
  startDestination = "auth"

変更後:
  startDestination = "splash"
  + composable("splash") を追加
```

```kotlin
NavHost(navController = navController, startDestination = "splash") {

    composable("splash") {
        SplashScreen(
            onNavigateToAuth = {
                navController.navigate("auth") {
                    popUpTo("splash") { inclusive = true }
                }
            },
            onNavigateToChildren = {
                navController.navigate("children") {
                    popUpTo("splash") { inclusive = true }
                }
            },
        )
    }

    composable("auth") { /* 既存のまま */ }
    composable("children") { /* 既存のまま */ }
    // ... 以下既存ルートはそのまま
}
```

- `popUpTo("splash") { inclusive = true }` で Splash をバックスタックから除去
  → 戻るボタンで Splash に戻らない

---

## 依存関係

`app/build.gradle.kts` に追加が必要なもの：

```kotlin
implementation(project(":core:datastore"))  // ← 既に追加済み ✓
```

`SplashViewModel` は `TokenDataStore` を直接 inject するため
`core:datastore` モジュールへの依存が必要だが、すでに追加済みのため変更不要。

---

## 実装順序

1. `SplashViewModel.kt` 作成
2. `SplashScreen.kt` 作成
3. `NavGraph.kt` の startDestination を変更、splash ルートを追加
4. ビルド確認

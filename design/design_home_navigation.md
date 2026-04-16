# ボトムナビゲーション + 子ども切り替え 設計書

## 画面構成イメージ

```
┌──────────────────────────────┐
│ TopAppBar: [太郎 ▼]          │  ← タップで子ども切り替えダイアログ
├──────────────────────────────┤
│                              │
│      画面コンテンツ           │  ← Daily / Tasks / Summary が切り替わる
│                              │
├──────────────────────────────┤
│  📅 日々  │ 📋 タスク │ 📊集計  │  ← BottomNavBar
└──────────────────────────────┘
```

---

## ナビゲーション構造

### 外側 NavGraph（NavGraph.kt）

```
"auth"              → AuthScreen
                         ↓ ログイン成功
"children"          → ChildrenScreen（初回 or 子ども追加用）
                         ↓ 子どもを選択
"home/{childId}"    → HomeScreen（BottomNav ハブ）
```

### HomeScreen 内側 NavHost

```
startDestination: "daily/{childId}/{today}"

"daily/{childId}/{date}"  → DailyScreen    （日々の記録）
"tasks/{childId}"         → TasksScreen    （タスク管理）
"summary/{childId}"       → SummaryScreen  （集計）
```

- 既存の `feature:*` ViewModels は SavedStateHandle["childId"] を内側 NavHost から取得するため**変更不要**
- タブ切り替えは `saveState = true` / `restoreState = true` でスクロール位置・状態を保持

---

## 新規作成ファイル

### 1. `app/HomeScreen.kt`

```kotlin
@Composable
fun HomeScreen(
    childId: String,
    onNavigateToChildren: () -> Unit,         // ChildrenScreen へ戻る（子ども管理）
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val innerNavController = rememberNavController()
    val today = LocalDate.now().toString()

    Scaffold(
        topBar = {
            HomeTopAppBar(
                childName = uiState.selectedChildName,
                onChildClick = viewModel::onShowSwitcher,
                onManageChildren = onNavigateToChildren,
            )
        },
        bottomBar = {
            HomeBottomBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { tab ->
                    viewModel.onTabSelected(tab)
                    innerNavController.navigateToTab(tab, childId, today)
                },
            )
        },
    ) { padding ->
        NavHost(
            navController = innerNavController,
            startDestination = "daily/$childId/$today",
            modifier = Modifier.padding(padding),
        ) {
            composable("daily/{childId}/{date}") {
                DailyScreen(childId, date, onBack = {})
            }
            composable("tasks/{childId}") {
                TasksScreen(childId, onBack = {})
            }
            composable("summary/{childId}") {
                SummaryScreen(
                    childId = childId,
                    onBack = {},
                    onDaySelected = { date ->
                        innerNavController.navigate("daily/$childId/$date")
                    },
                )
            }
        }
    }

    // 子ども切り替えダイアログ
    if (uiState.showSwitcher) {
        ChildSwitcherDialog(
            children = uiState.children,
            currentChildId = childId,
            onSelect = { newChildId -> viewModel.onSelectChild(newChildId) },
            onDismiss = viewModel::onDismissSwitcher,
        )
    }
}
```

---

### 2. `app/HomeViewModel.kt`

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getChildrenUseCase: GetChildrenUseCase,
) : ViewModel() {

    private val childId: String = checkNotNull(savedStateHandle["childId"])

    var uiState by mutableStateOf(HomeUiState())
        private set

    init { loadChildren() }

    fun onShowSwitcher() { uiState = uiState.copy(showSwitcher = true) }
    fun onDismissSwitcher() { uiState = uiState.copy(showSwitcher = false) }

    fun onTabSelected(tab: HomeTab) {
        uiState = uiState.copy(selectedTab = tab)
    }

    // 子ども切り替えは外側 NavGraph に委譲（ callback 経由）
    // selectedChild の名前表示のみ HomeViewModel が担当
}
```

---

### 3. `app/HomeUiState.kt`

```kotlin
data class HomeUiState(
    val children: List<Child> = emptyList(),
    val selectedChildName: String = "",
    val showSwitcher: Boolean = false,
    val selectedTab: HomeTab = HomeTab.DAILY,
)

enum class HomeTab { DAILY, TASKS, SUMMARY }
```

---

## 変更ファイル

### `app/NavGraph.kt`

```
変更前:
  "children" → ChildrenScreen → navigate("daily/$childId/$today")
  composable("tasks/{childId}") → TasksScreen
  composable("daily/{childId}/{date}") → DailyScreen
  composable("summary/{childId}") → SummaryScreen

変更後:
  "children" → ChildrenScreen → navigate("home/$childId")
  composable("home/{childId}") → HomeScreen
  ※ tasks / daily / summary の外側ルートを削除
     （HomeScreen 内側 NavHost に移動）
```

### `app/build.gradle.kts`

```
追加:
  implementation(project(":core:domain"))   // HomeViewModel で GetChildrenUseCase を使用
  implementation(libs.androidx.compose.material.icons.extended)  // BottomBar アイコン
```

---

## タブ切り替えナビゲーション関数

```kotlin
private fun NavController.navigateToTab(tab: HomeTab, childId: String, today: String) {
    val route = when (tab) {
        HomeTab.DAILY   -> "daily/$childId/$today"
        HomeTab.TASKS   -> "tasks/$childId"
        HomeTab.SUMMARY -> "summary/$childId"
    }
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
```

---

## 子ども切り替えフロー

```
HomeScreen の「[太郎 ▼]」タップ
  → ChildSwitcherDialog 表示（子ども一覧）
  → 「花子」を選択
  → 外側 NavGraph で navigate("home/花子ID") {
        popUpTo("home/{childId}") { inclusive = true }
     }
  → HomeScreen が新しい childId で再構築
  → 内側 NavHost も Daily タブから再スタート
```

---

## 変更影響範囲

| ファイル | 変更種別 | 内容 |
|---|---|---|
| `app/NavGraph.kt` | 変更 | home ルート追加、children 遷移先変更、外側 daily/tasks/summary 削除 |
| `app/build.gradle.kts` | 変更 | core:domain 追加 |
| `app/HomeScreen.kt` | 新規 | BottomNav ハブ |
| `app/HomeViewModel.kt` | 新規 | 子ども一覧・切り替えダイアログ管理 |
| `app/HomeUiState.kt` | 新規 | HomeTab enum + UiState |
| `feature:daily/DailyScreen.kt` | 変更 | `onBack` が不要になるため引数削除（任意） |
| `feature:tasks/TasksScreen.kt` | 変更 | 同上（任意） |
| `feature:summary/SummaryScreen.kt` | 変更 | 同上（任意） |

feature 側の `onBack` は BottomNav 環境では使わないが、残してもビルドは通る。

---

## 実装順序

1. `HomeUiState.kt` 作成
2. `HomeViewModel.kt` 作成
3. `HomeScreen.kt` 作成（ChildSwitcherDialog 含む）
4. `app/build.gradle.kts` 更新
5. `NavGraph.kt` 更新
6. ビルド確認

# タスク並び替え機能 設計

## 概要

タスク一覧画面にドラッグ＆ドロップによる並び替え機能を追加する。
長押しでドラッグ開始、ドロップ完了後に即時 API 送信。

- **関連タスク**: B-2
- **UI 方式**: ドラッグ＆ドロップ（長押し起動）
- **API 送信タイミング**: ドロップ完了ごとに即時送信

---

## 使用ライブラリ

| ライブラリ | バージョン | 用途 |
|---|---|---|
| `sh.calvin.reorderable:reorderable` | 2.4.3 | LazyColumn のドラッグ＆ドロップ |

### libs.versions.toml への追記

```toml
[versions]
reorderable = "2.4.3"

[libraries]
reorderable = { group = "sh.calvin.reorderable", name = "reorderable", version.ref = "reorderable" }
```

---

## UI 仕様

### タスクカードの変更

各カードの左端にドラッグハンドル（☰）を追加。長押しでドラッグ開始。

```
┌─────────────────────────────────────┐
│ ☰  算数ドリル  30分  月〜金         │
│    教科: 算数              [✏][📦]  │
└─────────────────────────────────────┘
      ↕ 長押しでドラッグ
┌─────────────────────────────────────┐
│ ☰  英語リーディング  20分  月水金   │
│    教科: 英語              [✏][📦]  │
└─────────────────────────────────────┘
```

### 動作フロー

```
カードを長押し → ドラッグ開始（カードが浮き上がる）
  → 上下にドラッグして位置を変更
    → ドロップ（指を離す）
      → ローカルリストを即時更新（楽観的更新）
      → ReorderTasksUseCase を呼び出し
        → 成功: そのまま（リストは既に更新済み）
        → 失敗: 元の順序に戻す + Snackbar でエラー表示
```

---

## 修正ファイル一覧

### 1. `gradle/libs.versions.toml`

`reorderable` ライブラリを追加。

---

### 2. `feature/tasks/build.gradle.kts`

```kotlin
implementation(libs.reorderable)
```

---

### 3. `core/network/response/TaskDto.kt`

`sort_order` フィールドを追加。

```kotlin
data class TaskDto(
    // ...既存フィールド...
    @SerializedName("sort_order") val sortOrder: Int = 0,  // 追加
)
```

---

### 4. `core/model/Task.kt`

`sortOrder` フィールドを追加。

```kotlin
data class Task(
    // ...既存フィールド...
    val sortOrder: Int = 0,  // 追加
)
```

---

### 5. `core/data/mapper/TaskMapper.kt`

`sortOrder` のマッピングを追加。

```kotlin
fun TaskDto.toModel() = Task(
    // ...既存フィールド...
    sortOrder = sortOrder,  // 追加
)
```

---

### 6. `feature/tasks/TasksUiState.kt`

変更なし。`tasks: List<Task>` はそのまま使用。

---

### 7. `feature/tasks/TasksViewModel.kt`

- `ReorderTasksUseCase` を DI で注入
- `onMove(from: Int, to: Int)` メソッドを追加

```kotlin
// ViewModel 内
fun onMove(from: Int, to: Int) {
    val current = _uiState.value.tasks.toMutableList()
    val moved = current.removeAt(from)
    current.add(to, moved)
    // 楽観的更新
    _uiState.update { it.copy(tasks = current) }
    // API 送信
    viewModelScope.launch {
        val orders = current.mapIndexed { index, task -> task.id to index }
        reorderTasksUseCase(childId, orders)
            .onFailure {
                // 失敗時は再取得して元に戻す
                loadTasks()
                _uiState.update { it.copy(errorMessage = "並び替えに失敗しました") }
            }
    }
}
```

---

### 8. `feature/tasks/TasksScreen.kt`

- `ReorderableLazyColumn` + `rememberReorderableLazyListState` を使用
- ドラッグハンドルアイコン（`Icons.Filled.DragHandle`）を追加

---

## 実装順序

1. `libs.versions.toml` — ライブラリ追加
2. `feature/tasks/build.gradle.kts` — 依存追加
3. `core/network/response/TaskDto.kt` — sort_order 追加
4. `core/model/Task.kt` — sortOrder 追加
5. `core/data/mapper/TaskMapper.kt` — マッピング追加
6. `feature/tasks/TasksViewModel.kt` — ReorderTasksUseCase 注入・onMove 追加
7. `feature/tasks/TasksScreen.kt` — ドラッグ＆ドロップ UI 実装

---

**作成日**: 2026-04-18

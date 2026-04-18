# B-3 オフライン時のエラーメッセージ改善 設計書

## 概要

現状、ネットワーク切断時も「タスクの取得に失敗しました」のような汎用メッセージが表示される。
`IOException`（オフライン・タイムアウト）と HTTP エラーを区別し、ユーザーが原因を判断できるメッセージを表示する。

**関連タスク**: B-3

---

## 現状の問題

```
// 全 ViewModel で以下のパターン（例外型を無視）
.onFailure {
    _uiState.update { it.copy(errorMessage = "タスクの取得に失敗しました") }
}
```

- オフライン時も「取得に失敗しました」→ ユーザーは原因不明
- `IOException`（ネットワーク系）と `HttpException`（サーバー系）を区別していない
- 401 / 500 などの HTTP ステータスも区別なし

---

## 改善方針

### 例外の分類

| 例外クラス | 原因 | 表示メッセージ |
|---|---|---|
| `java.io.IOException` | オフライン・タイムアウト | `"ネットワークに接続できません。接続を確認してください。"` |
| `retrofit2.HttpException` (5xx) | サーバーエラー | `"サーバーエラーが発生しました。しばらくしてからお試しください。"` |
| `retrofit2.HttpException` (401) | 認証エラー | `"認証エラーが発生しました。再ログインしてください。"` |
| その他 | 不明なエラー | 既存の操作別メッセージをそのまま使用 |

### 設計方針

- **最小変更**: 各 ViewModel の `onFailure` ブロックを修正するだけ
- **共通ヘルパー**: `core/common` に `Throwable` 拡張関数を 1 つ追加
- **後方互換**: 分類できない例外は既存メッセージをフォールバックとして使用

---

## 実装内容

### 1. `core/common` に拡張関数を追加

**新規ファイル**: `core/common/src/main/java/com/learn/app/core/common/ThrowableExt.kt`

```kotlin
package com.learn.app.core.common

import retrofit2.HttpException
import java.io.IOException

fun Throwable.toErrorMessage(fallback: String): String = when {
    this is IOException ->
        "ネットワークに接続できません。接続を確認してください。"
    this is HttpException && code() >= 500 ->
        "サーバーエラーが発生しました。しばらくしてからお試しください。"
    this is HttpException && code() == 401 ->
        "認証エラーが発生しました。再ログインしてください。"
    else -> fallback
}
```

`core/common/build.gradle.kts` に retrofit 依存を追加:
```kotlin
implementation(libs.retrofit)
```

### 2. 修正する ViewModel

| ViewModel | 修正箇所 |
|---|---|
| `TasksViewModel` | `loadTasks`, `onSaveTask`, `onArchiveTask`, `onMove` の `onFailure` |
| `DailyViewModel` | `loadDailyView`, `updateDailyLog` の `onFailure` |
| `ChildrenViewModel` | `loadChildren`, `onSaveChild`, `onDeleteChild` の `onFailure` |
| `SummaryViewModel` | `loadMonth` の `onFailure` |

### 修正例（TasksViewModel）

```kotlin
// Before
.onFailure {
    _uiState.update { it.copy(isLoading = false, errorMessage = "タスクの取得に失敗しました") }
}

// After
.onFailure { throwable ->
    _uiState.update {
        it.copy(
            isLoading = false,
            errorMessage = throwable.toErrorMessage("タスクの取得に失敗しました"),
        )
    }
}
```

---

## 修正ファイル一覧

1. `core/common/src/main/java/com/learn/app/core/common/ThrowableExt.kt` — 新規作成
2. `core/common/build.gradle.kts` — retrofit 依存追加
3. `feature/tasks/src/main/java/com/learn/app/feature/tasks/TasksViewModel.kt`
4. `feature/daily/src/main/java/com/learn/app/feature/daily/DailyViewModel.kt`
5. `feature/children/src/main/java/com/learn/app/feature/children/ChildrenViewModel.kt`
6. `feature/summary/src/main/java/com/learn/app/feature/summary/SummaryViewModel.kt`

---

## 対象外（変更しない）

- `AuthViewModel`: 既に独自のメッセージマッピングあり。今回は触らない。
- UI 表示層（Screen.kt）: Snackbar の仕組みはそのまま

---

**作成日**: 2026-04-18

# アカウント削除機能 Android 実装設計

## 概要

Play Store 審査要件に対応するため、アプリ内からアカウントを削除できる機能を追加する。
バックエンドの `DELETE /api/v1/me` エンドポイントを呼び出し、全データを削除してログアウトする。

- **関連タスク**: B-1
- **参照**: `design/plan_delete_account.md`

---

## UI 仕様

### 配置場所

`HomeScreen` の右上 `MoreVert`（三点リーダー）メニューに「アカウントを削除」を追加。

```
[ 子ども名 ▼ ]          [ ログアウト ] [ ⋮ ]
                                        ├ プライバシーポリシー  ← 既存
                                        └ アカウントを削除     ← 新規追加
```

### 確認ダイアログ

メニュー選択後に AlertDialog を表示。

```
┌─────────────────────────┐
│ アカウントを削除         │
│                         │
│ アカウントを削除すると、 │
│ すべてのデータが削除され │
│ 復元できません。         │
│                         │
│ [キャンセル]  [削除する] │
└─────────────────────────┘
```

### 処理フロー

```
メニュー「アカウントを削除」タップ
  → 確認ダイアログ表示
    → [削除する] タップ
      → DELETE /api/v1/me（API 呼び出し）
        → 成功: トークン削除 → AuthScreen へ遷移（onLoggedOut）
        → 失敗: エラーダイアログ表示（「削除に失敗しました」）
```

---

## 修正ファイル一覧

### 1. `core/network/.../LearnApiService.kt`

```kotlin
// Auth セクションに追加
@DELETE("api/v1/me")
suspend fun deleteAccount()
```

---

### 2. `core/domain/.../AuthRepository.kt`

```kotlin
interface AuthRepository {
    suspend fun login(email: String, password: String): String
    suspend fun signup(email: String, password: String)
    suspend fun getMe(): User
    suspend fun logout()
    suspend fun deleteAccount()   // 追加
}
```

---

### 3. `core/data/.../AuthRepositoryImpl.kt`

```kotlin
override suspend fun deleteAccount() {
    api.deleteAccount()
    tokenDataStore.clearToken()
}
```

---

### 4. `core/domain/usecase/DeleteAccountUseCase.kt`（新規作成）

```kotlin
class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        runCatching { authRepository.deleteAccount() }
}
```

---

### 5. `feature/home/.../HomeUiState.kt`

```kotlin
data class HomeUiState(
    // ...既存フィールド...
    val showDeleteAccountConfirm: Boolean = false,  // 追加
    val deleteAccountError: Boolean = false,         // 追加
)
```

---

### 6. `feature/home/.../HomeViewModel.kt`

- `DeleteAccountUseCase` を DI で注入
- 以下のメソッドを追加:

```kotlin
fun onShowDeleteAccountConfirm() {
    _uiState.update { it.copy(showDeleteAccountConfirm = true) }
}
fun onDismissDeleteAccountConfirm() {
    _uiState.update { it.copy(showDeleteAccountConfirm = false) }
}
fun onDeleteAccount(onSuccess: () -> Unit) {
    viewModelScope.launch {
        deleteAccountUseCase()
            .onSuccess { onSuccess() }
            .onFailure {
                _uiState.update {
                    it.copy(showDeleteAccountConfirm = false, deleteAccountError = true)
                }
            }
    }
}
fun onDismissDeleteAccountError() {
    _uiState.update { it.copy(deleteAccountError = false) }
}
```

---

### 7. `feature/home/.../HomeScreen.kt`

- `DropdownMenu` に「アカウントを削除」メニュー項目を追加
- `HomeContent` の引数に `onShowDeleteAccountConfirm`, `onDeleteAccount`, `onDismissDeleteAccountConfirm`, `onDismissDeleteAccountError` を追加
- 確認ダイアログ・エラーダイアログを追加

---

## 実装順序

1. `LearnApiService.kt` — API 追加
2. `AuthRepository.kt` — インターフェース追加
3. `AuthRepositoryImpl.kt` — 実装追加
4. `DeleteAccountUseCase.kt` — 新規作成
5. `HomeUiState.kt` — フィールド追加
6. `HomeViewModel.kt` — メソッド追加
7. `HomeScreen.kt` — UI 追加

---

**作成日**: 2026-04-18

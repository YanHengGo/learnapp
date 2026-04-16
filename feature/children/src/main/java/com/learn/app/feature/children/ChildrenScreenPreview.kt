package com.learn.app.feature.children

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.learn.app.core.model.Child

// ── プレビュー用ダミーデータ ──────────────────────────────────────────────

private val previewChildren = listOf(
    Child(id = "1", name = "長男", grade = "小学3年生", isActive = true),
    Child(id = "2", name = "次男", grade = null, isActive = false),
)

private val emptyCallbacks = object {
    val onChildSelected: (String) -> Unit = {}
    val onLoggedOut: () -> Unit = {}
    val onShowAddDialog: () -> Unit = {}
    val onShowEditDialog: (Child) -> Unit = {}
    val onDeleteChild: (Child) -> Unit = {}
    val onShowLogoutConfirm: () -> Unit = {}
    val onDismissLogoutConfirm: () -> Unit = {}
    val onLogout: () -> Unit = {}
    val onDismissDialog: () -> Unit = {}
    val onNameChange: (String) -> Unit = {}
    val onGradeChange: (String) -> Unit = {}
    val onSaveChild: () -> Unit = {}
    val onErrorDismiss: () -> Unit = {}
    val onLoadChildren: () -> Unit = {}
    val onPrivacyPolicy: () -> Unit = {}
}

// ── プレビュー関数 ────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "ローディング中")
@Composable
private fun PreviewLoading() {
    MaterialTheme {
        ChildrenContent(
            uiState = ChildrenUiState(isLoading = true),
            onChildSelected = emptyCallbacks.onChildSelected,
            onLoggedOut = emptyCallbacks.onLoggedOut,
            onShowAddDialog = emptyCallbacks.onShowAddDialog,
            onShowEditDialog = emptyCallbacks.onShowEditDialog,
            onDeleteChild = emptyCallbacks.onDeleteChild,
            onShowLogoutConfirm = emptyCallbacks.onShowLogoutConfirm,
            onDismissLogoutConfirm = emptyCallbacks.onDismissLogoutConfirm,
            onLogout = emptyCallbacks.onLogout,
            onDismissDialog = emptyCallbacks.onDismissDialog,
            onNameChange = emptyCallbacks.onNameChange,
            onGradeChange = emptyCallbacks.onGradeChange,
            onSaveChild = emptyCallbacks.onSaveChild,
            onErrorDismiss = emptyCallbacks.onErrorDismiss,
            onLoadChildren = emptyCallbacks.onLoadChildren,
        )
    }
}

@Preview(showBackground = true, name = "空リスト")
@Composable
private fun PreviewEmpty() {
    MaterialTheme {
        ChildrenContent(
            uiState = ChildrenUiState(children = emptyList()),
            onChildSelected = emptyCallbacks.onChildSelected,
            onLoggedOut = emptyCallbacks.onLoggedOut,
            onShowAddDialog = emptyCallbacks.onShowAddDialog,
            onShowEditDialog = emptyCallbacks.onShowEditDialog,
            onDeleteChild = emptyCallbacks.onDeleteChild,
            onShowLogoutConfirm = emptyCallbacks.onShowLogoutConfirm,
            onDismissLogoutConfirm = emptyCallbacks.onDismissLogoutConfirm,
            onLogout = emptyCallbacks.onLogout,
            onDismissDialog = emptyCallbacks.onDismissDialog,
            onNameChange = emptyCallbacks.onNameChange,
            onGradeChange = emptyCallbacks.onGradeChange,
            onSaveChild = emptyCallbacks.onSaveChild,
            onErrorDismiss = emptyCallbacks.onErrorDismiss,
            onLoadChildren = emptyCallbacks.onLoadChildren,
        )
    }
}

@Preview(showBackground = true, name = "読み込みエラー")
@Composable
private fun PreviewLoadError() {
    MaterialTheme {
        ChildrenContent(
            uiState = ChildrenUiState(isLoadError = true),
            onChildSelected = emptyCallbacks.onChildSelected,
            onLoggedOut = emptyCallbacks.onLoggedOut,
            onShowAddDialog = emptyCallbacks.onShowAddDialog,
            onShowEditDialog = emptyCallbacks.onShowEditDialog,
            onDeleteChild = emptyCallbacks.onDeleteChild,
            onShowLogoutConfirm = emptyCallbacks.onShowLogoutConfirm,
            onDismissLogoutConfirm = emptyCallbacks.onDismissLogoutConfirm,
            onLogout = emptyCallbacks.onLogout,
            onDismissDialog = emptyCallbacks.onDismissDialog,
            onNameChange = emptyCallbacks.onNameChange,
            onGradeChange = emptyCallbacks.onGradeChange,
            onSaveChild = emptyCallbacks.onSaveChild,
            onErrorDismiss = emptyCallbacks.onErrorDismiss,
            onLoadChildren = emptyCallbacks.onLoadChildren,
        )
    }
}

@Preview(showBackground = true, name = "子ども一覧")
@Composable
private fun PreviewList() {
    MaterialTheme {
        ChildrenContent(
            uiState = ChildrenUiState(children = previewChildren),
            onChildSelected = emptyCallbacks.onChildSelected,
            onLoggedOut = emptyCallbacks.onLoggedOut,
            onShowAddDialog = emptyCallbacks.onShowAddDialog,
            onShowEditDialog = emptyCallbacks.onShowEditDialog,
            onDeleteChild = emptyCallbacks.onDeleteChild,
            onShowLogoutConfirm = emptyCallbacks.onShowLogoutConfirm,
            onDismissLogoutConfirm = emptyCallbacks.onDismissLogoutConfirm,
            onLogout = emptyCallbacks.onLogout,
            onDismissDialog = emptyCallbacks.onDismissDialog,
            onNameChange = emptyCallbacks.onNameChange,
            onGradeChange = emptyCallbacks.onGradeChange,
            onSaveChild = emptyCallbacks.onSaveChild,
            onErrorDismiss = emptyCallbacks.onErrorDismiss,
            onLoadChildren = emptyCallbacks.onLoadChildren,
        )
    }
}

@Preview(showBackground = true, name = "ログアウト確認ダイアログ")
@Composable
private fun PreviewLogoutDialog() {
    MaterialTheme {
        ChildrenContent(
            uiState = ChildrenUiState(
                children = previewChildren,
                showLogoutConfirm = true,
            ),
            onChildSelected = emptyCallbacks.onChildSelected,
            onLoggedOut = emptyCallbacks.onLoggedOut,
            onShowAddDialog = emptyCallbacks.onShowAddDialog,
            onShowEditDialog = emptyCallbacks.onShowEditDialog,
            onDeleteChild = emptyCallbacks.onDeleteChild,
            onShowLogoutConfirm = emptyCallbacks.onShowLogoutConfirm,
            onDismissLogoutConfirm = emptyCallbacks.onDismissLogoutConfirm,
            onLogout = emptyCallbacks.onLogout,
            onDismissDialog = emptyCallbacks.onDismissDialog,
            onNameChange = emptyCallbacks.onNameChange,
            onGradeChange = emptyCallbacks.onGradeChange,
            onSaveChild = emptyCallbacks.onSaveChild,
            onErrorDismiss = emptyCallbacks.onErrorDismiss,
            onLoadChildren = emptyCallbacks.onLoadChildren,
        )
    }
}

@Preview(showBackground = true, name = "子ども追加ダイアログ")
@Composable
private fun PreviewAddDialog() {
    MaterialTheme {
        ChildrenContent(
            uiState = ChildrenUiState(showAddDialog = true),
            onChildSelected = emptyCallbacks.onChildSelected,
            onLoggedOut = emptyCallbacks.onLoggedOut,
            onShowAddDialog = emptyCallbacks.onShowAddDialog,
            onShowEditDialog = emptyCallbacks.onShowEditDialog,
            onDeleteChild = emptyCallbacks.onDeleteChild,
            onShowLogoutConfirm = emptyCallbacks.onShowLogoutConfirm,
            onDismissLogoutConfirm = emptyCallbacks.onDismissLogoutConfirm,
            onLogout = emptyCallbacks.onLogout,
            onDismissDialog = emptyCallbacks.onDismissDialog,
            onNameChange = emptyCallbacks.onNameChange,
            onGradeChange = emptyCallbacks.onGradeChange,
            onSaveChild = emptyCallbacks.onSaveChild,
            onErrorDismiss = emptyCallbacks.onErrorDismiss,
            onLoadChildren = emptyCallbacks.onLoadChildren,
        )
    }
}

@Preview(showBackground = true, name = "子ども編集ダイアログ")
@Composable
private fun PreviewEditDialog() {
    MaterialTheme {
        ChildrenContent(
            uiState = ChildrenUiState(
                children = previewChildren,
                editingChild = previewChildren.first(),
                dialogName = "長男",
                dialogGrade = "小学3年生",
            ),
            onChildSelected = emptyCallbacks.onChildSelected,
            onLoggedOut = emptyCallbacks.onLoggedOut,
            onShowAddDialog = emptyCallbacks.onShowAddDialog,
            onShowEditDialog = emptyCallbacks.onShowEditDialog,
            onDeleteChild = emptyCallbacks.onDeleteChild,
            onShowLogoutConfirm = emptyCallbacks.onShowLogoutConfirm,
            onDismissLogoutConfirm = emptyCallbacks.onDismissLogoutConfirm,
            onLogout = emptyCallbacks.onLogout,
            onDismissDialog = emptyCallbacks.onDismissDialog,
            onNameChange = emptyCallbacks.onNameChange,
            onGradeChange = emptyCallbacks.onGradeChange,
            onSaveChild = emptyCallbacks.onSaveChild,
            onErrorDismiss = emptyCallbacks.onErrorDismiss,
            onLoadChildren = emptyCallbacks.onLoadChildren,
        )
    }
}

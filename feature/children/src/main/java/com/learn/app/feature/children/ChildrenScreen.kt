package com.learn.app.feature.children

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learn.app.core.model.Child

@Composable
fun ChildrenScreen(
    onChildSelected: (String) -> Unit,
    onLoggedOut: () -> Unit,
    onPrivacyPolicy: () -> Unit = {},
    viewModel: ChildrenViewModel = hiltViewModel(),
) {
    ChildrenContent(
        uiState = viewModel.uiState.collectAsState().value,
        onChildSelected = onChildSelected,
        onLoggedOut = onLoggedOut,
        onPrivacyPolicy = onPrivacyPolicy,
        onShowAddDialog = viewModel::onShowAddDialog,
        onShowEditDialog = viewModel::onShowEditDialog,
        onDeleteChild = viewModel::onDeleteChild,
        onShowLogoutConfirm = viewModel::onShowLogoutConfirm,
        onDismissLogoutConfirm = viewModel::onDismissLogoutConfirm,
        onLogout = { viewModel.onLogout(onLoggedOut) },
        onDismissDialog = viewModel::onDismissDialog,
        onNameChange = viewModel::onDialogNameChange,
        onGradeChange = viewModel::onDialogGradeChange,
        onSaveChild = viewModel::onSaveChild,
        onErrorDismiss = viewModel::onErrorDismiss,
        onLoadChildren = viewModel::loadChildren,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChildrenContent(
    uiState: ChildrenUiState,
    onChildSelected: (String) -> Unit,
    onLoggedOut: () -> Unit,
    onShowAddDialog: () -> Unit,
    onShowEditDialog: (Child) -> Unit,
    onDeleteChild: (Child) -> Unit,
    onShowLogoutConfirm: () -> Unit,
    onDismissLogoutConfirm: () -> Unit,
    onLogout: () -> Unit,
    onDismissDialog: () -> Unit,
    onNameChange: (String) -> Unit,
    onGradeChange: (String) -> Unit,
    onSaveChild: () -> Unit,
    onErrorDismiss: () -> Unit,
    onLoadChildren: () -> Unit,
    onPrivacyPolicy: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onErrorDismiss()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("子ども一覧") },
                actions = {
                    IconButton(
                        onClick = onShowLogoutConfirm,
                        modifier = Modifier.testTag("logoutButton"),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "ログアウト")
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "メニュー")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("プライバシーポリシー") },
                            onClick = {
                                menuExpanded = false
                                onPrivacyPolicy()
                            },
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowAddDialog,
                modifier = Modifier.testTag("addChildButton"),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "子どもを追加")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.isLoadError && uiState.children.isEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "データの取得に失敗しました",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    androidx.compose.material3.Button(onClick = onLoadChildren) {
                        Text("再読み込み")
                    }
                }
            } else if (uiState.children.isEmpty()) {
                Text(
                    text = "子どもが登録されていません\n右下のボタンから追加してください",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.children, key = { it.id }) { child ->
                        ChildCard(
                            child = child,
                            onClick = { onChildSelected(child.id) },
                            onEdit = { onShowEditDialog(child) },
                            onDelete = { onDeleteChild(child) },
                        )
                    }
                }
            }
        }
    }

    if (uiState.showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = onDismissLogoutConfirm,
            title = { Text("ログアウト") },
            text = { Text("ログアウトしますか？") },
            confirmButton = {
                TextButton(onClick = onLogout) {
                    Text("ログアウト")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissLogoutConfirm) {
                    Text("キャンセル")
                }
            },
        )
    }

    if (uiState.showAddDialog || uiState.editingChild != null) {
        ChildDialog(
            title = if (uiState.editingChild != null) "子どもを編集" else "子どもを追加",
            name = uiState.dialogName,
            grade = uiState.dialogGrade,
            isSaving = uiState.isSaving,
            onNameChange = onNameChange,
            onGradeChange = onGradeChange,
            onConfirm = onSaveChild,
            onDismiss = onDismissDialog,
        )
    }
}

@Composable
private fun ChildCard(
    child: Child,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("childCard"),
        colors = CardDefaults.cardColors(
            containerColor = if (child.isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = child.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (child.grade != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = child.grade ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "編集")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "削除", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ChildDialog(
    title: String,
    name: String,
    grade: String,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onGradeChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("名前") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = grade,
                    onValueChange = onGradeChange,
                    label = { Text("学年（任意）") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = name.isNotBlank() && !isSaving,
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("キャンセル")
            }
        },
    )
}

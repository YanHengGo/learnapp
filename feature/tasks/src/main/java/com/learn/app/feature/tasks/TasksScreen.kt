package com.learn.app.feature.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.learn.app.core.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    childId: String,
    onBack: () -> Unit,
    viewModel: TasksViewModel = hiltViewModel(),
) {
    TasksContent(
        uiState = viewModel.uiState.collectAsState().value,
        onBack = onBack,
        onShowAddDialog = viewModel::onShowAddDialog,
        onShowEditDialog = viewModel::onShowEditDialog,
        onArchiveTask = viewModel::onArchiveTask,
        onMove = viewModel::onMove,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onSubjectChange = viewModel::onSubjectChange,
        onMinutesChange = viewModel::onMinutesChange,
        onDayToggle = viewModel::onDayToggle,
        onStartDateChange = viewModel::onStartDateChange,
        onEndDateChange = viewModel::onEndDateChange,
        onSaveTask = viewModel::onSaveTask,
        onDismissDialog = viewModel::onDismissDialog,
        onErrorDismiss = viewModel::onErrorDismiss,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TasksContent(
    uiState: TasksUiState,
    onBack: () -> Unit,
    onShowAddDialog: () -> Unit,
    onShowEditDialog: (Task) -> Unit,
    onArchiveTask: (Task) -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubjectChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onDayToggle: (Int) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onSaveTask: () -> Unit,
    onDismissDialog: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onErrorDismiss()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text("タスク管理")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier.height(48.dp),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowAddDialog,
                modifier = Modifier.testTag("addTaskButton"),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "タスクを追加")
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
            } else if (uiState.tasks.isEmpty()) {
                Text(
                    text = "タスクが登録されていません\n右下のボタンから追加してください",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                )
            } else {
                val lazyListState = rememberLazyListState()
                val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
                    onMove(from.index, to.index)
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(uiState.tasks, key = { _, task -> task.id }) { _, task ->
                        ReorderableItem(reorderableState, key = task.id) {
                            TaskCard(
                                task = task,
                                onEdit = { onShowEditDialog(task) },
                                onArchive = { onArchiveTask(task) },
                                dragHandle = {
                                    Icon(
                                        imageVector = Icons.Filled.DragHandle,
                                        contentDescription = "並び替え",
                                        modifier = Modifier.draggableHandle(),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showDialog) {
        TaskDialog(
            uiState = uiState,
            onNameChange = onNameChange,
            onDescriptionChange = onDescriptionChange,
            onSubjectChange = onSubjectChange,
            onMinutesChange = onMinutesChange,
            onDayToggle = onDayToggle,
            onStartDateChange = onStartDateChange,
            onEndDateChange = onEndDateChange,
            onConfirm = onSaveTask,
            onDismiss = onDismissDialog,
        )
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    dragHandle: @Composable () -> Unit = {},
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            dragHandle()
            Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${task.subject}  ・  ${task.defaultMinutes}分  ・  ${daysLabel(task.daysMask)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (task.startDate != null || task.endDate != null) {
                    val period = listOfNotNull(task.startDate, task.endDate).joinToString(" 〜 ")
                    Text(
                        text = period,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "編集")
            }
            IconButton(onClick = onArchive) {
                Icon(Icons.Filled.Archive, contentDescription = "アーカイブ", tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

private fun daysLabel(mask: Int): String {
    return DAY_LABELS.filterIndexed { i, _ -> mask.hasDayBit(i) }.joinToString("")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDialog(
    uiState: TasksUiState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubjectChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onDayToggle: (Int) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val isValid = uiState.dialogName.isNotBlank() &&
        uiState.dialogSubject.isNotBlank() &&
        (uiState.dialogMinutes.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (uiState.editingTask != null) "タスクを編集" else "タスクを追加") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = uiState.dialogName,
                    onValueChange = onNameChange,
                    label = { Text("タスク名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving,
                )
                OutlinedTextField(
                    value = uiState.dialogSubject,
                    onValueChange = onSubjectChange,
                    label = { Text("教科") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving,
                )
                OutlinedTextField(
                    value = uiState.dialogMinutes,
                    onValueChange = onMinutesChange,
                    label = { Text("標準時間（分）") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving,
                )
                OutlinedTextField(
                    value = uiState.dialogDescription,
                    onValueChange = onDescriptionChange,
                    label = { Text("メモ（任意）") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving,
                )
                Text("曜日", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                ) {
                    DAY_LABELS.forEachIndexed { index, label ->
                        FilterChip(
                            selected = uiState.dialogDaysMask.hasDayBit(index),
                            onClick = { onDayToggle(index) },
                            label = { Text(label) },
                            enabled = !uiState.isSaving,
                        )
                    }
                }
                OutlinedTextField(
                    value = uiState.dialogStartDate,
                    onValueChange = onStartDateChange,
                    label = { Text("開始日（任意）yyyy-MM-dd") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving,
                )
                OutlinedTextField(
                    value = uiState.dialogEndDate,
                    onValueChange = onEndDateChange,
                    label = { Text("終了日（任意）yyyy-MM-dd") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = isValid && !uiState.isSaving,
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !uiState.isSaving) {
                Text("キャンセル")
            }
        },
    )
}

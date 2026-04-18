package com.learn.app.feature.tasks

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.learn.app.core.model.Task

private val previewTasks = listOf(
    Task(
        id = "1",
        name = "算数ドリル",
        description = null,
        subject = "算数",
        defaultMinutes = 30,
        daysMask = 0b0111110, // 月〜金
        isArchived = false,
        startDate = null,
        endDate = null,
    ),
    Task(
        id = "2",
        name = "英語リスニング",
        description = "教材p.10〜20",
        subject = "英語",
        defaultMinutes = 20,
        daysMask = 0b1000001, // 土・日
        isArchived = false,
        startDate = "2026-04-01",
        endDate = "2026-06-30",
    ),
)

@Preview(showBackground = true, name = "ローディング中")
@Composable
private fun PreviewLoading() {
    MaterialTheme {
        TasksContent(
            uiState = TasksUiState(isLoading = true),
            onBack = {},
            onShowAddDialog = {},
            onShowEditDialog = {},
            onArchiveTask = {},
            onMove = { _, _ -> },
            onNameChange = {},
            onDescriptionChange = {},
            onSubjectChange = {},
            onMinutesChange = {},
            onDayToggle = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSaveTask = {},
            onDismissDialog = {},
            onErrorDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "タスクなし（空）")
@Composable
private fun PreviewEmpty() {
    MaterialTheme {
        TasksContent(
            uiState = TasksUiState(tasks = emptyList()),
            onBack = {},
            onShowAddDialog = {},
            onShowEditDialog = {},
            onArchiveTask = {},
            onMove = { _, _ -> },
            onNameChange = {},
            onDescriptionChange = {},
            onSubjectChange = {},
            onMinutesChange = {},
            onDayToggle = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSaveTask = {},
            onDismissDialog = {},
            onErrorDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "タスク一覧")
@Composable
private fun PreviewList() {
    MaterialTheme {
        TasksContent(
            uiState = TasksUiState(tasks = previewTasks),
            onBack = {},
            onShowAddDialog = {},
            onShowEditDialog = {},
            onArchiveTask = {},
            onMove = { _, _ -> },
            onNameChange = {},
            onDescriptionChange = {},
            onSubjectChange = {},
            onMinutesChange = {},
            onDayToggle = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSaveTask = {},
            onDismissDialog = {},
            onErrorDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "タスク追加ダイアログ")
@Composable
private fun PreviewAddDialog() {
    MaterialTheme {
        TasksContent(
            uiState = TasksUiState(showDialog = true, editingTask = null),
            onBack = {},
            onShowAddDialog = {},
            onShowEditDialog = {},
            onArchiveTask = {},
            onMove = { _, _ -> },
            onNameChange = {},
            onDescriptionChange = {},
            onSubjectChange = {},
            onMinutesChange = {},
            onDayToggle = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSaveTask = {},
            onDismissDialog = {},
            onErrorDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "タスク編集ダイアログ")
@Composable
private fun PreviewEditDialog() {
    MaterialTheme {
        TasksContent(
            uiState = TasksUiState(
                tasks = previewTasks,
                showDialog = true,
                editingTask = previewTasks.first(),
                dialogName = "算数ドリル",
                dialogSubject = "算数",
                dialogMinutes = "30",
                dialogDaysMask = 0b0111110,
            ),
            onBack = {},
            onShowAddDialog = {},
            onShowEditDialog = {},
            onArchiveTask = {},
            onMove = { _, _ -> },
            onNameChange = {},
            onDescriptionChange = {},
            onSubjectChange = {},
            onMinutesChange = {},
            onDayToggle = {},
            onStartDateChange = {},
            onEndDateChange = {},
            onSaveTask = {},
            onDismissDialog = {},
            onErrorDismiss = {},
        )
    }
}

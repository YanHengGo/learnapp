package com.learn.app.feature.daily

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

private val previewTaskRows = listOf(
    DailyTaskRow(
        taskId = "1",
        name = "算数ドリル",
        subject = "算数",
        defaultMinutes = 30,
        isDone = true,
        minutes = "30",
    ),
    DailyTaskRow(
        taskId = "2",
        name = "英語リスニング",
        subject = "英語",
        defaultMinutes = 20,
        isDone = false,
        minutes = "",
    ),
)

@Preview(showBackground = true, name = "ローディング中")
@Composable
private fun PreviewLoading() {
    MaterialTheme {
        DailyContent(
            uiState = DailyUiState(isLoading = true),
            onBack = {},
            onPreviousDate = {},
            onNextDate = {},
            onToggleDone = {},
            onMinutesChange = { _, _ -> },
            onSave = {},
            onErrorDismiss = {},
            onSaveSuccessDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "記録なし（空）")
@Composable
private fun PreviewEmpty() {
    MaterialTheme {
        DailyContent(
            uiState = DailyUiState(
                date = "2026-04-17",
                weekday = "木",
                taskRows = emptyList(),
            ),
            onBack = {},
            onPreviousDate = {},
            onNextDate = {},
            onToggleDone = {},
            onMinutesChange = { _, _ -> },
            onSave = {},
            onErrorDismiss = {},
            onSaveSuccessDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "記録あり")
@Composable
private fun PreviewWithRecords() {
    MaterialTheme {
        DailyContent(
            uiState = DailyUiState(
                date = "2026-04-17",
                weekday = "木",
                taskRows = previewTaskRows,
            ),
            onBack = {},
            onPreviousDate = {},
            onNextDate = {},
            onToggleDone = {},
            onMinutesChange = { _, _ -> },
            onSave = {},
            onErrorDismiss = {},
            onSaveSuccessDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "保存中")
@Composable
private fun PreviewSaving() {
    MaterialTheme {
        DailyContent(
            uiState = DailyUiState(
                date = "2026-04-17",
                weekday = "木",
                taskRows = previewTaskRows,
                isSaving = true,
            ),
            onBack = {},
            onPreviousDate = {},
            onNextDate = {},
            onToggleDone = {},
            onMinutesChange = { _, _ -> },
            onSave = {},
            onErrorDismiss = {},
            onSaveSuccessDismiss = {},
        )
    }
}

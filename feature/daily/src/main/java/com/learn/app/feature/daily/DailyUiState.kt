package com.learn.app.feature.daily

data class DailyUiState(
    val date: String = "",
    val weekday: String = "",
    val taskRows: List<DailyTaskRow> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
)

data class DailyTaskRow(
    val taskId: String,
    val name: String,
    val subject: String,
    val defaultMinutes: Int,
    val isDone: Boolean,
    val minutes: String,
)

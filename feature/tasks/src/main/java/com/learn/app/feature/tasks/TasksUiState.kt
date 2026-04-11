package com.learn.app.feature.tasks

import com.learn.app.core.model.Task

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDialog: Boolean = false,
    val editingTask: Task? = null,
    val dialogName: String = "",
    val dialogDescription: String = "",
    val dialogSubject: String = "",
    val dialogMinutes: String = "30",
    val dialogDaysMask: Int = 0b0111110, // 月〜金
    val dialogStartDate: String = "",
    val dialogEndDate: String = "",
    val isSaving: Boolean = false,
)

// 曜日ビットマスク: bit0=日, bit1=月, bit2=火, bit3=水, bit4=木, bit5=金, bit6=土
val DAY_LABELS = listOf("日", "月", "火", "水", "木", "金", "土")

fun Int.hasDayBit(dayIndex: Int): Boolean = (this and (1 shl dayIndex)) != 0

fun Int.toggleDayBit(dayIndex: Int): Int = this xor (1 shl dayIndex)

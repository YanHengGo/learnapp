package com.learn.app.core.model

data class CalendarSummary(
    val from: String,
    val to: String,
    val days: List<CalendarDay>,
)

data class CalendarDay(
    val date: String,
    val status: CalendarStatus,
    val total: Int,
    val done: Int,
)

enum class CalendarStatus {
    GREEN,
    YELLOW,
    RED,
    WHITE,
}

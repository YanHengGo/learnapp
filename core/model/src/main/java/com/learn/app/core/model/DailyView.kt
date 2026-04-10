package com.learn.app.core.model

data class DailyView(
    val date: String,
    val weekday: String,
    val tasks: List<DailyTask>,
)

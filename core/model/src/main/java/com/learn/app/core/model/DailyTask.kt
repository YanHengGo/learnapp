package com.learn.app.core.model

data class DailyTask(
    val taskId: String,
    val name: String,
    val subject: String,
    val defaultMinutes: Int,
    val daysMask: Int,
    val isDone: Boolean,
    val minutes: Int,
)

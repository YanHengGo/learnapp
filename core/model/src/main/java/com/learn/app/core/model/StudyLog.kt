package com.learn.app.core.model

data class StudyLog(
    val id: String,
    val childId: String,
    val taskId: String,
    val date: String,
    val minutes: Int,
)

package com.learn.app.core.model

data class Task(
    val id: String,
    val childId: String,
    val name: String,
    val subject: String,
    val defaultMinutes: Int,
    val daysMask: Int,
    val sortOrder: Int,
    val isArchived: Boolean,
    val startDate: String?,
    val endDate: String?,
)

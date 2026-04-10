package com.learn.app.core.model

data class Task(
    val id: String,
    val name: String,
    val description: String?,
    val subject: String,
    val defaultMinutes: Int,
    val daysMask: Int,
    val isArchived: Boolean,
    val startDate: String?,
    val endDate: String?,
)

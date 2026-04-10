package com.learn.app.core.network

import com.google.gson.annotations.SerializedName

data class TokenResponse(val token: String)

data class UserResponse(
    val id: String,
    val email: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val provider: String,
)

data class ChildResponse(
    val id: String,
    val name: String,
    val grade: String?,
    @SerializedName("is_active") val isActive: Boolean,
)

data class ChildrenResponse(val children: List<ChildResponse>)

data class TaskResponse(
    val id: String,
    @SerializedName("child_id") val childId: String,
    val name: String,
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("sort_order") val sortOrder: Int,
    @SerializedName("is_archived") val isArchived: Boolean,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
)

data class TasksResponse(val tasks: List<TaskResponse>)

data class StudyLogResponse(
    val id: String,
    @SerializedName("child_id") val childId: String,
    @SerializedName("task_id") val taskId: String,
    val date: String,
    val minutes: Int,
)

data class StudyLogsResponse(val logs: List<StudyLogResponse>)

data class CalendarSummaryResponse(val summary: Map<String, String>)

data class SummaryResponse(val summary: List<Map<String, Any>>)

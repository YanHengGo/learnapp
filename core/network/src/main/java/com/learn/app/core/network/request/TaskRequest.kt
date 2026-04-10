package com.learn.app.core.network.request

import com.google.gson.annotations.SerializedName

data class CreateTaskRequest(
    val name: String,
    val description: String?,
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
)

data class UpdateTaskRequest(
    val name: String,
    val description: String?,
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("is_archived") val isArchived: Boolean,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
)

data class ReorderRequest(
    val orders: List<ReorderItem>,
)

data class ReorderItem(
    @SerializedName("task_id") val taskId: String,
    @SerializedName("sort_order") val sortOrder: Int,
)

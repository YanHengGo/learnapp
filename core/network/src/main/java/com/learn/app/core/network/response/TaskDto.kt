package com.learn.app.core.network.response

import com.google.gson.annotations.SerializedName

data class TaskDto(
    val id: String,
    val name: String,
    val description: String?,
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("is_archived") val isArchived: Boolean,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
)

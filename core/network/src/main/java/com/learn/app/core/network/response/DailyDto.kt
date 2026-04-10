package com.learn.app.core.network.response

import com.google.gson.annotations.SerializedName

data class DailyViewDto(
    val date: String,
    val weekday: String,
    val tasks: List<DailyTaskDto>,
)

data class DailyTaskDto(
    @SerializedName("task_id") val taskId: String,
    val name: String,
    val subject: String,
    @SerializedName("default_minutes") val defaultMinutes: Int,
    @SerializedName("days_mask") val daysMask: Int,
    @SerializedName("is_done") val isDone: Boolean,
    val minutes: Int,
)

data class DailyLogDto(
    val date: String,
    val items: List<DailyItemDto>,
)

data class DailyItemDto(
    @SerializedName("task_id") val taskId: String,
    val minutes: Int,
)

data class UpdateDailyResponseDto(
    val date: String,
    @SerializedName("saved_count") val savedCount: Int,
)

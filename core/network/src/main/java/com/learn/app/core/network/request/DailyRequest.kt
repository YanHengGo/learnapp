package com.learn.app.core.network.request

import com.google.gson.annotations.SerializedName

data class UpdateDailyRequest(
    val items: List<DailyItemRequest>,
)

data class DailyItemRequest(
    @SerializedName("task_id") val taskId: String,
    val minutes: Int,
)

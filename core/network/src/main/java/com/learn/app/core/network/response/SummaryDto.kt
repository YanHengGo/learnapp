package com.learn.app.core.network.response

import com.google.gson.annotations.SerializedName

data class CalendarSummaryDto(
    val from: String,
    val to: String,
    val days: List<CalendarDayDto>,
)

data class CalendarDayDto(
    val date: String,
    val status: String,
    val total: Int,
    val done: Int,
)

data class SummaryDto(
    val from: String,
    val to: String,
    @SerializedName("total_minutes") val totalMinutes: Int,
    @SerializedName("by_day") val byDay: List<SummaryByDayDto>,
    @SerializedName("by_subject") val bySubject: List<SummaryBySubjectDto>,
    @SerializedName("by_task") val byTask: List<SummaryByTaskDto>,
)

data class SummaryByDayDto(
    val date: String,
    val minutes: Int,
)

data class SummaryBySubjectDto(
    val subject: String,
    val minutes: Int,
)

data class SummaryByTaskDto(
    @SerializedName("task_id") val taskId: String,
    val name: String,
    val subject: String,
    val minutes: Int,
)

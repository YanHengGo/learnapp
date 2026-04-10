package com.learn.app.core.model

data class Summary(
    val from: String,
    val to: String,
    val totalMinutes: Int,
    val byDay: List<SummaryByDay>,
    val bySubject: List<SummaryBySubject>,
    val byTask: List<SummaryByTask>,
)

data class SummaryByDay(
    val date: String,
    val minutes: Int,
)

data class SummaryBySubject(
    val subject: String,
    val minutes: Int,
)

data class SummaryByTask(
    val taskId: String,
    val name: String,
    val subject: String,
    val minutes: Int,
)

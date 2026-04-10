package com.learn.app.core.data.mapper

import com.learn.app.core.model.CalendarDay
import com.learn.app.core.model.CalendarStatus
import com.learn.app.core.model.CalendarSummary
import com.learn.app.core.model.Summary
import com.learn.app.core.model.SummaryByDay
import com.learn.app.core.model.SummaryBySubject
import com.learn.app.core.model.SummaryByTask
import com.learn.app.core.network.response.CalendarDayDto
import com.learn.app.core.network.response.CalendarSummaryDto
import com.learn.app.core.network.response.SummaryDto

fun CalendarSummaryDto.toModel() = CalendarSummary(
    from = from,
    to = to,
    days = days.map { it.toModel() },
)

fun CalendarDayDto.toModel() = CalendarDay(
    date = date,
    status = status.toCalendarStatus(),
    total = total,
    done = done,
)

fun String.toCalendarStatus() = when (this) {
    "green"  -> CalendarStatus.GREEN
    "yellow" -> CalendarStatus.YELLOW
    "red"    -> CalendarStatus.RED
    else     -> CalendarStatus.WHITE
}

fun SummaryDto.toModel() = Summary(
    from = from,
    to = to,
    totalMinutes = totalMinutes,
    byDay = byDay.map { SummaryByDay(it.date, it.minutes) },
    bySubject = bySubject.map { SummaryBySubject(it.subject, it.minutes) },
    byTask = byTask.map { SummaryByTask(it.taskId, it.name, it.subject, it.minutes) },
)

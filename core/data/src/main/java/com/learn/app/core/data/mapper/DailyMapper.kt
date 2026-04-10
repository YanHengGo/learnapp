package com.learn.app.core.data.mapper

import com.learn.app.core.model.DailyItem
import com.learn.app.core.model.DailyTask
import com.learn.app.core.model.DailyView
import com.learn.app.core.network.response.DailyItemDto
import com.learn.app.core.network.response.DailyLogDto
import com.learn.app.core.network.response.DailyTaskDto
import com.learn.app.core.network.response.DailyViewDto

fun DailyViewDto.toModel() = DailyView(
    date = date,
    weekday = weekday,
    tasks = tasks.map { it.toModel() },
)

fun DailyTaskDto.toModel() = DailyTask(
    taskId = taskId,
    name = name,
    subject = subject,
    defaultMinutes = defaultMinutes,
    daysMask = daysMask,
    isDone = isDone,
    minutes = minutes,
)

fun DailyLogDto.toItems(): List<DailyItem> = items.map { it.toModel() }

fun DailyItemDto.toModel() = DailyItem(
    taskId = taskId,
    minutes = minutes,
)

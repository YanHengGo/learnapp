package com.learn.app.core.data.mapper

import com.learn.app.core.model.Task
import com.learn.app.core.network.response.TaskDto

fun TaskDto.toModel() = Task(
    id = id,
    name = name,
    description = description,
    subject = subject,
    defaultMinutes = defaultMinutes,
    daysMask = daysMask,
    isArchived = isArchived,
    startDate = startDate,
    endDate = endDate,
    sortOrder = sortOrder,
)

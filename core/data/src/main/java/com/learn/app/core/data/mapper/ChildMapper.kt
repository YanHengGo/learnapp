package com.learn.app.core.data.mapper

import com.learn.app.core.model.Child
import com.learn.app.core.network.response.ChildDto

fun ChildDto.toModel() = Child(
    id = id,
    name = name,
    grade = grade,
    isActive = isActive,
)

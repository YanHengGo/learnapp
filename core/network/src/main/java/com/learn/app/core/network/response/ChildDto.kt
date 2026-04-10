package com.learn.app.core.network.response

import com.google.gson.annotations.SerializedName

data class ChildDto(
    val id: String,
    val name: String,
    val grade: String?,
    @SerializedName("is_active") val isActive: Boolean,
)

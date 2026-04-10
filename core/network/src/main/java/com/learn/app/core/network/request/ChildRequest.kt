package com.learn.app.core.network.request

data class CreateChildRequest(
    val name: String,
    val grade: String?,
)

data class UpdateChildRequest(
    val name: String,
    val grade: String?,
)

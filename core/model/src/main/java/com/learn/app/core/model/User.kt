package com.learn.app.core.model

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val avatarUrl: String?,
    val provider: String,
)

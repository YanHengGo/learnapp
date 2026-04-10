package com.learn.app.core.network.request

data class LoginRequest(
    val email: String,
    val password: String,
)

data class SignupRequest(
    val email: String,
    val password: String,
)

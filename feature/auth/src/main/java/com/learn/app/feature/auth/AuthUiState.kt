package com.learn.app.feature.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val mode: AuthMode = AuthMode.LOGIN,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)

enum class AuthMode {
    LOGIN,
    SIGNUP,
}

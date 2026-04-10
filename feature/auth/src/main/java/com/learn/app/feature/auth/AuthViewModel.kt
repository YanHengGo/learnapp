package com.learn.app.feature.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.app.core.domain.usecase.LoginUseCase
import com.learn.app.core.domain.usecase.SignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase,
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, errorMessage = null)
    }

    fun onModeToggle() {
        uiState = uiState.copy(
            mode = if (uiState.mode == AuthMode.LOGIN) AuthMode.SIGNUP else AuthMode.LOGIN,
            errorMessage = null,
        )
    }

    fun onSubmit() {
        val email = uiState.email.trim()
        val password = uiState.password

        if (email.isBlank() || password.isBlank()) {
            uiState = uiState.copy(errorMessage = "メールアドレスとパスワードを入力してください")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val result = if (uiState.mode == AuthMode.LOGIN) {
                loginUseCase(email, password)
            } else {
                signupUseCase(email, password)
            }

            result
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                .onFailure { error ->
                    val message = when {
                        error.message?.contains("invalid credentials") == true ->
                            "メールアドレスまたはパスワードが正しくありません"
                        error.message?.contains("email already exists") == true ->
                            "このメールアドレスは既に登録されています"
                        else -> "エラーが発生しました。もう一度お試しください"
                    }
                    uiState = uiState.copy(isLoading = false, errorMessage = message)
                }
        }
    }
}

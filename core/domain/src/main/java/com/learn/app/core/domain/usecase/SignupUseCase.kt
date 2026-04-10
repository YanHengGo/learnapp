package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.AuthRepository
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    // signup はトークンを返さないため、完了後に login を呼んでトークンを取得する
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        runCatching {
            authRepository.signup(email, password)
            authRepository.login(email, password)
        }.map {}
}

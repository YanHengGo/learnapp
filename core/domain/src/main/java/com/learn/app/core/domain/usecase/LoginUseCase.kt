package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        runCatching { authRepository.login(email, password) }.map {}
}

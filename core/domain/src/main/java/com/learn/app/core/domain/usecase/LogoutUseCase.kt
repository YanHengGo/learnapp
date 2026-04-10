package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        runCatching { authRepository.logout() }
}

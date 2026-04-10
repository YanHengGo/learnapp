package com.learn.app.core.domain.usecase

import com.learn.app.core.domain.repository.AuthRepository
import com.learn.app.core.model.User
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Result<User> =
        runCatching { authRepository.getMe() }
}

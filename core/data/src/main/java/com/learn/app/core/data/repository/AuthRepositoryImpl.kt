package com.learn.app.core.data.repository

import com.learn.app.core.datastore.TokenDataStore
import com.learn.app.core.domain.repository.AuthRepository
import com.learn.app.core.model.User
import com.learn.app.core.network.LearnApiService
import com.learn.app.core.network.request.LoginRequest
import com.learn.app.core.network.request.SignupRequest
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): String {
        val response = api.login(LoginRequest(email = email, password = password))
        tokenDataStore.saveToken(response.token)
        return response.token
    }

    override suspend fun signup(email: String, password: String) {
        api.signup(SignupRequest(email = email, password = password))
        // signup はトークンを返さない。ログインを別途呼ぶ。
    }

    override suspend fun getMe(): User {
        val dto = api.getMe().user
        return User(
            id = dto.id,
            email = dto.email,
            displayName = dto.displayName,
            avatarUrl = dto.avatarUrl,
            provider = dto.provider,
        )
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
    }

    override suspend fun deleteAccount() {
        api.deleteAccount()
        tokenDataStore.clearToken()
    }
}

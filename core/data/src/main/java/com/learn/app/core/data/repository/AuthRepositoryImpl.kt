package com.learn.app.core.data.repository

import com.learn.app.core.datastore.TokenDataStore
import com.learn.app.core.domain.repository.AuthRepository
import com.learn.app.core.model.User
import com.learn.app.core.network.LearnApiService
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: LearnApiService,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(email: String, password: String): String {
        val response = api.login(mapOf("email" to email, "password" to password))
        tokenDataStore.saveToken(response.token)
        return response.token
    }

    override suspend fun signup(email: String, password: String): String {
        val response = api.signup(mapOf("email" to email, "password" to password))
        tokenDataStore.saveToken(response.token)
        return response.token
    }

    override suspend fun getMe(): User {
        val response = api.getMe()
        return User(
            id = response.id,
            email = response.email,
            displayName = response.displayName,
            avatarUrl = response.avatarUrl,
            provider = response.provider,
        )
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
    }
}

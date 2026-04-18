package com.learn.app.core.domain.repository

import com.learn.app.core.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): String
    suspend fun signup(email: String, password: String)
    suspend fun getMe(): User
    suspend fun logout()
    suspend fun deleteAccount()
}

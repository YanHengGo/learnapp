package com.learn.app.core.network.response

import com.google.gson.annotations.SerializedName

data class TokenDto(
    val token: String,
)

data class SignupDto(
    val user: SignupUserDto,
)

data class SignupUserDto(
    val id: String,
    val email: String,
)

data class MeDto(
    val user: UserDto,
)

data class UserDto(
    val id: String,
    val email: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    val provider: String,
)

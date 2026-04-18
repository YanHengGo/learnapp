package com.learn.app.core.common

import retrofit2.HttpException
import java.io.IOException

fun Throwable.toErrorMessage(fallback: String): String = when {
    this is IOException ->
        "ネットワークに接続できません。接続を確認してください。"
    this is HttpException && code() >= 500 ->
        "サーバーエラーが発生しました。しばらくしてからお試しください。"
    this is HttpException && code() == 401 ->
        "認証エラーが発生しました。再ログインしてください。"
    else -> fallback
}

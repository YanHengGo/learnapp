package com.learn.app.core.startup

import android.util.Log

object AppStartInfoLogger {

    private const val TAG = "AppStartInfo"

    fun log(snapshot: AppStartSnapshot) {
        Log.d(TAG, "══════════════════════════════")
        Log.d(TAG, "  起動タイプ  : ${snapshot.startType}")
        Log.d(TAG, "  起動理由    : ${snapshot.reason}")
        Log.d(TAG, "  強制停止後  : ${snapshot.wasForceStopped}")
        Log.d(TAG, "  起動モード  : ${snapshot.launchMode}")
        Log.d(TAG, "  ── タイムスタンプ（Fork 基点）──")
        Log.d(TAG, "  BindApp     : ${snapshot.bindApplicationMs?.let { "$it ms" } ?: "N/A"}")
        Log.d(TAG, "  App.onCreate: ${snapshot.appOnCreateMs?.let { "$it ms" } ?: "N/A"}")
        Log.d(TAG, "  FirstFrame  : ${snapshot.firstFrameMs?.let { "$it ms（合計）" } ?: "N/A"}")
        Log.d(TAG, "══════════════════════════════")
    }
}

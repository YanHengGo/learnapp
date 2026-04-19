package com.learn.app.core.startup

import android.app.ActivityManager
import android.app.ApplicationStartInfo
import android.content.Context
import androidx.annotation.RequiresApi

@RequiresApi(35)
class AppStartInfoCollector(private val context: Context) {

    fun collect(): AppStartSnapshot? {
        val am = context.getSystemService(ActivityManager::class.java)
        val info = am.getHistoricalProcessStartReasons(1).firstOrNull() ?: return null
        val ts = info.startupTimestamps
        val fork = ts[ApplicationStartInfo.START_TIMESTAMP_FORK]

        return AppStartSnapshot(
            startType = info.startType.toStartTypeLabel(),
            reason = info.reason.toReasonLabel(),
            wasForceStopped = info.wasForceStopped(),
            launchMode = info.launchMode.toLaunchModeLabel(),
            bindApplicationMs = fork?.let { base ->
                ts[ApplicationStartInfo.START_TIMESTAMP_BIND_APPLICATION]?.elapsedMsFrom(base)
            },
            appOnCreateMs = fork?.let { base ->
                ts[ApplicationStartInfo.START_TIMESTAMP_APPLICATION_ONCREATE]?.elapsedMsFrom(base)
            },
            firstFrameMs = fork?.let { base ->
                ts[ApplicationStartInfo.START_TIMESTAMP_FIRST_FRAME]?.elapsedMsFrom(base)
            },
        )
    }

    private fun Long.elapsedMsFrom(base: Long): Long = (this - base) / 1_000_000L

    private fun Int.toStartTypeLabel(): String = when (this) {
        ApplicationStartInfo.START_TYPE_COLD -> "COLD"
        ApplicationStartInfo.START_TYPE_WARM -> "WARM"
        ApplicationStartInfo.START_TYPE_HOT  -> "HOT"
        else -> "UNKNOWN($this)"
    }

    private fun Int.toReasonLabel(): String = when (this) {
        ApplicationStartInfo.START_REASON_LAUNCHER          -> "LAUNCHER（アイコンタップ）"
        ApplicationStartInfo.START_REASON_LAUNCHER_RECENTS  -> "LAUNCHER_RECENTS（履歴）"
        ApplicationStartInfo.START_REASON_PUSH              -> "PUSH（プッシュ通知）"
        ApplicationStartInfo.START_REASON_ALARM             -> "ALARM（アラーム）"
        ApplicationStartInfo.START_REASON_BROADCAST         -> "BROADCAST"
        ApplicationStartInfo.START_REASON_SERVICE           -> "SERVICE"
        ApplicationStartInfo.START_REASON_CONTENT_PROVIDER  -> "CONTENT_PROVIDER"
        ApplicationStartInfo.START_REASON_START_ACTIVITY    -> "START_ACTIVITY（外部Intent）"
        ApplicationStartInfo.START_REASON_BOOT_COMPLETE     -> "BOOT_COMPLETE"
        ApplicationStartInfo.START_REASON_BACKUP            -> "BACKUP（バックアップ）"
        ApplicationStartInfo.START_REASON_OTHER             -> "OTHER"
        else -> "UNKNOWN($this)"
    }

    private fun Int.toLaunchModeLabel(): String = when (this) {
        android.content.pm.ActivityInfo.LAUNCH_MULTIPLE      -> "STANDARD"
        android.content.pm.ActivityInfo.LAUNCH_SINGLE_TOP    -> "SINGLE_TOP"
        android.content.pm.ActivityInfo.LAUNCH_SINGLE_TASK   -> "SINGLE_TASK"
        android.content.pm.ActivityInfo.LAUNCH_SINGLE_INSTANCE -> "SINGLE_INSTANCE"
        else -> "UNKNOWN($this)"
    }
}

package com.learn.app.core.profiling

import android.content.Context
import android.util.Log
import androidx.annotation.RequiresApi
import com.learn.app.core.startup.AppStartSnapshot

@RequiresApi(35)
class StartupProfilingTrigger(private val context: Context) {

    private val TAG = "AppProfiling"

    fun maybeProfile(snapshot: AppStartSnapshot) {
        val firstFrame = snapshot.firstFrameMs ?: return
        if (firstFrame <= ProfilingConfig.SLOW_START_THRESHOLD_MS) return

        Log.w(TAG, "起動遅延を検知: FirstFrame=${firstFrame}ms > 閾値${ProfilingConfig.SLOW_START_THRESHOLD_MS}ms")
        Log.w(TAG, "プロファイリングを開始します...")

        val tag = "slow_start_${System.currentTimeMillis()}"
        AppProfilingManager(context).triggerAll(tag)
    }
}

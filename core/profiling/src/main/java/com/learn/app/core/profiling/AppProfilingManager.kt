package com.learn.app.core.profiling

import android.content.Context
import android.os.Bundle
import android.os.ProfilingManager
import android.os.ProfilingResult
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(35)
internal class AppProfilingManager(private val context: Context) {

    private val TAG = "AppProfiling"
    private val profilingManager = context.getSystemService(ProfilingManager::class.java)

    fun triggerAll(tag: String) {
        triggerHeapProfile(tag)
        triggerJavaHeapDump(tag)
        triggerStackSampling(tag)
        triggerSystemTrace(tag)
    }

    private fun triggerHeapProfile(tag: String) {
        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_HEAP_PROFILE,
            null,
            "${tag}_heap",
            null,
            context.mainExecutor,
        ) { result -> logResult("HeapProfile", result) }
    }

    private fun triggerJavaHeapDump(tag: String) {
        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_JAVA_HEAP_DUMP,
            null,
            "${tag}_java_heap",
            null,
            context.mainExecutor,
        ) { result -> logResult("JavaHeapDump", result) }
    }

    private fun triggerStackSampling(tag: String) {
        val params = Bundle().apply {
            putInt("android.profiling.duration_ms", ProfilingConfig.SAMPLING_DURATION_MS)
            putInt("android.profiling.stack_sampling_frequency_hz", ProfilingConfig.STACK_SAMPLING_FREQUENCY_HZ)
        }
        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_STACK_SAMPLING,
            params,
            "${tag}_stack",
            null,
            context.mainExecutor,
        ) { result -> logResult("StackSampling", result) }
    }

    private fun triggerSystemTrace(tag: String) {
        val params = Bundle().apply {
            putInt("android.profiling.duration_ms", ProfilingConfig.SAMPLING_DURATION_MS)
        }
        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_SYSTEM_TRACE,
            params,
            "${tag}_trace",
            null,
            context.mainExecutor,
        ) { result -> logResult("SystemTrace", result) }
    }

    private fun logResult(type: String, result: ProfilingResult) {
        if (result.errorCode == ProfilingResult.ERROR_NONE) {
            Log.d(TAG, "[$type] 完了 → ${result.resultFilePath}")
            Log.d(TAG, "  adb pull ${result.resultFilePath} .")
        } else {
            Log.w(TAG, "[$type] 失敗 errorCode=${result.errorCode} message=${result.errorMessage}")
        }
    }
}

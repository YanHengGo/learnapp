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
        val queue = listOf(
            ProfilingRequest(
                type = ProfilingManager.PROFILING_TYPE_HEAP_PROFILE,
                params = null,
                tag = "${tag}_heap",
                label = "HeapProfile",
            ),
            ProfilingRequest(
                type = ProfilingManager.PROFILING_TYPE_JAVA_HEAP_DUMP,
                params = null,
                tag = "${tag}_java_heap",
                label = "JavaHeapDump",
            ),
            ProfilingRequest(
                type = ProfilingManager.PROFILING_TYPE_STACK_SAMPLING,
                params = Bundle().apply {
                    putInt("android.profiling.duration_ms", ProfilingConfig.SAMPLING_DURATION_MS)
                    putInt("android.profiling.stack_sampling_frequency_hz", ProfilingConfig.STACK_SAMPLING_FREQUENCY_HZ)
                },
                tag = "${tag}_stack",
                label = "StackSampling",
            ),
            ProfilingRequest(
                type = ProfilingManager.PROFILING_TYPE_SYSTEM_TRACE,
                params = Bundle().apply {
                    putInt("android.profiling.duration_ms", ProfilingConfig.SAMPLING_DURATION_MS)
                },
                tag = "${tag}_trace",
                label = "SystemTrace",
            ),
        )
        runNext(queue, index = 0)
    }

    private fun runNext(queue: List<ProfilingRequest>, index: Int) {
        if (index >= queue.size) {
            Log.d(TAG, "全プロファイリング完了")
            return
        }
        val request = queue[index]
        Log.d(TAG, "[${request.label}] 開始...")
        profilingManager.requestProfiling(
            request.type,
            request.params,
            request.tag,
            null,
            context.mainExecutor,
        ) { result ->
            logResult(request.label, result)
            runNext(queue, index + 1)
        }
    }

    private fun logResult(label: String, result: ProfilingResult) {
        if (result.errorCode == ProfilingResult.ERROR_NONE) {
            Log.d(TAG, "[$label] 完了 → ${result.resultFilePath}")
            Log.d(TAG, "  adb pull ${result.resultFilePath} .")
        } else {
            Log.w(TAG, "[$label] 失敗 errorCode=${result.errorCode} message=${result.errorMessage}")
        }
    }

    private data class ProfilingRequest(
        val type: Int,
        val params: Bundle?,
        val tag: String,
        val label: String,
    )
}

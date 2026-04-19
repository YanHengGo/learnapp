package com.learn.app.core.profiling

internal object ProfilingConfig {
    /** FirstFrame がこの値（ms）を超えたらプロファイリングを起動 */
    const val SLOW_START_THRESHOLD_MS = 2_500L

    /** スタックサンプリング・システムトレースの計測時間（ms）*/
    const val SAMPLING_DURATION_MS = 10_000

    /** スタックサンプリング周波数（Hz）*/
    const val STACK_SAMPLING_FREQUENCY_HZ = 100
}

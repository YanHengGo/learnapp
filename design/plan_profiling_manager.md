# ProfilingManager 導入設計

## 概要

Android 15（API 35）の `ProfilingManager` を用いて、アプリ起動遅延を自動検知し
プロファイリングデータを収集する仕組みを導入する。

- **トリガー**: `ApplicationStartInfo.firstFrameMs` が閾値（2,500ms）を超えた場合
- **実行内容**: 4種類のプロファイリングを全て実行
- **出力先**: アプリ内部ストレージ → `adb pull` で取り出す
- **対象**: debug ビルドのみ
- **配置**: 新規 `core:profiling` モジュール

---

## 全体フロー

```
onWindowFocusChanged(hasFocus=true)
        │
        ▼
AppStartInfoCollector.collect()
        │
        ├── AppStartInfoLogger.log(snapshot)   ← 既存（常に出力）
        │
        └── StartupProfilingTrigger.maybeProfile(snapshot)
                    │
              firstFrameMs > 2500ms ?
                    │ YES
                    ▼
          AppProfilingManager.triggerAll()
                    │
          ┌─────────┼──────────┬──────────────┐
          ▼         ▼          ▼              ▼
      HEAP_DUMP  JAVA_HEAP  STACK_SAMPLING  SYSTEM_TRACE
                            (10秒間)        (10秒間)
                    │
          各結果のファイルパスを Logcat に出力
          → adb pull で取り出す
```

---

## モジュール構成

```
core/profiling/
├── build.gradle.kts
└── src/main/java/com/learn/app/core/profiling/
    ├── ProfilingConfig.kt           // 設定値（閾値・サンプリングパラメータ）
    ├── AppProfilingManager.kt       // ProfilingManager ラッパー（4種類実行）
    └── StartupProfilingTrigger.kt   // AppStartSnapshot から閾値判定→トリガー
```

`core:startup` と `core:profiling` は独立したモジュール。
`app` モジュールの `MainActivity` が両者を協調させる。

---

## クラス設計

### ProfilingConfig.kt

```kotlin
object ProfilingConfig {
    /** FirstFrame がこの値（ms）を超えたらプロファイリングを起動 */
    const val SLOW_START_THRESHOLD_MS = 2_500L

    /** スタックサンプリング・システムトレースの計測時間（ms）*/
    const val SAMPLING_DURATION_MS = 10_000L

    /** スタックサンプリング周波数（Hz）*/
    const val STACK_SAMPLING_FREQUENCY_HZ = 100
}
```

### AppProfilingManager.kt

```kotlin
@RequiresApi(35)
class AppProfilingManager(private val context: Context) {

    private val TAG = "AppProfiling"
    private val profilingManager = context.getSystemService(ProfilingManager::class.java)

    fun triggerAll(tag: String) {
        triggerHeapDump(tag)
        triggerJavaHeapDump(tag)
        triggerStackSampling(tag)
        triggerSystemTrace(tag)
    }

    private fun triggerHeapDump(tag: String) {
        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_HEAP_DUMP,
            null,
            "${tag}_heap",
            context.mainExecutor,
        ) { result -> logResult("HeapDump", result) }
    }

    private fun triggerJavaHeapDump(tag: String) {
        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_JAVA_HEAP_DUMP,
            null,
            "${tag}_java_heap",
            context.mainExecutor,
        ) { result -> logResult("JavaHeapDump", result) }
    }

    private fun triggerStackSampling(tag: String) {
        val params = Bundle().apply {
            putInt(ProfilingManager.KEY_DURATION_MS, ProfilingConfig.SAMPLING_DURATION_MS.toInt())
            putInt(ProfilingManager.KEY_STACK_SAMPLING_FREQUENCY, ProfilingConfig.STACK_SAMPLING_FREQUENCY_HZ)
        }
        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_STACK_SAMPLING,
            params,
            "${tag}_stack",
            context.mainExecutor,
        ) { result -> logResult("StackSampling", result) }
    }

    private fun triggerSystemTrace(tag: String) {
        val params = Bundle().apply {
            putInt(ProfilingManager.KEY_DURATION_MS, ProfilingConfig.SAMPLING_DURATION_MS.toInt())
        }
        profilingManager.requestProfiling(
            ProfilingManager.PROFILING_TYPE_SYSTEM_TRACE,
            params,
            "${tag}_trace",
            context.mainExecutor,
        ) { result -> logResult("SystemTrace", result) }
    }

    private fun logResult(type: String, result: ProfilingResult) {
        if (result.errorCode == 0) {
            Log.d(TAG, "[$type] 完了 → ${result.resultFilePath}")
            Log.d(TAG, "  adb pull ${result.resultFilePath} .")
        } else {
            Log.w(TAG, "[$type] 失敗 errorCode=${result.errorCode}")
        }
    }
}
```

### StartupProfilingTrigger.kt

```kotlin
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
```

---

## MainActivity への統合

`core:startup` の `onWindowFocusChanged` に1行追加するだけ。

```kotlin
override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus && BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 35) {
        val snapshot = AppStartInfoCollector(this).collect()
        snapshot?.let {
            AppStartInfoLogger.log(it)                      // 既存
            StartupProfilingTrigger(this).maybeProfile(it)  // 追加
        }
    }
}
```

---

## Logcat 出力例

### 起動遅延を検知した場合

```
W/AppProfiling: 起動遅延を検知: FirstFrame=3124ms > 閾値2500ms
W/AppProfiling: プロファイリングを開始します...
D/AppProfiling: [HeapDump]    完了 → /data/data/com.yanheng.learnapp/files/slow_start_1713500000000_heap.hprof
D/AppProfiling:   adb pull /data/data/com.yanheng.learnapp/files/slow_start_1713500000000_heap.hprof .
D/AppProfiling: [JavaHeapDump] 完了 → /data/data/com.yanheng.learnapp/files/slow_start_1713500000000_java_heap.hprof
D/AppProfiling:   adb pull /data/data/com.yanheng.learnapp/files/slow_start_1713500000000_java_heap.hprof .
（10秒後）
D/AppProfiling: [StackSampling] 完了 → /data/data/com.yanheng.learnapp/files/slow_start_1713500000000_stack.trace
D/AppProfiling:   adb pull /data/data/com.yanheng.learnapp/files/slow_start_1713500000000_stack.trace .
D/AppProfiling: [SystemTrace]   完了 → /data/data/com.yanheng.learnapp/files/slow_start_1713500000000_trace.perfetto-trace
D/AppProfiling:   adb pull /data/data/com.yanheng.learnapp/files/slow_start_1713500000000_trace.perfetto-trace .
```

### 正常起動の場合（閾値以下）

```
（何も出力されない）
```

---

## ファイル取り出し方法

Logcat に表示された `adb pull` コマンドをそのまま実行：

```bash
# 個別取り出し（Logcat の adb pull 行をコピー）
adb pull /data/data/com.yanheng.learnapp/files/slow_start_xxx_heap.hprof .

# まとめて取り出す場合
adb pull /data/data/com.yanheng.learnapp/files/ ./profiling_output/
```

### ファイルの開き方

| ファイル種別 | 拡張子 | 開くツール |
|---|---|---|
| ネイティブヒープダンプ | `.hprof` | Android Studio → Profiler → Load hprof |
| Java ヒープダンプ | `.hprof` | Android Studio → Profiler → Load hprof |
| スタックサンプリング | `.trace` | Android Studio → Profiler → Load trace |
| システムトレース | `.perfetto-trace` | [ui.perfetto.dev](https://ui.perfetto.dev) に読み込む |

---

## 変更ファイル一覧

| ファイル | 変更種別 | 内容 |
|---|---|---|
| `core/profiling/build.gradle.kts` | 新規 | モジュール定義 |
| `core/profiling/.../ProfilingConfig.kt` | 新規 | 閾値・サンプリング設定 |
| `core/profiling/.../AppProfilingManager.kt` | 新規 | 4種類のプロファイリング実行 |
| `core/profiling/.../StartupProfilingTrigger.kt` | 新規 | 閾値判定・トリガー |
| `settings.gradle.kts` | 変更 | `include(":core:profiling")` 追加 |
| `app/build.gradle.kts` | 変更 | `implementation(project(":core:profiling"))` 追加 |
| `app/.../MainActivity.kt` | 変更 | `StartupProfilingTrigger.maybeProfile()` 呼び出し追加 |

---

## 制約・注意事項

- `ProfilingManager` は **API 35 以上のみ**
- 同時に複数のプロファイリングを `requestProfiling()` しても OS がキューイングして順次処理する
- Stack sampling / System trace は **非同期**（10秒後にコールバック）。ヒープダンプは比較的速い
- 内部ストレージのファイルは `adb pull` 時に root 不要（debuggable ビルドのため）
- リリースビルドは `BuildConfig.DEBUG` ガードにより完全除外

---

**作成日**: 2026-04-19

# ApplicationStartInfo 導入設計

## 概要

Android 15（API 35）の `ApplicationStartInfo` API を用いて、アプリ起動の全プロセスを
OSレベルで計測・診断する機能を導入する。

- **対象**: debug ビルドのみ
- **出力先**: Logcat（タグ `AppStartInfo`）
- **配置**: 新規 `core:startup` モジュール

---

## ApplicationStartInfo とは

`ActivityManager.getHistoricalProcessStartReasons(n)` で取得できる OSが自動記録する起動診断書。

| 情報カテゴリ | 取得内容 |
|---|---|
| 起動タイプ | COLD / WARM / HOT |
| 起動理由 | アイコンタップ / Push通知 / アラーム / Service / Broadcast 等 |
| タイムスタンプ | Fork → BindApplication → Application.onCreate → FirstFrame の各時刻 |
| 状態 | 強制停止後の初回起動フラグ / 起動モード（singleTask 等）|

---

## モジュール構成

```
core/startup/
├── build.gradle.kts
└── src/main/java/com/learn/app/core/startup/
    ├── AppStartInfoCollector.kt   // ApplicationStartInfo を収集
    ├── AppStartSnapshot.kt        // 収集結果のデータクラス
    └── AppStartInfoLogger.kt      // Logcat 出力
```

`app` モジュールから `implementation(project(":core:startup"))` で参照し、
`MainActivity` 内で `BuildConfig.DEBUG && API >= 35` のガードのもと呼び出す。

---

## クラス設計

### AppStartSnapshot.kt

```kotlin
data class AppStartSnapshot(
    val startType: String,             // "COLD" / "WARM" / "HOT"
    val reason: String,                // "LAUNCHER" / "PUSH" / "ALARM" / etc.
    val wasForceStopped: Boolean,      // 強制停止後の初回起動か
    val launchMode: String,            // "STANDARD" / "SINGLE_TASK" / etc.
    val forkMs: Long,                  // 基点（0ms）
    val bindApplicationMs: Long?,      // Fork からの経過 ms
    val appOnCreateMs: Long?,          // Fork からの経過 ms
    val firstFrameMs: Long?,           // Fork からの経過 ms（合計起動時間）
)
```

### AppStartInfoCollector.kt

```kotlin
class AppStartInfoCollector(private val context: Context) {

    @RequiresApi(35)
    fun collect(): AppStartSnapshot? {
        val am = context.getSystemService(ActivityManager::class.java)
        val info = am.getHistoricalProcessStartReasons(1).firstOrNull() ?: return null

        val ts = info.startupTimestamps
        val fork = ts[ApplicationStartInfo.START_TIMESTAMP_FORK]

        return AppStartSnapshot(
            startType        = info.startType.toStartTypeLabel(),
            reason           = info.reason.toReasonLabel(),
            wasForceStopped  = info.wasForceStopped(),
            launchMode       = info.launchMode.toLaunchModeLabel(),
            forkMs           = 0L,
            bindApplicationMs = fork?.let { ts[BIND_APPLICATION]?.elapsedMsFrom(it) },
            appOnCreateMs    = fork?.let { ts[APPLICATION_ONCREATE]?.elapsedMsFrom(it) },
            firstFrameMs     = fork?.let { ts[FIRST_FRAME]?.elapsedMsFrom(it) },
        )
    }
}
```

### AppStartInfoLogger.kt

```kotlin
object AppStartInfoLogger {
    private const val TAG = "AppStartInfo"

    fun log(snapshot: AppStartSnapshot) {
        Log.d(TAG, "══════════════════════════════")
        Log.d(TAG, "  起動タイプ  : ${snapshot.startType}")
        Log.d(TAG, "  起動理由    : ${snapshot.reason}")
        Log.d(TAG, "  強制停止後  : ${snapshot.wasForceStopped}")
        Log.d(TAG, "  起動モード  : ${snapshot.launchMode}")
        Log.d(TAG, "  ── タイムスタンプ（Fork 基点）──")
        Log.d(TAG, "  BindApp     : ${snapshot.bindApplicationMs?.let { "${it} ms" } ?: "N/A"}")
        Log.d(TAG, "  App.onCreate: ${snapshot.appOnCreateMs?.let { "${it} ms" } ?: "N/A"}")
        Log.d(TAG, "  FirstFrame  : ${snapshot.firstFrameMs?.let { "${it} ms（合計）" } ?: "N/A"}")
        Log.d(TAG, "══════════════════════════════")
    }
}
```

---

## MainActivity への統合

`onWindowFocusChanged(hasFocus = true)` のタイミングで呼び出す。
このタイミングは FirstFrame が確定した後のため、全タイムスタンプが揃っている。

```kotlin
override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus && BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 35) {
        val snapshot = AppStartInfoCollector(this).collect()
        snapshot?.let { AppStartInfoLogger.log(it) }
    }
}
```

---

## Logcat 出力例

```
D/AppStartInfo: ══════════════════════════════
D/AppStartInfo:   起動タイプ  : COLD
D/AppStartInfo:   起動理由    : LAUNCHER（アイコンタップ）
D/AppStartInfo:   強制停止後  : false
D/AppStartInfo:   起動モード  : STANDARD
D/AppStartInfo:   ── タイムスタンプ（Fork 基点）──
D/AppStartInfo:   BindApp     : 48 ms
D/AppStartInfo:   App.onCreate: 62 ms
D/AppStartInfo:   FirstFrame  : 284 ms（合計）
D/AppStartInfo: ══════════════════════════════
```

---

## 起動理由ラベル対応表

| 定数 | ラベル | 意味 |
|---|---|---|
| `START_REASON_LAUNCHER` | `LAUNCHER（アイコンタップ）` | ホーム画面からタップ |
| `START_REASON_LAUNCHER_RECENTS` | `LAUNCHER_RECENTS（履歴）` | タスク履歴から復帰 |
| `START_REASON_PUSH` | `PUSH（プッシュ通知）` | FCM 等の Push |
| `START_REASON_ALARM` | `ALARM（アラーム）` | AlarmManager トリガー |
| `START_REASON_BROADCAST` | `BROADCAST` | BroadcastReceiver |
| `START_REASON_SERVICE` | `SERVICE` | Service 起動 |
| `START_REASON_CONTENT_PROVIDER` | `CONTENT_PROVIDER` | ContentProvider |
| `START_REASON_START_ACTIVITY` | `START_ACTIVITY（外部Intent）` | 他アプリからの明示的起動 |
| `START_REASON_BOOT_COMPLETE` | `BOOT_COMPLETE` | 端末起動後の自動起動 |
| `START_REASON_BACKUP` | `BACKUP（バックアップ）` | バックアップ/リストア |
| `START_REASON_OTHER` | `OTHER` | その他 |

---

## 変更ファイル一覧

| ファイル | 変更種別 | 内容 |
|---|---|---|
| `core/startup/build.gradle.kts` | 新規 | モジュール定義 |
| `core/startup/.../AppStartSnapshot.kt` | 新規 | 収集結果データクラス |
| `core/startup/.../AppStartInfoCollector.kt` | 新規 | 情報収集クラス |
| `core/startup/.../AppStartInfoLogger.kt` | 新規 | Logcat 出力クラス |
| `settings.gradle.kts` | 変更 | `include(":core:startup")` 追加 |
| `app/build.gradle.kts` | 変更 | `implementation(project(":core:startup"))` 追加 |
| `app/.../MainActivity.kt` | 変更 | `onWindowFocusChanged` で収集・出力 |

---

## 制約・注意事項

- `ApplicationStartInfo` は **API 35 以上のみ**。API 31〜34 の端末では何もしない。
- `getHistoricalProcessStartReasons(1)` は **直近1件**の起動情報を返す。アプリ初回インストール直後など情報がない場合は `null` になる。
- Logcat フィルタ: `tag:AppStartInfo` で絞り込み可能。
- release ビルドには `BuildConfig.DEBUG` ガードにより一切含まれない（コンパイラが最適化で除去）。

---

**作成日**: 2026-04-19

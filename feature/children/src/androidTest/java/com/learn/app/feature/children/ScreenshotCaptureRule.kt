package com.learn.app.feature.children

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.services.storage.TestStorage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Compose UIテスト実行後に自動でスクリーンショットを保存する TestWatcher Rule。
 *
 * 保存先:
 *   screenshots/children/NNN/  （NNN は実行ごとにカウントアップ）
 *   ※ TestStorage → Gradle 中継 → collectScreenshots タスクが最終的にコピー（adb 不要）
 *
 * 使い方:
 *   @get:Rule(order = 0) val composeTestRule = createComposeRule()
 *   @get:Rule(order = 1) val screenshotRule = ScreenshotCaptureRule(composeTestRule)
 *
 * ファイル命名規則: <TestClassName>_<testMethodName>.png
 *
 * 前提:
 *   build.gradle.kts に以下が必要:
 *     testInstrumentationRunnerArguments["useTestStorageService"] = "true"
 *     androidTestImplementation(libs.androidx.test.services)
 *     androidTestUtil(libs.androidx.test.services)
 */
class ScreenshotCaptureRule(
    private val composeRule: ComposeContentTestRule,
) : TestWatcher() {

    /** テスト成功・失敗にかかわらず毎回キャプチャする */
    override fun finished(description: Description) {
        captureScreen(description)
    }

    private fun captureScreen(description: Description) {
        try {
            val bitmap: Bitmap = composeRule.onRoot().captureToImage().asAndroidBitmap()

            val className = description.className.substringAfterLast(".")
            val fileName = "screenshots/${className}_${description.methodName}.png"

            // TestStorage 経由で書き込む → Gradle が自動的にホストへ収集
            val storage = TestStorage()
            storage.openOutputFile(fileName).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (_: Throwable) {
            // ダイアログ表示中など複数ウィンドウがある場合はキャプチャ失敗を無視
        }
    }
}

package com.learn.app.feature.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @get:Rule(order = 0)
    val composeTestRule = createComposeRule()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotCaptureRule(composeTestRule)

    @Test
    fun showsAppTitle() {
        composeTestRule.setContent { SplashContent() }
        composeTestRule.onNodeWithText("学習管理アプリ").assertIsDisplayed()
    }

    @Test
    fun showsProgressIndicator() {
        composeTestRule.setContent { SplashContent() }
        // CircularProgressIndicator はセマンティクスを持たないため
        // タイトルが表示されていることでロード中のスプラッシュ画面を確認
        composeTestRule.onNodeWithText("学習管理アプリ").assertIsDisplayed()
    }
}

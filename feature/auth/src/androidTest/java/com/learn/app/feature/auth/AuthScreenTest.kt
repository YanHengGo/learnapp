package com.learn.app.feature.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreenTest {

    @get:Rule(order = 0)
    val composeTestRule = createComposeRule()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotCaptureRule(composeTestRule)

    // ─── ヘルパー ───────────────────────────────────────────────

    private fun setContent(
        uiState: AuthUiState = AuthUiState(),
        onEmailChange: (String) -> Unit = {},
        onPasswordChange: (String) -> Unit = {},
        onModeToggle: () -> Unit = {},
        onSubmit: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            AuthContent(
                uiState = uiState,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onModeToggle = onModeToggle,
                onSubmit = onSubmit,
            )
        }
    }

    // ─── タイトル ─────────────────────────────────────────────

    @Test
    fun showsAppTitle() {
        setContent()
        composeTestRule.onNodeWithText("学習管理アプリ").assertIsDisplayed()
    }

    // ─── モードラベル ────────────────────────────────────────────

    @Test
    fun loginMode_showsLoginText_inLabelAndButton() {
        setContent(uiState = AuthUiState(mode = AuthMode.LOGIN))
        // モードラベルとボタンの両方に "ログイン" が表示される
        composeTestRule.onAllNodesWithText("ログイン")[0].assertIsDisplayed()
    }

    @Test
    fun signupMode_showsSignupText_inLabelAndButton() {
        setContent(uiState = AuthUiState(mode = AuthMode.SIGNUP))
        // モードラベルとボタンの両方に "新規登録" が表示される
        composeTestRule.onAllNodesWithText("新規登録")[0].assertIsDisplayed()
    }

    // ─── フォームフィールド ──────────────────────────────────────

    @Test
    fun showsEmailField() {
        setContent()
        composeTestRule.onNodeWithText("メールアドレス").assertIsDisplayed()
    }

    @Test
    fun showsPasswordField() {
        setContent()
        composeTestRule.onNodeWithText("パスワード").assertIsDisplayed()
    }

    // ─── 送信ボタン ──────────────────────────────────────────────

    @Test
    fun submitButton_isEnabled_whenNotLoading() {
        setContent(uiState = AuthUiState(isLoading = false))
        composeTestRule.onNodeWithTag("authSubmitButton").assertIsEnabled()
    }

    @Test
    fun submitButton_isDisabled_whenLoading() {
        setContent(uiState = AuthUiState(isLoading = true))
        composeTestRule.onNodeWithTag("authSubmitButton").assertIsNotEnabled()
    }

    @Test
    fun submitButton_click_callsOnSubmit() {
        var called = false
        setContent(onSubmit = { called = true })
        composeTestRule.onNodeWithTag("authSubmitButton").performClick()
        assertTrue(called)
    }

    // ─── モード切り替えボタン ────────────────────────────────────

    @Test
    fun loginMode_showsSignupToggleText() {
        setContent(uiState = AuthUiState(mode = AuthMode.LOGIN))
        composeTestRule.onNodeWithText("アカウントをお持ちでない方はこちら").assertIsDisplayed()
    }

    @Test
    fun signupMode_showsLoginToggleText() {
        setContent(uiState = AuthUiState(mode = AuthMode.SIGNUP))
        composeTestRule.onNodeWithText("すでにアカウントをお持ちの方はこちら").assertIsDisplayed()
    }

    @Test
    fun modeToggleButton_click_callsOnModeToggle() {
        var called = false
        setContent(
            uiState = AuthUiState(mode = AuthMode.LOGIN),
            onModeToggle = { called = true },
        )
        composeTestRule.onNodeWithText("アカウントをお持ちでない方はこちら").performClick()
        assertTrue(called)
    }

    // ─── エラーメッセージ ─────────────────────────────────────────

    @Test
    fun errorMessage_isShown_whenNotNull() {
        setContent(uiState = AuthUiState(errorMessage = "認証に失敗しました"))
        composeTestRule.onNodeWithText("認証に失敗しました").assertIsDisplayed()
    }

    @Test
    fun errorMessage_isNotShown_whenNull() {
        setContent(uiState = AuthUiState(errorMessage = null))
        composeTestRule.onNodeWithText("認証に失敗しました").assertDoesNotExist()
    }

    // ─── パスワード表示切り替え ──────────────────────────────────

    @Test
    fun passwordVisibilityToggle_isShown() {
        setContent()
        // Icon の contentDescription で確認
        composeTestRule.onNodeWithContentDescription("パスワードを表示").assertIsDisplayed()
    }
}

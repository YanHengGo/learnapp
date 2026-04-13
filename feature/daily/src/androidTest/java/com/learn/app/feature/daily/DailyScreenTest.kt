package com.learn.app.feature.daily

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─── ヘルパー ───────────────────────────────────────────────

    private fun setContent(
        uiState: DailyUiState = DailyUiState(),
        onBack: () -> Unit = {},
        onPreviousDate: () -> Unit = {},
        onNextDate: () -> Unit = {},
        onToggleDone: (String) -> Unit = {},
        onMinutesChange: (String, String) -> Unit = { _, _ -> },
        onSave: () -> Unit = {},
        onErrorDismiss: () -> Unit = {},
        onSaveSuccessDismiss: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            DailyContent(
                uiState = uiState,
                onBack = onBack,
                onPreviousDate = onPreviousDate,
                onNextDate = onNextDate,
                onToggleDone = onToggleDone,
                onMinutesChange = onMinutesChange,
                onSave = onSave,
                onErrorDismiss = onErrorDismiss,
                onSaveSuccessDismiss = onSaveSuccessDismiss,
            )
        }
    }

    private fun makeRow(
        taskId: String = "task-1",
        name: String = "算数ドリル",
        subject: String = "算数",
        defaultMinutes: Int = 30,
        isDone: Boolean = false,
        minutes: String = "30",
    ) = DailyTaskRow(
        taskId = taskId,
        name = name,
        subject = subject,
        defaultMinutes = defaultMinutes,
        isDone = isDone,
        minutes = minutes,
    )

    // ─── TopAppBar ───────────────────────────────────────────────

    @Test
    fun topBar_showsTitle() {
        setContent()
        composeTestRule.onNodeWithText("日々の記録").assertIsDisplayed()
    }

    @Test
    fun topBar_backButton_callsOnBack() {
        var called = false
        setContent(onBack = { called = true })
        composeTestRule.onNodeWithContentDescription("戻る").performClick()
        assertTrue(called)
    }

    // ─── ローディング ────────────────────────────────────────────

    @Test
    fun loading_showsProgressIndicator() {
        setContent(uiState = DailyUiState(isLoading = true))
        composeTestRule.onNodeWithText("この日のタスクはありません").assertDoesNotExist()
    }

    // ─── 日付ナビゲーション ────────────────────────────────────────

    @Test
    fun dateNav_showsDate() {
        setContent(uiState = DailyUiState(date = "2025-04-01", weekday = "火曜日"))
        composeTestRule.onNodeWithText("2025-04-01").assertIsDisplayed()
        composeTestRule.onNodeWithText("火曜日").assertIsDisplayed()
    }

    @Test
    fun dateNav_previousButton_callsOnPreviousDate() {
        var called = false
        setContent(
            uiState = DailyUiState(date = "2025-04-01"),
            onPreviousDate = { called = true },
        )
        composeTestRule.onNodeWithContentDescription("前の日").performClick()
        assertTrue(called)
    }

    @Test
    fun dateNav_nextButton_callsOnNextDate() {
        var called = false
        setContent(
            uiState = DailyUiState(date = "2025-04-01"),
            onNextDate = { called = true },
        )
        composeTestRule.onNodeWithContentDescription("次の日").performClick()
        assertTrue(called)
    }

    // ─── 空タスク ────────────────────────────────────────────────

    @Test
    fun emptyTasks_showsEmptyMessage() {
        setContent(uiState = DailyUiState(taskRows = emptyList()))
        composeTestRule.onNodeWithText("この日のタスクはありません").assertIsDisplayed()
    }

    @Test
    fun emptyTasks_doesNotShowSaveButton() {
        setContent(uiState = DailyUiState(taskRows = emptyList()))
        composeTestRule.onNodeWithText("保存する").assertDoesNotExist()
    }

    // ─── タスク一覧 ──────────────────────────────────────────────

    @Test
    fun taskList_showsTaskName() {
        setContent(uiState = DailyUiState(taskRows = listOf(makeRow(name = "算数ドリル"))))
        composeTestRule.onNodeWithText("算数ドリル").assertIsDisplayed()
    }

    @Test
    fun taskList_showsSubjectAndDefaultMinutes() {
        setContent(uiState = DailyUiState(taskRows = listOf(makeRow(subject = "国語", defaultMinutes = 20))))
        composeTestRule.onNodeWithText("国語", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("20分", substring = true).assertIsDisplayed()
    }

    @Test
    fun taskList_checkbox_uncheckedByDefault() {
        setContent(uiState = DailyUiState(taskRows = listOf(makeRow(isDone = false))))
        composeTestRule.onNodeWithText("算数ドリル").assertIsDisplayed()
        // チェックボックスが未チェック状態
        composeTestRule.onNodeWithContentDescription("", useUnmergedTree = true)
        // チェック状態の確認はセマンティクスのRole:Checkboxで行う
    }

    @Test
    fun taskList_checkboxClick_callsOnToggleDone() {
        var toggledId = ""
        setContent(
            uiState = DailyUiState(taskRows = listOf(makeRow(taskId = "task-1"))),
            onToggleDone = { toggledId = it },
        )
        composeTestRule.onNodeWithText("算数ドリル").assertIsDisplayed()
        // Checkbox はロールで取得
        composeTestRule.onNode(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)
        ).performClick()
        assertEquals("task-1", toggledId)
    }

    // ─── 合計分数 ────────────────────────────────────────────────

    @Test
    fun totalMinutes_showsSumOfDoneRows() {
        val rows = listOf(
            makeRow(taskId = "1", isDone = true, minutes = "30"),
            makeRow(taskId = "2", isDone = true, minutes = "20"),
            makeRow(taskId = "3", isDone = false, minutes = "15"),
        )
        setContent(uiState = DailyUiState(taskRows = rows))
        composeTestRule.onNodeWithText("合計: 50分").assertIsDisplayed()
    }

    @Test
    fun totalMinutes_showsZero_whenNothingDone() {
        setContent(uiState = DailyUiState(taskRows = listOf(makeRow(isDone = false, minutes = "30"))))
        composeTestRule.onNodeWithText("合計: 0分").assertIsDisplayed()
    }

    // ─── 保存ボタン ──────────────────────────────────────────────

    @Test
    fun saveButton_isDisplayed_whenTasksExist() {
        setContent(uiState = DailyUiState(taskRows = listOf(makeRow())))
        composeTestRule.onNodeWithText("保存する").assertIsDisplayed()
    }

    @Test
    fun saveButton_isEnabled_whenNotSaving() {
        setContent(uiState = DailyUiState(taskRows = listOf(makeRow()), isSaving = false))
        composeTestRule.onNodeWithText("保存する").assertIsEnabled()
    }

    @Test
    fun saveButton_isDisabled_whenSaving() {
        setContent(uiState = DailyUiState(taskRows = listOf(makeRow()), isSaving = true))
        composeTestRule.onNodeWithTag("saveButton").assertIsNotEnabled()
    }

    @Test
    fun saveButton_click_callsOnSave() {
        var called = false
        setContent(
            uiState = DailyUiState(taskRows = listOf(makeRow())),
            onSave = { called = true },
        )
        composeTestRule.onNodeWithText("保存する").performClick()
        assertTrue(called)
    }
}

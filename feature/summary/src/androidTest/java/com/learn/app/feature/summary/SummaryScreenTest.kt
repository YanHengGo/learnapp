package com.learn.app.feature.summary

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.learn.app.core.model.CalendarDay
import com.learn.app.core.model.CalendarStatus
import com.learn.app.core.model.CalendarSummary
import com.learn.app.core.model.Summary
import com.learn.app.core.model.SummaryBySubject
import com.learn.app.core.model.SummaryByTask
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
class SummaryScreenTest {

    @get:Rule(order = 0)
    val composeTestRule = createComposeRule()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotCaptureRule(composeTestRule)

    // ─── ヘルパー ───────────────────────────────────────────────

    private fun setContent(
        uiState: SummaryUiState = SummaryUiState(),
        onBack: () -> Unit = {},
        onPreviousMonth: () -> Unit = {},
        onNextMonth: () -> Unit = {},
        onDaySelected: (String) -> Unit = {},
        onErrorDismiss: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            SummaryContent(
                uiState = uiState,
                onBack = onBack,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onDaySelected = onDaySelected,
                onErrorDismiss = onErrorDismiss,
            )
        }
    }

    private fun makeSummary(
        totalMinutes: Int = 120,
        bySubject: List<SummaryBySubject> = emptyList(),
        byTask: List<SummaryByTask> = emptyList(),
    ) = Summary(
        from = "2025-04-01",
        to = "2025-04-30",
        totalMinutes = totalMinutes,
        byDay = emptyList(),
        bySubject = bySubject,
        byTask = byTask,
    )

    // ─── TopAppBar ───────────────────────────────────────────────

    @Test
    fun topBar_showsTitle() {
        setContent()
        composeTestRule.onNodeWithText("集計").assertIsDisplayed()
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
    fun loading_hidesContent() {
        setContent(uiState = SummaryUiState(isLoading = true))
        // ローディング中はカレンダーコンテンツを表示しない
        composeTestRule.onNodeWithContentDescription("前の月").assertDoesNotExist()
    }

    // ─── 月ナビゲーション ─────────────────────────────────────────

    @Test
    fun monthNav_showsYearMonth() {
        setContent(uiState = SummaryUiState(yearMonth = YearMonth.of(2025, 4)))
        composeTestRule.onNodeWithText("2025年4月").assertIsDisplayed()
    }

    @Test
    fun monthNav_previousButton_callsOnPreviousMonth() {
        var called = false
        setContent(onPreviousMonth = { called = true })
        composeTestRule.onNodeWithContentDescription("前の月").performClick()
        assertTrue(called)
    }

    @Test
    fun monthNav_nextButton_callsOnNextMonth() {
        var called = false
        setContent(onNextMonth = { called = true })
        composeTestRule.onNodeWithContentDescription("次の月").performClick()
        assertTrue(called)
    }

    // ─── カレンダー曜日ヘッダー ────────────────────────────────────

    @Test
    fun calendar_showsDayOfWeekHeaders() {
        setContent(uiState = SummaryUiState(yearMonth = YearMonth.of(2025, 4)))
        composeTestRule.onNodeWithText("日", substring = false, useUnmergedTree = false).assertIsDisplayed()
        composeTestRule.onNodeWithText("月", substring = false, useUnmergedTree = false).assertIsDisplayed()
        composeTestRule.onNodeWithText("土", substring = false, useUnmergedTree = false).assertIsDisplayed()
    }

    // ─── 凡例 ─────────────────────────────────────────────────────

    @Test
    fun legend_showsAllLabels() {
        setContent()
        composeTestRule.onNodeWithText("全完了").assertIsDisplayed()
        composeTestRule.onNodeWithText("一部完了").assertIsDisplayed()
        composeTestRule.onNodeWithText("未完了").assertIsDisplayed()
    }

    // ─── 統計: 未設定 ─────────────────────────────────────────────

    @Test
    fun summaryStats_notShown_whenSummaryIsNull() {
        setContent(uiState = SummaryUiState(summary = null))
        composeTestRule.onNodeWithText("期間合計").assertDoesNotExist()
    }

    // ─── 統計: 期間合計 ───────────────────────────────────────────

    @Test
    fun summaryStats_showsTotalLabel_whenSummaryExists() {
        setContent(uiState = SummaryUiState(summary = makeSummary(totalMinutes = 90)))
        composeTestRule.onNodeWithText("期間合計").assertIsDisplayed()
    }

    @Test
    fun summaryStats_showsTotalMinutes_hoursAndMinutes() {
        // 90分 → "1時間30分"
        setContent(uiState = SummaryUiState(summary = makeSummary(totalMinutes = 90)))
        composeTestRule.onNodeWithText("1時間30分").assertIsDisplayed()
    }

    @Test
    fun summaryStats_showsTotalMinutes_minutesOnly() {
        // 45分 → "45分"
        setContent(uiState = SummaryUiState(summary = makeSummary(totalMinutes = 45)))
        composeTestRule.onNodeWithText("45分").assertIsDisplayed()
    }

    // ─── 統計: 教科別 ─────────────────────────────────────────────

    @Test
    fun summaryStats_showsBySubjectHeader_whenNotEmpty() {
        val summary = makeSummary(bySubject = listOf(SummaryBySubject("算数", 60)))
        setContent(uiState = SummaryUiState(summary = summary))
        composeTestRule.onNodeWithText("教科別").assertIsDisplayed()
        composeTestRule.onNodeWithText("算数").assertIsDisplayed()
    }

    @Test
    fun summaryStats_noBySubjectHeader_whenEmpty() {
        val summary = makeSummary(bySubject = emptyList())
        setContent(uiState = SummaryUiState(summary = summary))
        composeTestRule.onNodeWithText("教科別").assertDoesNotExist()
    }

    // ─── 統計: タスク別 ───────────────────────────────────────────

    @Test
    fun summaryStats_showsByTaskHeader_whenNotEmpty() {
        val summary = makeSummary(
            byTask = listOf(SummaryByTask("t1", "算数ドリル", "算数", 60)),
        )
        setContent(uiState = SummaryUiState(summary = summary))
        composeTestRule.onNodeWithText("タスク別").assertIsDisplayed()
        composeTestRule.onNodeWithText("算数ドリル").assertIsDisplayed()
    }

    @Test
    fun summaryStats_noByTaskHeader_whenEmpty() {
        val summary = makeSummary(byTask = emptyList())
        setContent(uiState = SummaryUiState(summary = summary))
        composeTestRule.onNodeWithText("タスク別").assertDoesNotExist()
    }
}

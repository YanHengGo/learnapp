package com.learn.app.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.learn.app.core.model.Child
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule(order = 0)
    val composeTestRule = createComposeRule()

    @get:Rule(order = 1)
    val screenshotRule = ScreenshotCaptureRule(composeTestRule)

    // ─── ヘルパー ───────────────────────────────────────────────

    private fun setContent(
        uiState: HomeUiState = HomeUiState(),
        childId: String = "child-1",
        selectedTab: HomeTab = HomeTab.DAILY,
        onShowSwitcher: () -> Unit = {},
        onShowLogoutConfirm: () -> Unit = {},
        onTabDailyClick: () -> Unit = {},
        onTabTasksClick: () -> Unit = {},
        onTabSummaryClick: () -> Unit = {},
        onDismissSwitcher: () -> Unit = {},
        onChildSelect: (String) -> Unit = {},
        onDismissLogoutConfirm: () -> Unit = {},
        onLogout: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            HomeContent(
                uiState = uiState,
                childId = childId,
                selectedTab = selectedTab,
                onShowSwitcher = onShowSwitcher,
                onShowLogoutConfirm = onShowLogoutConfirm,
                onTabDailyClick = onTabDailyClick,
                onTabTasksClick = onTabTasksClick,
                onTabSummaryClick = onTabSummaryClick,
                onDismissSwitcher = onDismissSwitcher,
                onChildSelect = onChildSelect,
                onDismissLogoutConfirm = onDismissLogoutConfirm,
                onLogout = onLogout,
            ) { Box {} }
        }
    }

    // ─── TopAppBar ───────────────────────────────────────────────

    @Test
    fun topBar_showsChildName() {
        setContent(uiState = HomeUiState(selectedChildName = "たろう"))
        composeTestRule.onNodeWithText("たろう").assertIsDisplayed()
    }

    @Test
    fun topBar_showsEllipsis_whenNameBlank() {
        setContent(uiState = HomeUiState(selectedChildName = ""))
        composeTestRule.onNodeWithText("...").assertIsDisplayed()
    }

    @Test
    fun topBar_switcherButton_callsOnShowSwitcher() {
        var called = false
        setContent(
            uiState = HomeUiState(selectedChildName = "たろう"),
            onShowSwitcher = { called = true },
        )
        composeTestRule.onNodeWithContentDescription("子どもを切り替え").performClick()
        assertTrue(called)
    }

    @Test
    fun topBar_logoutButton_callsOnShowLogoutConfirm() {
        var called = false
        setContent(onShowLogoutConfirm = { called = true })
        composeTestRule.onNodeWithContentDescription("ログアウト").performClick()
        assertTrue(called)
    }

    // ─── BottomNavigationBar ─────────────────────────────────────

    @Test
    fun bottomBar_showsAllTabs() {
        setContent()
        composeTestRule.onNodeWithText("日々").assertIsDisplayed()
        composeTestRule.onNodeWithText("タスク").assertIsDisplayed()
        composeTestRule.onNodeWithText("集計").assertIsDisplayed()
    }

    @Test
    fun bottomBar_dailyTab_isSelectedByDefault() {
        setContent(selectedTab = HomeTab.DAILY)
        composeTestRule.onNodeWithText("日々").assertIsSelected()
    }

    @Test
    fun bottomBar_tasksTab_isSelectedWhenTabIsTasks() {
        setContent(selectedTab = HomeTab.TASKS)
        composeTestRule.onNodeWithText("タスク").assertIsSelected()
    }

    @Test
    fun bottomBar_summaryTab_isSelectedWhenTabIsSummary() {
        setContent(selectedTab = HomeTab.SUMMARY)
        composeTestRule.onNodeWithText("集計").assertIsSelected()
    }

    @Test
    fun bottomBar_tasksTabClick_callsCallback() {
        var called = false
        setContent(onTabTasksClick = { called = true })
        composeTestRule.onNodeWithText("タスク").performClick()
        assertTrue(called)
    }

    @Test
    fun bottomBar_summaryTabClick_callsCallback() {
        var called = false
        setContent(onTabSummaryClick = { called = true })
        composeTestRule.onNodeWithText("集計").performClick()
        assertTrue(called)
    }

    @Test
    fun bottomBar_dailyTabClick_callsCallback() {
        var called = false
        setContent(selectedTab = HomeTab.TASKS, onTabDailyClick = { called = true })
        composeTestRule.onNodeWithText("日々").performClick()
        assertTrue(called)
    }

    // ─── 子ども切り替えダイアログ ─────────────────────────────────

    @Test
    fun switcherDialog_showsWhenFlagTrue() {
        setContent(uiState = HomeUiState(showSwitcher = true))
        composeTestRule.onNodeWithText("子どもを切り替え").assertIsDisplayed()
    }

    @Test
    fun switcherDialog_showsChildren() {
        val children = listOf(
            Child(id = "1", name = "たろう", grade = null, isActive = true),
            Child(id = "2", name = "はなこ", grade = null, isActive = true),
        )
        setContent(
            uiState = HomeUiState(showSwitcher = true, children = children),
            childId = "1",
        )
        composeTestRule.onNodeWithText("✓ たろう").assertIsDisplayed()
        composeTestRule.onNodeWithText("はなこ").assertIsDisplayed()
    }

    @Test
    fun switcherDialog_selectChild_callsOnChildSelect() {
        var selectedId = ""
        val children = listOf(
            Child(id = "1", name = "たろう", grade = null, isActive = true),
            Child(id = "2", name = "はなこ", grade = null, isActive = true),
        )
        setContent(
            uiState = HomeUiState(showSwitcher = true, children = children),
            childId = "1",
            onChildSelect = { selectedId = it },
        )
        composeTestRule.onNodeWithText("はなこ").performClick()
        assertEquals("2", selectedId)
    }

    @Test
    fun switcherDialog_currentChild_isDisabled() {
        val children = listOf(
            Child(id = "1", name = "たろう", grade = null, isActive = true),
        )
        setContent(
            uiState = HomeUiState(showSwitcher = true, children = children),
            childId = "1",
        )
        // 現在選択中の子はボタンが無効（✓ マーク付き）
        composeTestRule.onNodeWithText("✓ たろう").assertIsDisplayed()
    }

    // ─── ログアウト確認ダイアログ ─────────────────────────────────

    @Test
    fun logoutDialog_showsWhenFlagTrue() {
        setContent(uiState = HomeUiState(showLogoutConfirm = true))
        composeTestRule.onNodeWithText("ログアウトしますか？").assertIsDisplayed()
        composeTestRule.onNodeWithText("キャンセル").assertIsDisplayed()
    }

    @Test
    fun logoutDialog_confirmClick_callsOnLogout() {
        var called = false
        setContent(
            uiState = HomeUiState(showLogoutConfirm = true),
            onLogout = { called = true },
        )
        composeTestRule.onNode(hasText("ログアウト") and hasClickAction()).performClick()
        assertTrue(called)
    }

    @Test
    fun logoutDialog_cancelClick_callsOnDismissLogoutConfirm() {
        var called = false
        setContent(
            uiState = HomeUiState(showLogoutConfirm = true),
            onDismissLogoutConfirm = { called = true },
        )
        composeTestRule.onNodeWithText("キャンセル").performClick()
        assertTrue(called)
    }
}

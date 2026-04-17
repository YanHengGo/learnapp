package com.learn.app.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.learn.app.core.model.Child

private val previewChildren = listOf(
    Child(id = "1", name = "長男", grade = "小学3年生", isActive = true),
    Child(id = "2", name = "次男", grade = null, isActive = false),
)

@Preview(showBackground = true, name = "日々タブ選択中")
@Composable
private fun PreviewDailyTab() {
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState(
                selectedChildName = "長男",
                selectedTab = HomeTab.DAILY,
            ),
            childId = "1",
            selectedTab = HomeTab.DAILY,
            onShowSwitcher = {},
            onShowLogoutConfirm = {},
            onTabDailyClick = {},
            onTabTasksClick = {},
            onTabSummaryClick = {},
            onDismissSwitcher = {},
            onChildSelect = {},
            onDismissLogoutConfirm = {},
            onLogout = {},
        ) { Box(modifier = Modifier.padding(it)) }
    }
}

@Preview(showBackground = true, name = "タスクタブ選択中")
@Composable
private fun PreviewTasksTab() {
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState(
                selectedChildName = "長男",
                selectedTab = HomeTab.TASKS,
            ),
            childId = "1",
            selectedTab = HomeTab.TASKS,
            onShowSwitcher = {},
            onShowLogoutConfirm = {},
            onTabDailyClick = {},
            onTabTasksClick = {},
            onTabSummaryClick = {},
            onDismissSwitcher = {},
            onChildSelect = {},
            onDismissLogoutConfirm = {},
            onLogout = {},
        ) { Box(modifier = Modifier.padding(it)) }
    }
}

@Preview(showBackground = true, name = "集計タブ選択中")
@Composable
private fun PreviewSummaryTab() {
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState(
                selectedChildName = "長男",
                selectedTab = HomeTab.SUMMARY,
            ),
            childId = "1",
            selectedTab = HomeTab.SUMMARY,
            onShowSwitcher = {},
            onShowLogoutConfirm = {},
            onTabDailyClick = {},
            onTabTasksClick = {},
            onTabSummaryClick = {},
            onDismissSwitcher = {},
            onChildSelect = {},
            onDismissLogoutConfirm = {},
            onLogout = {},
        ) { Box(modifier = Modifier.padding(it)) }
    }
}

@Preview(showBackground = true, name = "子ども切り替えダイアログ")
@Composable
private fun PreviewSwitcherDialog() {
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState(
                selectedChildName = "長男",
                children = previewChildren,
                showSwitcher = true,
            ),
            childId = "1",
            selectedTab = HomeTab.DAILY,
            onShowSwitcher = {},
            onShowLogoutConfirm = {},
            onTabDailyClick = {},
            onTabTasksClick = {},
            onTabSummaryClick = {},
            onDismissSwitcher = {},
            onChildSelect = {},
            onDismissLogoutConfirm = {},
            onLogout = {},
        ) { Box(modifier = Modifier.padding(it)) }
    }
}

@Preview(showBackground = true, name = "ログアウト確認ダイアログ")
@Composable
private fun PreviewLogoutDialog() {
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState(
                selectedChildName = "長男",
                showLogoutConfirm = true,
            ),
            childId = "1",
            selectedTab = HomeTab.DAILY,
            onShowSwitcher = {},
            onShowLogoutConfirm = {},
            onTabDailyClick = {},
            onTabTasksClick = {},
            onTabSummaryClick = {},
            onDismissSwitcher = {},
            onChildSelect = {},
            onDismissLogoutConfirm = {},
            onLogout = {},
        ) { Box(modifier = Modifier.padding(it)) }
    }
}

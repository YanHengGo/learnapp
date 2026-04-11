package com.learn.app

import com.learn.app.core.model.Child

data class HomeUiState(
    val children: List<Child> = emptyList(),
    val selectedChildName: String = "",
    val showSwitcher: Boolean = false,
    val showLogoutConfirm: Boolean = false,
    val selectedTab: HomeTab = HomeTab.DAILY,
)

enum class HomeTab { DAILY, TASKS, SUMMARY }

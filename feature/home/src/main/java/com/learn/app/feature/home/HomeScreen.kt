package com.learn.app.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.learn.app.core.model.Child
import com.learn.app.feature.daily.DailyScreen
import com.learn.app.feature.summary.SummaryScreen
import com.learn.app.feature.tasks.TasksScreen
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    childId: String,
    onBack: () -> Unit,
    onChildSwitch: (newChildId: String) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val innerNavController = rememberNavController()
    val today = LocalDate.now().toString()

    val currentRoute = innerNavController.currentBackStackEntryAsState().value?.destination?.route
    val isAtStartDestination = currentRoute?.startsWith("daily") == true

    BackHandler(enabled = isAtStartDestination) {
        onBack()
    }

    val selectedTab = when {
        currentRoute?.startsWith("tasks") == true -> HomeTab.TASKS
        currentRoute?.startsWith("summary") == true -> HomeTab.SUMMARY
        else -> HomeTab.DAILY
    }

    HomeContent(
        uiState = uiState,
        childId = childId,
        selectedTab = selectedTab,
        onShowSwitcher = viewModel::onShowSwitcher,
        onShowLogoutConfirm = viewModel::onShowLogoutConfirm,
        onTabDailyClick = {
            viewModel.onTabSelected(HomeTab.DAILY)
            innerNavController.navigateToTab("daily/$childId")
        },
        onTabTasksClick = {
            viewModel.onTabSelected(HomeTab.TASKS)
            innerNavController.navigateToTab("tasks/$childId")
        },
        onTabSummaryClick = {
            viewModel.onTabSelected(HomeTab.SUMMARY)
            innerNavController.navigateToTab("summary/$childId")
        },
        onDismissSwitcher = viewModel::onDismissSwitcher,
        onChildSelect = { newChildId ->
            viewModel.onDismissSwitcher()
            onChildSwitch(newChildId)
        },
        onDismissLogoutConfirm = viewModel::onDismissLogoutConfirm,
        onLogout = { viewModel.onLogout(onLoggedOut) },
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = "daily/$childId",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            composable("daily/{childId}") {
                DailyScreen(
                    childId = childId,
                    date = today,
                    onBack = onBack,
                )
            }
            composable("tasks/{childId}") {
                TasksScreen(
                    childId = childId,
                    onBack = onBack,
                )
            }
            composable("summary/{childId}") {
                SummaryScreen(
                    childId = childId,
                    onBack = onBack,
                    onDaySelected = { date ->
                        innerNavController.navigate("daily_detail/$childId/$date")
                    },
                )
            }
            composable("daily_detail/{childId}/{date}") { backStackEntry ->
                val date = backStackEntry.arguments?.getString("date") ?: today
                DailyScreen(
                    childId = childId,
                    date = date,
                    onBack = { innerNavController.popBackStack() },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeContent(
    uiState: HomeUiState,
    childId: String,
    selectedTab: HomeTab,
    onShowSwitcher: () -> Unit,
    onShowLogoutConfirm: () -> Unit,
    onTabDailyClick: () -> Unit,
    onTabTasksClick: () -> Unit,
    onTabSummaryClick: () -> Unit,
    onDismissSwitcher: () -> Unit,
    onChildSelect: (String) -> Unit,
    onDismissLogoutConfirm: () -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextButton(onClick = onShowSwitcher) {
                        Text(text = uiState.selectedChildName.ifBlank { "..." })
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "子どもを切り替え")
                    }
                },
                actions = {
                    IconButton(onClick = onShowLogoutConfirm) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "ログアウト")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == HomeTab.DAILY,
                    onClick = onTabDailyClick,
                    icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                    label = { Text("日々") },
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.TASKS,
                    onClick = onTabTasksClick,
                    icon = { Icon(Icons.Filled.Assignment, contentDescription = null) },
                    label = { Text("タスク") },
                )
                NavigationBarItem(
                    selected = selectedTab == HomeTab.SUMMARY,
                    onClick = onTabSummaryClick,
                    icon = { Icon(Icons.Filled.BarChart, contentDescription = null) },
                    label = { Text("集計") },
                )
            }
        },
    ) { paddingValues ->
        content(paddingValues)
    }

    if (uiState.showSwitcher) {
        ChildSwitcherDialog(
            children = uiState.children,
            currentChildId = childId,
            onSelect = onChildSelect,
            onDismiss = onDismissSwitcher,
        )
    }

    if (uiState.showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = onDismissLogoutConfirm,
            title = { Text("ログアウト") },
            text = { Text("ログアウトしますか？") },
            confirmButton = {
                TextButton(onClick = onLogout) {
                    Text("ログアウト")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissLogoutConfirm) {
                    Text("キャンセル")
                }
            },
        )
    }
}

private fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun ChildSwitcherDialog(
    children: List<Child>,
    currentChildId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("子どもを切り替え") },
        text = {
            LazyColumn {
                items(children.size) { index ->
                    val child = children[index]
                    TextButton(
                        onClick = { onSelect(child.id) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = child.id != currentChildId,
                    ) {
                        Text(
                            text = if (child.id == currentChildId) "✓ ${child.name}" else child.name,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        },
    )
}

package com.learn.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learn.app.feature.auth.AuthScreen
import com.learn.app.feature.children.ChildrenScreen
import com.learn.app.feature.daily.DailyScreen
import com.learn.app.feature.summary.SummaryScreen
import com.learn.app.feature.tasks.TasksScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate("auth") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToChildren = {
                    navController.navigate("children") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
            )
        }
        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate("children") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("children") {
            val today = java.time.LocalDate.now().toString()
            ChildrenScreen(
                onChildSelected = { childId ->
                    navController.navigate("daily/$childId/$today")
                }
            )
        }
        composable("tasks/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            TasksScreen(
                childId = childId,
                onBack = { navController.popBackStack() },
            )
        }
        composable("daily/{childId}/{date}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            DailyScreen(
                childId = childId,
                date = date,
                onBack = { navController.popBackStack() },
            )
        }
        composable("summary/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            SummaryScreen(
                childId = childId,
                onBack = { navController.popBackStack() },
                onDaySelected = { date ->
                    navController.navigate("daily/$childId/$date")
                },
            )
        }
    }
}

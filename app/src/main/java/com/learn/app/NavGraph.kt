package com.learn.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learn.app.feature.auth.AuthScreen
import com.learn.app.feature.children.ChildrenScreen
import com.learn.app.feature.tasks.TasksScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {
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
            ChildrenScreen(
                onChildSelected = { childId ->
                    navController.navigate("tasks/$childId")
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
        composable("daily/{childId}") {
            // TODO: feature:daily
        }
    }
}

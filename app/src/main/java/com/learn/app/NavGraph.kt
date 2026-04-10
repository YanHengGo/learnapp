package com.learn.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learn.app.feature.auth.AuthScreen
import com.learn.app.feature.children.ChildrenScreen

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
                    navController.navigate("daily/$childId")
                }
            )
        }
        composable("daily/{childId}") {
            // TODO: feature:daily
        }
    }
}

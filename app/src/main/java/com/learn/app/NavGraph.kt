package com.learn.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learn.app.feature.auth.AuthScreen
import com.learn.app.feature.children.ChildrenScreen
import com.learn.app.feature.home.HomeScreen
import com.learn.app.feature.splash.SplashScreen

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
            ChildrenScreen(
                onChildSelected = { childId ->
                    navController.navigate("home/$childId") {
                        popUpTo("children") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLoggedOut = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable("home/{childId}") { backStackEntry ->
            val childId = backStackEntry.arguments?.getString("childId") ?: ""
            HomeScreen(
                childId = childId,
                onBack = { navController.popBackStack() },
                onChildSwitch = { newChildId ->
                    navController.navigate("home/$newChildId") {
                        popUpTo("home/$childId") { inclusive = true }
                    }
                },
                onLoggedOut = {
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}

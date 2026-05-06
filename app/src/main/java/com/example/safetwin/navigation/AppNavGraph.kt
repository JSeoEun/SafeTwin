package com.example.safetwin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safetwin.ui.screen.auth.LoginScreen
import com.example.safetwin.ui.screen.auth.SignUpScreen
import com.example.safetwin.ui.screen.main.MainScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = NavRoutes.LOGIN,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(NavRoutes.LOGIN)   { LoginScreen(navController) }
        composable(NavRoutes.SIGN_UP) { SignUpScreen(navController) }
        composable(NavRoutes.MAIN)    { MainScreen(navController) }
    }
}

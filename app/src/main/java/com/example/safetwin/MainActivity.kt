package com.example.safetwin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.safetwin.data.local.SessionManager
import com.example.safetwin.data.local.TokenManager
import com.example.safetwin.navigation.AppNavGraph
import com.example.safetwin.navigation.NavRoutes
import com.example.safetwin.ui.theme.SafeTwinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeTwinTheme {
                val navController = rememberNavController()
                val startDestination =
                    if (TokenManager.isLoggedIn()) NavRoutes.MAIN else NavRoutes.LOGIN

                AppNavGraph(navController, startDestination)

                // 토큰 갱신 실패(세션 만료) 시 LOGIN으로 강제 이동
                LaunchedEffect(Unit) {
                    SessionManager.sessionExpired.collect {
                        navController.navigate(NavRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

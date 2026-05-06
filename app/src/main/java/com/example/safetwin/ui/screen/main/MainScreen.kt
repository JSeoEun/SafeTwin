package com.example.safetwin.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.safetwin.data.local.TokenManager
import com.example.safetwin.data.model.LogoutRequest
import com.example.safetwin.data.network.ApiClient
import com.example.safetwin.navigation.NavRoutes
import com.example.safetwin.ui.theme.Primary
import kotlinx.coroutines.launch

private data class DrawerMenuItem(val label: String, val route: String)

private val menuItems = listOf(
    DrawerMenuItem("대시보드",       NavRoutes.Main.DASHBOARD),
    DrawerMenuItem("안전 분석",      NavRoutes.Main.ANALYSIS),
    DrawerMenuItem("탑뷰 시각화",    NavRoutes.Main.DIGITAL_TWIN),
    DrawerMenuItem("통계 & 사후관리", NavRoutes.Main.AFTER_CARE),
    DrawerMenuItem("법적 증빙",      NavRoutes.Main.LEGAL),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(rootNavController: NavHostController) {
    val innerNavController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.Main.DASHBOARD

    val screenTitle = when (currentRoute) {
        NavRoutes.Main.DASHBOARD    -> "대시보드"
        NavRoutes.Main.ANALYSIS     -> "안전 분석"
        NavRoutes.Main.DIGITAL_TWIN -> "탑뷰 시각화"
        NavRoutes.Main.AFTER_CARE   -> "통계 & 사후관리"
        NavRoutes.Main.LEGAL        -> "법적 증빙"
        else                        -> "SafeTwin"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    innerNavController.navigate(route) {
                        popUpTo(innerNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    scope.launch {
                        val refreshToken = TokenManager.getRefreshToken()
                        try {
                            if (refreshToken != null) {
                                ApiClient.api.logout(LogoutRequest(refreshToken))
                            }
                        } catch (e: Exception) {
                            // best-effort: API 실패해도 로그아웃 진행
                        } finally {
                            TokenManager.clearTokens()
                            rootNavController.navigate(NavRoutes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                },
            )
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(screenTitle, fontWeight = FontWeight.Bold, color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "메뉴 열기", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = innerNavController,
                startDestination = NavRoutes.Main.DASHBOARD,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(NavRoutes.Main.DASHBOARD)    { DashScreen() }
                composable(NavRoutes.Main.ANALYSIS)     { AnalysisScreen() }
                composable(NavRoutes.Main.DIGITAL_TWIN) { DigitalTwinScreen() }
                composable(NavRoutes.Main.AFTER_CARE)   { AfterCareScreen() }
                composable(NavRoutes.Main.LEGAL)        { LegalScreen() }
            }
        }
    }
}

@Composable
private fun AppDrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(Color.White)
            .padding(16.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "SafeTwin",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        HorizontalDivider()
        Spacer(Modifier.height(8.dp))

        menuItems.forEach { item ->
            val isSelected = currentRoute == item.route
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSelected) Color(0xFFE8EEF8) else Color.Transparent,
                    )
                    .clickable { onNavigate(item.route) }
                    .padding(horizontal = 12.dp, vertical = 14.dp),
            ) {
                Text(
                    text = item.label,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Primary else Color(0xFF1A1A2E),
                )
            }
        }

        Spacer(Modifier.weight(1f))
        HorizontalDivider()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(horizontal = 12.dp, vertical = 14.dp),
        ) {
            Text(text = "로그아웃", fontSize = 16.sp, color = Color(0xFF666666))
        }
    }
}


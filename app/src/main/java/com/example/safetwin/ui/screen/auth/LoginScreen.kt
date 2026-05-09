package com.example.safetwin.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.safetwin.navigation.NavRoutes
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.safetwin.ui.component.LabeledTextField
import com.example.safetwin.ui.component.PrimaryButton
import com.example.safetwin.ui.theme.SafeTwinTheme

@Composable
fun LoginScreen(
    navController: NavHostController,
    vm: LoginViewModel = viewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8EEF8))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))

        // 로고 섹션
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🛡️", fontSize = 32.sp)
            Spacer(Modifier.width(8.dp))
            Text("Safe", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1640C))
            Text("Twin", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "안전한 사업장 관리를 위한 스마트 솔루션",
            fontSize = 14.sp,
            color = Color(0xFF666666),
        )
        Spacer(Modifier.height(30.dp))

        // 메인 카드
        Column(
            modifier = Modifier
                .width(280.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(28.dp),
        ) {
            // 탭 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF2F4F7)),
            ) {
                // 로그인 탭 (선택됨)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(3.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "로그인",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2A44),
                    )
                }
                // 회원가입 탭
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable { navController.navigate(NavRoutes.SIGN_UP) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "회원가입",
                        fontSize = 13.sp,
                        color = Color(0xFF8A98AD),
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            LabeledTextField(
                label = "이메일",
                value = vm.email,
                onValueChange = { vm.email = it; vm.errorMsg = null },
                hint = "example@company.com",
            )

            Spacer(Modifier.height(18.dp))

            LabeledTextField(
                label = "비밀번호",
                value = vm.password,
                onValueChange = { vm.password = it; vm.errorMsg = null },
                hint = "••••••••",
                isPassword = true,
            )

            vm.errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFFD93025), fontSize = 13.sp)
            }

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = if (vm.isLoading) "로그인 중..." else "로그인",
                enabled = !vm.isLoading,
                onClick = {
                    vm.login {
                        navController.navigate(NavRoutes.MAIN) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                },
            )
        }

        // 하단 회원가입 링크
        Spacer(Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("아직 회원이 아니신가요? ", color = Color(0xFF666666), fontSize = 14.sp)
            Text(
                text = "지금 가입하세요",
                color = Color(0xFF1565C0),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate(NavRoutes.SIGN_UP) },
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, name = "로그인")
@Composable
private fun LoginScreenPreview() {
    SafeTwinTheme {
        LoginScreen(navController = rememberNavController(), vm = LoginViewModel())
    }
}

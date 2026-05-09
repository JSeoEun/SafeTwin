package com.example.safetwin.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.safetwin.navigation.NavRoutes
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.safetwin.ui.component.LabeledTextField
import com.example.safetwin.ui.component.PrimaryButton
import com.example.safetwin.ui.theme.SafeGreen
import com.example.safetwin.ui.theme.SafeTwinTheme

@Composable
fun SignUpScreen(
    navController: NavHostController,
    vm: SignUpViewModel = viewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAF0FA))
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
                // 로그인 탭 (비선택)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable {
                            navController.navigate(NavRoutes.LOGIN) {
                                popUpTo(NavRoutes.LOGIN) { inclusive = true }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "로그인",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7C93),
                    )
                }
                // 회원가입 탭 (선택됨)
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
                        text = "회원가입",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2A44),
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            // 사업자 등록번호 + 인증 버튼
            Text(
                text = "사업자 등록번호",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF344054),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = vm.businessNumber,
                    onValueChange = {
                        vm.businessNumber = it
                        vm.isBusinessVerified = false
                        vm.companyName = null
                        vm.errorMsg = null
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("000-00-00000", color = Color(0xFF98A2B3), fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E4DCB),
                        unfocusedBorderColor = Color(0xFFD0D5DD),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                )
                if (vm.isBusinessVerified) {
                    Box(
                        modifier = Modifier
                            .size(width = 78.dp, height = 52.dp)
                            .border(1.dp, SafeGreen, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✓ 확인됨",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SafeGreen,
                        )
                    }
                } else {
                    Button(
                        onClick = { vm.verifyBiz() },
                        enabled = !vm.isVerifying,
                        modifier = Modifier.size(width = 78.dp, height = 52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1565C0),
                            disabledContainerColor = Color(0xFFB0BEC5),
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    ) {
                        Text(
                            text = if (vm.isVerifying) "확인 중" else "인증",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }
            if (vm.isBusinessVerified && vm.companyName != null) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "✓ ${vm.companyName}",
                    fontSize = 11.sp,
                    color = SafeGreen,
                )
            }

            Spacer(Modifier.height(20.dp))

            // 이름
            LabeledTextField(
                label = "이름",
                value = vm.name,
                onValueChange = { vm.name = it; vm.errorMsg = null },
                hint = "홍길동",
            )

            Spacer(Modifier.height(18.dp))

            // 이메일
            LabeledTextField(
                label = "이메일",
                value = vm.email,
                onValueChange = { vm.email = it; vm.errorMsg = null },
                hint = "example@company.com",
            )

            Spacer(Modifier.height(18.dp))

            // 비밀번호
            Text(
                text = "비밀번호",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF344054),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = vm.password,
                onValueChange = { vm.password = it; vm.errorMsg = null },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = { Text("최소 8자 이상", color = Color(0xFF98A2B3), fontSize = 14.sp) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1E4DCB),
                    unfocusedBorderColor = Color(0xFFD0D5DD),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
            )

            Spacer(Modifier.height(18.dp))

            // 전화번호 (선택)
            LabeledTextField(
                label = "전화번호 (선택)",
                value = vm.phone,
                onValueChange = { vm.phone = it },
                hint = "010-0000-0000",
            )

            vm.errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFFD93025), fontSize = 13.sp)
            }

            Spacer(Modifier.height(18.dp))

            PrimaryButton(
                text = if (vm.isLoading) "가입 중..." else "가입 완료",
                enabled = !vm.isLoading,
                onClick = {
                    vm.signUp {
                        navController.navigate(NavRoutes.MAIN) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                },
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, name = "회원가입")
@Composable
private fun SignUpScreenPreview() {
    SafeTwinTheme {
        SignUpScreen(navController = rememberNavController(), vm = SignUpViewModel())
    }
}

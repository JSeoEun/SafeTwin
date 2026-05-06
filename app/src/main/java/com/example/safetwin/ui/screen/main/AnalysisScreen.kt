package com.example.safetwin.ui.screen.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safetwin.data.model.AnalysisResponse
import com.example.safetwin.ui.component.BadgeStatus
import com.example.safetwin.ui.component.DashboardCard
import com.example.safetwin.ui.component.StatusBadge
import com.example.safetwin.ui.theme.DangerRed
import com.example.safetwin.ui.theme.Primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AnalysisScreen(vm: AnalysisViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var zoneIdText by remember { mutableStateOf("1") }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            val zoneId = zoneIdText.toLongOrNull() ?: 1L
            scope.launch(Dispatchers.IO) {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                bytes?.let { vm.uploadImage(zoneId, it) }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FB)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 헤더
        item {
            Text("안전 분석", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736))
            Spacer(Modifier.height(4.dp))
            Text("현장 사진 업로드 및 AI 위험 탐지", fontSize = 14.sp, color = Color(0xFF7A8AA0))
        }

        // 업로드 카드
        item {
            DashboardCard {
                Text("새 이미지 분석", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736))
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = zoneIdText,
                        onValueChange = { zoneIdText = it },
                        modifier = Modifier.width(100.dp),
                        label = { Text("구역 ID", fontSize = 12.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E4DCB),
                            unfocusedBorderColor = Color(0xFFD0D5DD),
                        ),
                    )
                    Button(
                        onClick = { imageLauncher.launch("image/*") },
                        enabled = !vm.isUploading,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) {
                        Text(
                            text = if (vm.isUploading) "분석 중..." else "이미지 선택",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        // 폴링 진행 상태
        if (vm.isUploading) {
            item {
                DashboardCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp,
                            color = Primary,
                        )
                        Text(
                            text = vm.pollingStatus ?: "분석 중...",
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                        )
                    }
                }
            }
        }

        // 오류 메시지
        vm.errorMsg?.let { msg ->
            item {
                Text(msg, color = DangerRed, fontSize = 13.sp)
            }
        }

        // 목록 로딩 중
        if (vm.isLoadingList) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        } else if (vm.analyses.isEmpty() && !vm.isUploading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("아직 분석 기록이 없습니다.", fontSize = 14.sp, color = Color(0xFF7A8AA0))
                }
            }
        } else {
            // 가장 최근 완료 분석 상세 표시
            val latestCompleted = vm.analyses.firstOrNull { it.status == "COMPLETED" }
            latestCompleted?.let { analysis ->
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusBadge(riskLevelToBadge(analysis.riskLevel), riskLevelKo(analysis.riskLevel))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            analysis.location ?: "-",
                            fontSize = 14.sp,
                            color = Color(0xFF26364D),
                        )
                    }
                }

                item {
                    DashboardCard {
                        Text(
                            "종합 위험 점수",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B1736),
                        )
                        Spacer(Modifier.height(8.dp))
                        val score = analysis.overallScore ?: 0
                        Text(
                            "$score / 100",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (score < 50) DangerRed else Color(0xFF1D4ED8),
                        )
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = if (score < 50) DangerRed else Primary,
                            trackColor = Color(0xFFE2E8F0),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            analysis.analyzedAt?.take(10) ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFF7A8AA0),
                        )
                    }
                }

                if (analysis.risks.isNotEmpty()) {
                    item {
                        Text(
                            "탐지된 위험 요소 ${analysis.risks.size}건",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0B1736),
                        )
                    }
                    items(analysis.risks) { risk ->
                        val index = analysis.risks.indexOf(risk) + 1
                        HazardItem(
                            number = "$index",
                            title = risk.label,
                            description = listOfNotNull(
                                risk.detail,
                                risk.law,
                                risk.action?.let { "조치: $it" },
                            ).joinToString(" · "),
                        )
                    }
                }
            }

            // 나머지 분석 목록
            val rest = if (latestCompleted != null) vm.analyses.drop(1) else vm.analyses
            if (rest.isNotEmpty()) {
                item {
                    Text(
                        "분석 이력",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B1736),
                    )
                }
                items(rest) { analysis ->
                    AnalysisListItem(analysis)
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun AnalysisListItem(analysis: AnalysisResponse) {
    DashboardCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    analysis.location ?: "-",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0B1736),
                )
                Text(
                    analysis.analyzedAt?.take(10) ?: "-",
                    fontSize = 12.sp,
                    color = Color(0xFF7A8AA0),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                when (analysis.status) {
                    "COMPLETED" -> {
                        StatusBadge(riskLevelToBadge(analysis.riskLevel), riskLevelKo(analysis.riskLevel))
                        analysis.overallScore?.let {
                            Spacer(Modifier.height(4.dp))
                            Text("${it}점", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                        }
                    }
                    "PENDING", "IN_PROGRESS" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(14.dp), strokeWidth = 2.dp, color = Primary)
                            Spacer(Modifier.width(6.dp))
                            Text("분석 중", fontSize = 12.sp, color = Color(0xFF7A8AA0))
                        }
                    }
                    else -> Text(analysis.status, fontSize = 12.sp, color = Color(0xFF7A8AA0))
                }
            }
        }
    }
}

@Composable
private fun HazardItem(number: String, title: String, description: String) {
    DashboardCard {
        Text(
            text = "$number  $title",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B1736),
        )
        if (description.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(description, fontSize = 14.sp, color = Color(0xFF26364D), lineHeight = 21.sp)
        }
    }
}

private fun riskLevelToBadge(level: String?): BadgeStatus = when (level) {
    "LOW"      -> BadgeStatus.SAFE
    "MEDIUM"   -> BadgeStatus.WARNING
    "HIGH",
    "CRITICAL" -> BadgeStatus.DANGER
    else       -> BadgeStatus.WARNING
}

private fun riskLevelKo(level: String?): String = when (level) {
    "LOW"      -> "양호"
    "MEDIUM"   -> "주의"
    "HIGH"     -> "위험"
    "CRITICAL" -> "고위험"
    else       -> level ?: "-"
}

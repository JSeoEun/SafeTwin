package com.example.safetwin.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safetwin.data.model.DashboardSummary
import com.example.safetwin.ui.component.DashboardCard
import com.example.safetwin.ui.theme.DangerRed
import com.example.safetwin.ui.theme.Primary
import com.example.safetwin.ui.theme.SafeGreen

@Composable
fun DashScreen(vm: DashboardViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FB)),
    ) {
        when (val state = vm.uiState) {
            is DashboardUiState.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            is DashboardUiState.Error -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(state.message, color = DangerRed, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { vm.loadDashboard() }) { Text("다시 시도") }
                }
            }
            is DashboardUiState.Success -> DashContent(state.data)
        }
    }
}

@Composable
private fun DashContent(summary: DashboardSummary) {
    val scoreColor = if (summary.safetyScoreDelta >= 0) SafeGreen else DangerRed
    val riskColor  = if (summary.weeklyRiskDelta <= 0) SafeGreen else DangerRed

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("SafeTwin", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(Modifier.height(4.dp))
            Text("안전 현황 요약", fontSize = 14.sp, color = Color(0xFF7A8AA0))
            Spacer(Modifier.height(10.dp))
        }

        item {
            StatCard(
                label = "안전 점수",
                value = "${summary.safetyScore.toInt()}점",
                delta = "${if (summary.safetyScoreDelta >= 0) "+" else ""}${String.format("%.1f", summary.safetyScoreDelta)} 전주 대비",
                deltaColor = scoreColor,
            )
        }
        item {
            StatCard(
                label = "이번 주 위험 탐지",
                value = "${summary.weeklyRiskCount}건",
                delta = "${if (summary.weeklyRiskDelta >= 0) "+" else ""}${summary.weeklyRiskDelta} 전주 대비",
                deltaColor = riskColor,
            )
        }
        item {
            StatCard(
                label = "미조치 항목",
                value = "${summary.unresolvedCount}개",
                delta = "현재 미해결",
                deltaColor = if (summary.unresolvedCount > 0) DangerRed else SafeGreen,
            )
        }
        item {
            StatCard(
                label = "교육 완료율",
                value = "${(summary.educationRate * 100).toInt()}%",
                delta = "이수율",
                deltaColor = if (summary.educationRate >= 0.8) SafeGreen else DangerRed,
            )
        }

        item {
            DashboardCard {
                Text(
                    "최근 분석 기록",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B1736),
                )
                Spacer(Modifier.height(14.dp))
                if (summary.recentAnalyses.isEmpty()) {
                    Text("최근 분석 기록이 없습니다.", fontSize = 13.sp, color = Color(0xFF7A8AA0))
                } else {
                    summary.recentAnalyses.forEachIndexed { i, analysis ->
                        if (i > 0) Spacer(Modifier.height(12.dp))
                        AnalysisRecord(
                            header = "${analysis.location ?: "-"}  |  ${analysis.analyzedAt?.take(10) ?: "-"}  |  ${riskLevelKo(analysis.riskLevel)}",
                            detail = "점수: ${analysis.overallScore ?: "-"} / 100",
                        )
                    }
                }
            }
        }

        item {
            DashboardCard {
                Text(
                    "안전 등급 현황",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B1736),
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    "${summary.safetyScore.toInt()} / 100",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D4ED8),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "등급 ${summary.safetyGrade}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E40AF),
                )
                Spacer(Modifier.height(6.dp))
                Text("목표 90점", fontSize = 13.sp, color = Color(0xFF64748B))
            }
        }

        summary.tbmGuide?.let { guide ->
            item {
                DashboardCard(modifier = Modifier.background(Color(0xFFEEF2FF))) {
                    Text(
                        "오늘의 TBM 가이드",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B1736),
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(guide, fontSize = 14.sp, color = Color(0xFF334155), lineHeight = 22.sp)
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

private fun riskLevelKo(level: String?): String = when (level) {
    "LOW"      -> "양호"
    "MEDIUM"   -> "주의"
    "HIGH"     -> "위험"
    "CRITICAL" -> "고위험"
    else       -> level ?: "-"
}

@Composable
private fun StatCard(label: String, value: String, delta: String, deltaColor: Color) {
    DashboardCard(modifier = Modifier.height(120.dp)) {
        Text(label, fontSize = 14.sp, color = Color(0xFF6B7C93))
        Text(value, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736))
        Text(delta, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = deltaColor)
    }
}

@Composable
private fun AnalysisRecord(header: String, detail: String) {
    Text(
        text = header,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF26364D),
        modifier = Modifier.fillMaxWidth(),
    )
    Text(text = detail, fontSize = 13.sp, color = Color(0xFF26364D))
}

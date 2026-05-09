package com.example.safetwin.ui.screen.main

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safetwin.data.model.CompareResponse
import com.example.safetwin.data.model.ScoreTrendResponse
import com.example.safetwin.ui.component.DashboardCard
import androidx.compose.ui.tooling.preview.Preview
import com.example.safetwin.ui.theme.DangerRed
import com.example.safetwin.ui.theme.Primary
import com.example.safetwin.ui.theme.SafeGreen
import com.example.safetwin.ui.theme.SafeTwinTheme

@Composable
fun AfterCareScreen(vm: AfterCareViewModel = viewModel()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FB)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                "통계 & 사후 관리",
                fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "안전 점수 추이 및 Before / After 비교",
                fontSize = 14.sp, color = Color(0xFF7A8AA0),
            )
        }

        // 에러
        vm.errorMsg?.let {
            item { Text(it, color = DangerRed, fontSize = 13.sp) }
        }

        // 점수 추이 바 차트
        item {
            DashboardCard {
                Text(
                    "연간 안전 점수 추이",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736),
                )
                Spacer(Modifier.height(18.dp))
                if (vm.isLoadingTrend) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(170.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else {
                    val trend = vm.trend
                    if (trend != null) {
                        ApiBarChart(trend)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = trend.labels.joinToString("  "),
                            fontSize = 10.sp, color = Color(0xFF7A8AA0),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        Text(
                            "점수 추이 데이터가 없습니다.",
                            fontSize = 13.sp, color = Color(0xFF7A8AA0),
                            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        // 점수 요약 박스
        vm.trend?.let { trend ->
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val minScore     = trend.min ?: 0
                    val currentScore = trend.current ?: 0
                    val toTarget     = trend.target - currentScore
                    SummaryBox(
                        label = "$minScore\n안전 최저점",
                        textColor = Color(0xFFD93025),
                        bgColor   = Color(0xFFFFE5E5),
                        modifier  = Modifier.weight(1f),
                    )
                    SummaryBox(
                        label = "$currentScore\n현재 점수",
                        textColor = Color(0xFF1E4DCB),
                        bgColor   = Color(0xFFE8EEFF),
                        modifier  = Modifier.weight(1f),
                    )
                    SummaryBox(
                        label = "${if (toTarget > 0) "-${toTarget}점" else "달성!"}\n목표까지",
                        textColor = if (toTarget > 0) Color(0xFFE58A00) else SafeGreen,
                        bgColor   = if (toTarget > 0) Color(0xFFFFF3DC) else Color(0xFFE8F5E9),
                        modifier  = Modifier.weight(1f),
                    )
                }
            }
        }

        // Before / After 비교 입력
        item {
            DashboardCard {
                Text(
                    "Before / After 비교 분석",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736),
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = vm.beforeIdText,
                        onValueChange = { vm.beforeIdText = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("이전 분석 ID", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E4DCB),
                            unfocusedBorderColor = Color(0xFFD0D5DD),
                        ),
                    )
                    OutlinedTextField(
                        value = vm.afterIdText,
                        onValueChange = { vm.afterIdText = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("이후 분석 ID", fontSize = 11.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E4DCB),
                            unfocusedBorderColor = Color(0xFFD0D5DD),
                        ),
                    )
                    Button(
                        onClick = { vm.loadCompare() },
                        enabled = !vm.isLoadingCompare,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    ) {
                        if (vm.isLoadingCompare) {
                            CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text("비교", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 비교 결과
        vm.compare?.let { cmp ->
            item { CompareCard(cmp) }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ApiBarChart(trend: ScoreTrendResponse) {
    val maxBarHeight = 148.dp
    val bars: List<Pair<Dp, Boolean>> = trend.scores.mapIndexed { idx, score ->
        val height = if (score != null) maxBarHeight * (score / 100f) else 4.dp
        height to (idx == trend.scores.lastIndex)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        bars.forEachIndexed { idx, (height, isHighlight) ->
            val score = trend.scores[idx]
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(height)
                    .background(
                        if (isHighlight) Color(0xFF6F8FE8)
                        else if (score == null) Color(0xFFE2E8F0)
                        else Color(0xFFD8E0F2),
                    ),
                contentAlignment = Alignment.TopCenter,
            ) {
                if (score != null && (idx == 0 || isHighlight)) {
                    Text(
                        "$score",
                        fontSize = 10.sp,
                        fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
                        color = if (isHighlight) Color(0xFF1E4DCB) else Color(0xFF7A8AA0),
                    )
                }
            }
        }
    }
}

@Composable
private fun CompareCard(cmp: CompareResponse) {
    DashboardCard {
        // 요약 배지
        val improved = cmp.afterScore > cmp.beforeScore
        Box(
            modifier = Modifier
                .background(
                    (if (improved) SafeGreen else DangerRed).copy(alpha = 0.12f),
                    RoundedCornerShape(20.dp),
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            val sign = if (cmp.scoreDelta >= 0) "+" else ""
            Text(
                "점수 ${sign}${cmp.scoreDelta}점 · 위험 ${if (cmp.riskDelta <= 0) "${cmp.riskDelta}건 감소" else "+${cmp.riskDelta}건 증가"}",
                fontSize = 12.sp, fontWeight = FontWeight.Bold,
                color = if (improved) SafeGreen else DangerRed,
            )
        }
        Spacer(Modifier.height(16.dp))

        // Before
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFECEC), RoundedCornerShape(8.dp))
                .padding(14.dp),
        ) {
            Text("개선 전 (분석 #${cmp.beforeId})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736))
            Spacer(Modifier.height(10.dp))
            Text(
                "${cmp.beforeScore} / 100점",
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DangerRed,
            )
            Spacer(Modifier.height(8.dp))
            if (cmp.persistedItems.isNotEmpty()) {
                Text(
                    cmp.persistedItems.joinToString("  ✗ ", prefix = "✗ "),
                    fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828),
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // After
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFECF7EF), RoundedCornerShape(8.dp))
                .padding(14.dp),
        ) {
            Text("개선 후 (분석 #${cmp.afterId})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736))
            Spacer(Modifier.height(10.dp))
            Text(
                "${cmp.afterScore} / 100점",
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SafeGreen,
            )
            Spacer(Modifier.height(8.dp))
            if (cmp.improvedItems.isNotEmpty()) {
                Text(
                    cmp.improvedItems.joinToString("  ✓ ", prefix = "✓ "),
                    fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SafeGreen,
                )
            }
            if (cmp.newItems.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "신규 발생: " + cmp.newItems.joinToString(", "),
                    fontSize = 12.sp, color = Color(0xFFE58A00),
                )
            }
        }
    }
}

@Composable
private fun SummaryBox(label: String, textColor: Color, bgColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(70.dp)
            .background(bgColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 18.sp, fontWeight = FontWeight.Bold,
            color = textColor, textAlign = TextAlign.Center,
            lineHeight = 26.sp,
        )
    }
}

@Preview(showBackground = true, name = "통계 & 사후관리")
@Composable
private fun AfterCareScreenPreview() {
    SafeTwinTheme {
        AfterCareScreen(vm = AfterCareViewModel())
    }
}

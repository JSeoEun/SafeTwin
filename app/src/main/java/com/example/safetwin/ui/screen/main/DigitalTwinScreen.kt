package com.example.safetwin.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safetwin.data.model.ZoneDetailResponse
import com.example.safetwin.data.model.ZoneResponse
import com.example.safetwin.ui.component.DashboardCard
import androidx.compose.ui.tooling.preview.Preview
import com.example.safetwin.ui.theme.DangerRed
import com.example.safetwin.ui.theme.Primary
import com.example.safetwin.ui.theme.SafeGreen
import com.example.safetwin.ui.theme.SafeTwinTheme
import com.example.safetwin.ui.theme.WarningOrange

@Composable
fun DigitalTwinScreen(vm: DigitalTwinViewModel = viewModel()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FB)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        // 헤더 + 사업장 ID 입력
        item {
            Text("탑뷰 시각화", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736))
            Spacer(Modifier.height(4.dp))
            Text("인터랙티브 현장 도면", fontSize = 14.sp, color = Color(0xFF7A8AA0))
            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = vm.siteIdText,
                    onValueChange = { vm.siteIdText = it },
                    modifier = Modifier.width(100.dp),
                    label = { Text("사업장 ID", fontSize = 12.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1E4DCB),
                        unfocusedBorderColor = Color(0xFFD0D5DD),
                    ),
                )
                Button(
                    onClick = { vm.loadZones() },
                    enabled = !vm.isLoadingZones,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) {
                    Text(if (vm.isLoadingZones) "로딩 중" else "조회", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 에러
        vm.errorMsg?.let {
            item { Text(it, color = DangerRed, fontSize = 13.sp) }
        }

        // 탑뷰 도면 캔버스
        item {
            DashboardCard {
                Text(
                    "인터랙티브 탑뷰 도면",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736),
                )
                Spacer(Modifier.height(14.dp))
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Color(0xFFEEF2F7)),
                ) {
                    val W = maxWidth
                    val H = maxHeight
                    if (vm.isLoadingZones) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center), color = Primary)
                    } else if (vm.zones.isEmpty()) {
                        Text(
                            "구역 정보가 없습니다.",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 13.sp, color = Color(0xFF7A8AA0),
                        )
                    } else {
                        vm.zones.forEach { zone ->
                            val x = (zone.x ?: 0.05).toFloat().coerceIn(0f, 0.85f)
                            val y = (zone.y ?: 0.05).toFloat().coerceIn(0f, 0.8f)
                            val w = (zone.w ?: 0.25).toFloat().coerceIn(0.1f, 0.45f)
                            val h = (zone.h ?: 0.25).toFloat().coerceIn(0.15f, 0.45f)
                            val (bg, border, textColor) = zoneColors(zone.riskLevel)
                            val isSelected = vm.selectedZone?.id == zone.id
                            ZoneBox(
                                label = "${zone.name}\n${riskLevelKo(zone.riskLevel)}",
                                width = W * w,
                                height = H * h,
                                bgColor = if (isSelected) bg.copy(alpha = 0.3f) else bg,
                                borderColor = if (isSelected) border else border.copy(alpha = 0.7f),
                                textColor = textColor,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(W * x, H * y)
                                    .clickable { vm.selectZone(zone.id) },
                            )
                        }
                    }
                }
            }
        }

        // 구역 현황 요약
        if (vm.zones.isNotEmpty()) {
            item {
                DashboardCard {
                    Text(
                        "구역 현황",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736),
                    )
                    Spacer(Modifier.height(10.dp))
                    val highCount   = vm.zones.count { it.riskLevel in listOf("HIGH", "CRITICAL") }
                    val mediumCount = vm.zones.count { it.riskLevel == "MEDIUM" }
                    val lowCount    = vm.zones.count { it.riskLevel == "LOW" || it.riskLevel == null }
                    if (highCount > 0)   Text("■ 고위험 구역 ${highCount}개", color = DangerRed)
                    if (mediumCount > 0) { Spacer(Modifier.height(4.dp)); Text("■ 주의 구역 ${mediumCount}개", color = WarningOrange) }
                    if (lowCount > 0)    { Spacer(Modifier.height(4.dp)); Text("■ 양호 구역 ${lowCount}개", color = SafeGreen) }
                }
            }
        }

        // 선택된 구역 상세
        val detail = vm.selectedZone
        if (detail != null) {
            item { ZoneDetailCard(detail, vm.isLoadingDetail) }
        } else if (vm.zones.isNotEmpty() && !vm.isLoadingZones) {
            item {
                Text(
                    "구역을 클릭하면 상세 정보가 표시됩니다.",
                    fontSize = 13.sp, color = Color(0xFF7A8AA0),
                )
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun ZoneDetailCard(detail: ZoneDetailResponse, isLoading: Boolean) {
    DashboardCard {
        if (isLoading) {
            CircularProgressIndicator(Modifier.size(24.dp), color = Primary)
            return@DashboardCard
        }
        val (_, _, titleColor) = zoneColors(detail.riskLevel)
        Text(detail.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B1736))
        Spacer(Modifier.height(4.dp))
        Text(riskLevelKo(detail.riskLevel), fontWeight = FontWeight.Bold, color = titleColor)
        Spacer(Modifier.height(10.dp))
        detail.description?.let { Text("설명: $it", fontSize = 13.sp, color = Color(0xFF475569)) }
        detail.floorNumber?.let { Text("층: ${it}층", fontSize = 13.sp, color = Color(0xFF475569)) }
        detail.area?.let { Text("구역 특성: $it", fontSize = 13.sp, color = Color(0xFF475569)) }
        Text("위험 요소: ${detail.riskCount}개", fontSize = 13.sp, color = Color(0xFF475569))
        if (detail.riskTags.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            detail.riskTags.forEach { tag ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(DangerRed, RoundedCornerShape(2.dp)),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(tag.label, fontSize = 13.sp, color = Color(0xFF1E293B))
                    tag.law?.let {
                        Text(" · $it", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }
        if (detail.riskLevel in listOf("HIGH", "CRITICAL")) {
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .border(1.5.dp, DangerRed, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text("즉시 개선 필요", color = DangerRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ZoneBox(
    label: String,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    bgColor: Color,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width, height)
            .background(bgColor, RoundedCornerShape(4.dp))
            .border(1.dp, borderColor, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

private fun zoneColors(level: String?): Triple<Color, Color, Color> = when (level) {
    "HIGH", "CRITICAL" -> Triple(DangerRed.copy(alpha = 0.12f),    DangerRed,    DangerRed)
    "MEDIUM"           -> Triple(WarningOrange.copy(alpha = 0.12f), WarningOrange, Color(0xFFE58A00))
    else               -> Triple(SafeGreen.copy(alpha = 0.12f),    SafeGreen,    SafeGreen)
}

private fun riskLevelKo(level: String?): String = when (level) {
    "LOW"      -> "양호"
    "MEDIUM"   -> "주의"
    "HIGH"     -> "위험"
    "CRITICAL" -> "고위험"
    else       -> "양호"
}

@Preview(showBackground = true, name = "탑뷰 시각화")
@Composable
private fun DigitalTwinScreenPreview() {
    SafeTwinTheme {
        DigitalTwinScreen(vm = DigitalTwinViewModel())
    }
}

package com.example.safetwin.ui.screen.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.safetwin.data.model.DocResponse
import com.example.safetwin.ui.component.DashboardCard
import com.example.safetwin.ui.component.PrimaryButton
import androidx.compose.ui.tooling.preview.Preview
import com.example.safetwin.ui.theme.DangerRed
import com.example.safetwin.ui.theme.Primary
import com.example.safetwin.ui.theme.SafeTwinTheme

@Composable
fun LegalScreen(vm: LegalViewModel = viewModel()) {
    val context = LocalContext.current

    // PDF URL을 받으면 브라우저로 열기
    LaunchedEffect(vm.pdfUrlToOpen) {
        vm.pdfUrlToOpen?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            vm.clearPdfUrl()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6FB)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                "법적 증빙 관리",
                fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "고용노동부 기준 양식 자동 생성 · 전자 서명 지원",
                fontSize = 13.sp, color = Color(0xFF64748B),
            )
        }

        // 서명 대기 알림 배지
        if (vm.draftCount > 0) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFFCD34D), RoundedCornerShape(8.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("⊙", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "서명 대기 ${vm.draftCount}건   서명이 완료되지 않은 문서가 있습니다.",
                        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E),
                    )
                }
            }
        }

        // 에러
        vm.errorMsg?.let {
            item { Text(it, color = DangerRed, fontSize = 13.sp) }
        }

        // 위험성 평가서 생성
        item {
            DashboardCard {
                Text(
                    "위험성 평가서 생성",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B),
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = vm.analysisIdText,
                        onValueChange = { vm.analysisIdText = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("분석 ID", fontSize = 12.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E4DCB),
                            unfocusedBorderColor = Color(0xFFD0D5DD),
                        ),
                    )
                    PrimaryButton(
                        text = "생성",
                        onClick = { vm.generateRiskAssessment() },
                        modifier = Modifier.width(80.dp),
                    )
                }
            }
        }

        // 문서 목록
        item {
            DashboardCard {
                Text(
                    "문서 목록",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B),
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF2F7))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                ) {
                    TableHeader("문서 종류", weight = 2f)
                    TableHeader("사업장",   weight = 1.4f)
                    TableHeader("생성일",   weight = 1.5f)
                    TableHeader("상태",     weight = 1f)
                    TableHeader("PDF",     weight = 1f)
                }
                HorizontalDivider(color = Color(0xFFE2E8F0))

                if (vm.isLoadingDocs) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(Modifier.size(24.dp), color = Primary)
                    }
                } else if (vm.docs.isEmpty()) {
                    Text(
                        "문서가 없습니다.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp, color = Color(0xFF64748B),
                    )
                } else {
                    vm.docs.forEach { doc ->
                        DocumentRow(
                            doc = doc,
                            onDownload = { vm.openPdf(doc.id) },
                        )
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                    }
                }
            }
        }

        // 서명 플로우 안내
        item {
            DashboardCard {
                Text(
                    "작업자 서명 플로우",
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B),
                )
                Spacer(Modifier.height(14.dp))
                Text("①  문서 목록에서 서명 대기 문서 선택", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(Modifier.height(12.dp))
                Text("②  서명자 이름 및 서명 데이터 입력",   fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(Modifier.height(12.dp))
                Text("③  서명 등록 후 PDF 재다운로드",       fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(Modifier.height(14.dp))

                // 간단 서명 등록 UI (DRAFT 문서가 있을 때만)
                val firstDraft = vm.docs.firstOrNull { it.status == "DRAFT" }
                if (firstDraft != null) {
                    SignSection(firstDraft, onSign = { signerName, sigData ->
                        vm.signDoc(firstDraft.id, signerName, sigData)
                    })
                }
            }
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun SignSection(
    doc: DocResponse,
    onSign: (signerName: String, signatureData: String) -> Unit,
) {
    var signerName by remember { mutableStateOf("") }
    var sigData    by remember { mutableStateOf("") }

    Text(
        "서명 대기: ${docTypeKo(doc.type)}",
        fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706),
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = signerName,
        onValueChange = { signerName = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("서명자 이름", fontSize = 12.sp) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1E4DCB),
            unfocusedBorderColor = Color(0xFFD0D5DD),
        ),
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = sigData,
        onValueChange = { sigData = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("서명 데이터 (Base64)", fontSize = 12.sp) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1E4DCB),
            unfocusedBorderColor = Color(0xFFD0D5DD),
        ),
    )
    Spacer(Modifier.height(10.dp))
    PrimaryButton(
        text = "서명 등록",
        onClick = { if (signerName.isNotBlank() && sigData.isNotBlank()) onSign(signerName, sigData) },
    )
}

@Composable
private fun DocumentRow(doc: DocResponse, onDownload: () -> Unit) {
    val (statusText, statusColor) = when (doc.status) {
        "SIGNED" -> "완료" to Color(0xFF16A34A)
        else     -> "서명 대기" to Color(0xFFD97706)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            docTypeKo(doc.type),
            modifier = Modifier.weight(2f),
            fontSize = 12.sp, color = Color(0xFF1E293B),
        )
        Text(
            doc.siteName ?: "-",
            modifier = Modifier.weight(1.4f),
            fontSize = 12.sp, color = Color(0xFF475569),
        )
        Text(
            doc.createdAt.take(10),
            modifier = Modifier.weight(1.5f),
            fontSize = 12.sp, color = Color(0xFF475569),
        )
        Text(
            statusText,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp, fontWeight = FontWeight.Bold, color = statusColor,
        )
        Text(
            "↓",
            modifier = Modifier
                .weight(1f)
                .clickable { onDownload() },
            fontSize = 14.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TableHeader(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569),
    )
}

private fun docTypeKo(type: String): String = when (type) {
    "RISK_ASSESSMENT"   -> "위험성평가 결과서"
    "EDUCATION_CERT"    -> "교육 실시 확인서"
    "SAFETY_PLAN"       -> "안전보건관리계획서"
    "INSPECTION_REPORT" -> "점검 보고서"
    "GROUP_PHOTO"       -> "단체 사진"
    else                -> type
}

@Preview(showBackground = true, name = "법적 증빙")
@Composable
private fun LegalScreenPreview() {
    SafeTwinTheme {
        LegalScreen(vm = LegalViewModel())
    }
}

package com.example.safetwin.ui.screen.main

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetwin.data.model.CreateRiskAssessmentRequest
import com.example.safetwin.data.model.DocResponse
import com.example.safetwin.data.model.SignRequest
import com.example.safetwin.data.network.ApiClient
import kotlinx.coroutines.launch

class LegalViewModel : ViewModel() {
    var docs          by mutableStateOf<List<DocResponse>>(emptyList())
        private set
    val draftCount    by derivedStateOf { docs.count { it.status == "DRAFT" } }
    var isLoadingDocs by mutableStateOf(false)
        private set
    var errorMsg      by mutableStateOf<String?>(null)
    var pdfUrlToOpen  by mutableStateOf<String?>(null)
        private set
    var analysisIdText by mutableStateOf("")

    init {
        loadDocs()
    }

    fun loadDocs() {
        viewModelScope.launch {
            isLoadingDocs = true
            errorMsg = null
            try {
                val response = ApiClient.api.getDocs(page = 0, size = 20)
                if (response.success && response.data != null) {
                    docs = response.data.content
                }
            } catch (e: Exception) {
                errorMsg = "문서 목록을 불러올 수 없습니다."
            } finally {
                isLoadingDocs = false
            }
        }
    }

    fun openPdf(docId: Long) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.downloadPdf(docId)
                pdfUrlToOpen = response.raw().request.url.toString()
                response.body()?.close()
            } catch (e: Exception) {
                errorMsg = "PDF를 열 수 없습니다."
            }
        }
    }

    fun clearPdfUrl() {
        pdfUrlToOpen = null
    }

    fun generateRiskAssessment() {
        val analysisId = analysisIdText.toLongOrNull()
        if (analysisId == null) {
            errorMsg = "분석 ID를 입력해주세요."
            return
        }
        viewModelScope.launch {
            try {
                val response = ApiClient.api.createRiskAssessment(CreateRiskAssessmentRequest(analysisId))
                if (response.success && response.data != null) {
                    loadDocs()
                }
            } catch (e: Exception) {
                errorMsg = "문서 생성에 실패했습니다."
            }
        }
    }

    fun signDoc(docId: Long, signerName: String, signatureData: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.api.signDoc(docId, SignRequest(signerName, signatureData))
                if (response.success) {
                    loadDocs()
                }
            } catch (e: Exception) {
                errorMsg = "서명 등록에 실패했습니다."
            }
        }
    }
}

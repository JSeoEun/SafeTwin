package com.example.safetwin.ui.screen.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetwin.data.model.AnalysisResponse
import com.example.safetwin.data.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AnalysisViewModel : ViewModel() {
    var analyses       by mutableStateOf<List<AnalysisResponse>>(emptyList())
        private set
    var isLoadingList  by mutableStateOf(false)
        private set
    var isUploading    by mutableStateOf(false)
        private set
    var pollingStatus  by mutableStateOf<String?>(null)
        private set
    var errorMsg       by mutableStateOf<String?>(null)

    private var pollingJob: Job? = null

    init {
        loadAnalyses()
    }

    fun loadAnalyses(siteId: Long? = null) {
        viewModelScope.launch {
            isLoadingList = true
            errorMsg = null
            try {
                val response = ApiClient.api.getAnalyses(siteId = siteId, page = 0, size = 20)
                if (response.success && response.data != null) {
                    analyses = response.data.content
                }
            } catch (e: Exception) {
                errorMsg = "분석 목록을 불러올 수 없습니다."
            } finally {
                isLoadingList = false
            }
        }
    }

    fun uploadImage(zoneId: Long, imageBytes: ByteArray) {
        viewModelScope.launch {
            isUploading = true
            pollingStatus = "업로드 중..."
            errorMsg = null
            try {
                val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaType())
                val part = MultipartBody.Part.createFormData("image", "photo.jpg", requestBody)
                val response = ApiClient.api.startAnalysis(zoneId, part)
                if (response.success && response.data != null) {
                    pollingStatus = "분석 대기 중..."
                    pollStatus(response.data.id)
                } else {
                    isUploading = false
                    pollingStatus = null
                    errorMsg = "이미지 업로드에 실패했습니다."
                }
            } catch (e: Exception) {
                isUploading = false
                pollingStatus = null
                errorMsg = "이미지 업로드에 실패했습니다."
            }
        }
    }

    private fun pollStatus(analysisId: Long) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(3_000)
                try {
                    val resp = ApiClient.api.getAnalysisStatus(analysisId)
                    if (resp.success && resp.data != null) {
                        pollingStatus = when (resp.data.status) {
                            "PENDING"     -> "분석 대기 중..."
                            "IN_PROGRESS" -> "AI 분석 중..."
                            "COMPLETED"   -> "분석 완료"
                            "FAILED"      -> "분석 실패"
                            else          -> resp.data.status
                        }
                        when (resp.data.status) {
                            "COMPLETED" -> {
                                isUploading = false
                                loadAnalyses()
                                break
                            }
                            "FAILED" -> {
                                isUploading = false
                                errorMsg = "분석에 실패했습니다."
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    // 일시적 오류는 무시하고 재시도
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}

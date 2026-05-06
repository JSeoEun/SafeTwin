package com.example.safetwin.ui.screen.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetwin.data.model.CompareResponse
import com.example.safetwin.data.model.ScoreTrendResponse
import com.example.safetwin.data.network.ApiClient
import kotlinx.coroutines.launch

class AfterCareViewModel : ViewModel() {
    var trend            by mutableStateOf<ScoreTrendResponse?>(null)
        private set
    var compare          by mutableStateOf<CompareResponse?>(null)
        private set
    var isLoadingTrend   by mutableStateOf(false)
        private set
    var isLoadingCompare by mutableStateOf(false)
        private set
    var errorMsg         by mutableStateOf<String?>(null)
    var beforeIdText     by mutableStateOf("")
    var afterIdText      by mutableStateOf("")

    init {
        loadTrend()
    }

    fun loadTrend(period: String = "monthly") {
        viewModelScope.launch {
            isLoadingTrend = true
            errorMsg = null
            try {
                val response = ApiClient.api.getScoreTrend(period)
                if (response.success && response.data != null) {
                    trend = response.data
                }
            } catch (e: Exception) {
                errorMsg = "통계를 불러올 수 없습니다."
            } finally {
                isLoadingTrend = false
            }
        }
    }

    fun loadCompare() {
        val beforeId = beforeIdText.toLongOrNull()
        val afterId  = afterIdText.toLongOrNull()
        if (beforeId == null || afterId == null) {
            errorMsg = "분석 ID를 올바르게 입력해주세요."
            return
        }
        viewModelScope.launch {
            isLoadingCompare = true
            errorMsg = null
            try {
                val response = ApiClient.api.compareAnalyses(beforeId, afterId)
                if (response.success && response.data != null) {
                    compare = response.data
                }
            } catch (e: Exception) {
                errorMsg = "비교 분석을 불러올 수 없습니다."
            } finally {
                isLoadingCompare = false
            }
        }
    }
}

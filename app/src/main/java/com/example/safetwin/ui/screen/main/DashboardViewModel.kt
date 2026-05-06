package com.example.safetwin.ui.screen.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetwin.data.model.DashboardSummary
import com.example.safetwin.data.network.ApiClient
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val data: DashboardSummary) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel : ViewModel() {
    var uiState by mutableStateOf<DashboardUiState>(DashboardUiState.Loading)
        private set

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        uiState = DashboardUiState.Loading
        viewModelScope.launch {
            try {
                val response = ApiClient.api.getDashboardSummary()
                uiState = if (response.success && response.data != null) {
                    DashboardUiState.Success(response.data)
                } else {
                    DashboardUiState.Error("데이터를 불러올 수 없습니다.")
                }
            } catch (e: Exception) {
                uiState = DashboardUiState.Error("네트워크 오류가 발생했습니다.")
            }
        }
    }
}

package com.example.safetwin.ui.screen.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetwin.data.model.ZoneDetailResponse
import com.example.safetwin.data.model.ZoneResponse
import com.example.safetwin.data.network.ApiClient
import kotlinx.coroutines.launch

class DigitalTwinViewModel : ViewModel() {
    var zones          by mutableStateOf<List<ZoneResponse>>(emptyList())
        private set
    var selectedZone   by mutableStateOf<ZoneDetailResponse?>(null)
        private set
    var isLoadingZones by mutableStateOf(false)
        private set
    var isLoadingDetail by mutableStateOf(false)
        private set
    var errorMsg       by mutableStateOf<String?>(null)
    var siteIdText     by mutableStateOf("1")

    init {
        loadZones()
    }

    fun loadZones(siteId: Long = siteIdText.toLongOrNull() ?: 1L) {
        viewModelScope.launch {
            isLoadingZones = true
            errorMsg = null
            try {
                val response = ApiClient.api.getZones(siteId)
                if (response.success && response.data != null) {
                    zones = response.data
                    selectedZone = null
                }
            } catch (e: Exception) {
                errorMsg = "구역 목록을 불러올 수 없습니다."
            } finally {
                isLoadingZones = false
            }
        }
    }

    fun selectZone(zoneId: Long) {
        viewModelScope.launch {
            isLoadingDetail = true
            try {
                val response = ApiClient.api.getZone(zoneId)
                if (response.success && response.data != null) {
                    selectedZone = response.data
                }
            } catch (e: Exception) {
                errorMsg = "구역 상세를 불러올 수 없습니다."
            } finally {
                isLoadingDetail = false
            }
        }
    }
}

package com.example.safetwin.ui.screen.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetwin.data.local.TokenManager
import com.example.safetwin.data.model.ApiError
import com.example.safetwin.data.model.LoginRequest
import com.example.safetwin.data.network.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel : ViewModel() {
    var email     by mutableStateOf("")
    var password  by mutableStateOf("")
    var errorMsg  by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun login(onSuccess: () -> Unit) {
        errorMsg = when {
            email.isBlank()    -> "이메일을 입력해주세요."
            password.isBlank() -> "비밀번호를 입력해주세요."
            else               -> null
        }
        if (errorMsg != null) return

        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.api.login(LoginRequest(email, password))
                if (response.success && response.data != null) {
                    TokenManager.saveTokens(
                        response.data.accessToken,
                        response.data.refreshToken,
                    )
                    onSuccess()
                }
            } catch (e: HttpException) {
                val apiError = parseError(e)
                errorMsg = when (apiError?.code) {
                    "INVALID_CREDENTIALS" -> "이메일 또는 비밀번호가 올바르지 않습니다."
                    else                  -> "로그인 중 오류가 발생했습니다."
                }
            } catch (e: Exception) {
                errorMsg = "네트워크 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    private fun parseError(e: HttpException): ApiError? = try {
        Gson().fromJson(e.response()?.errorBody()?.string(), ApiError::class.java)
    } catch (_: Exception) {
        null
    }
}

package com.example.safetwin.ui.screen.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safetwin.data.local.TokenManager
import com.example.safetwin.data.model.ApiError
import com.example.safetwin.data.model.BizVerifyRequest
import com.example.safetwin.data.model.SignUpRequest
import com.example.safetwin.data.network.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignUpViewModel : ViewModel() {
    var businessNumber     by mutableStateOf("")
    var isBusinessVerified by mutableStateOf(false)
    var companyName        by mutableStateOf<String?>(null)
    var name               by mutableStateOf("")
    var email              by mutableStateOf("")
    var password           by mutableStateOf("")
    var phone              by mutableStateOf("")
    var errorMsg           by mutableStateOf<String?>(null)
    var isLoading          by mutableStateOf(false)
    var isVerifying        by mutableStateOf(false)

    fun verifyBiz() {
        val digits = businessNumber.replace("-", "").trim()
        if (digits.isBlank()) {
            errorMsg = "사업자 등록번호를 입력해주세요."
            return
        }
        viewModelScope.launch {
            isVerifying = true
            errorMsg = null
            try {
                val response = ApiClient.api.bizVerify(BizVerifyRequest(digits))
                if (response.success && response.data != null) {
                    if (response.data.valid) {
                        isBusinessVerified = true
                        companyName = response.data.companyName
                    } else {
                        isBusinessVerified = false
                        errorMsg = "유효하지 않은 사업자 번호입니다."
                    }
                }
            } catch (e: HttpException) {
                errorMsg = "사업자 인증 중 오류가 발생했습니다."
            } catch (e: Exception) {
                errorMsg = "네트워크 오류가 발생했습니다."
            } finally {
                isVerifying = false
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        errorMsg = when {
            name.isBlank()      -> "이름을 입력해주세요."
            email.isBlank()     -> "이메일을 입력해주세요."
            password.length < 8 -> "비밀번호는 최소 8자 이상이어야 합니다."
            else                -> null
        }
        if (errorMsg != null) return

        viewModelScope.launch {
            isLoading = true
            try {
                val response = ApiClient.api.signUp(
                    SignUpRequest(
                        email = email,
                        password = password,
                        name = name,
                        phone = phone.ifBlank { null },
                        bizNumber = businessNumber.replace("-", "").ifBlank { null },
                    )
                )
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
                    "DUPLICATE_EMAIL"  -> "이미 가입된 이메일입니다."
                    "VALIDATION_ERROR" -> "입력 정보를 확인해주세요."
                    else               -> "회원가입 중 오류가 발생했습니다."
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

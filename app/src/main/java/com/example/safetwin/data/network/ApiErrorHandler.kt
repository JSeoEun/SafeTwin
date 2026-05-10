package com.example.safetwin.data.network

import com.example.safetwin.data.model.ApiError
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

object ApiErrorHandler {

    fun errorMessage(code: String?): String = when (code) {
        "DUPLICATE_EMAIL"        -> "이미 가입된 이메일입니다."
        "SITE_NOT_FOUND"         -> "사업장을 찾을 수 없습니다."
        "WORKER_NOT_FOUND"       -> "근로자를 찾을 수 없습니다."
        "RISK_NOT_FOUND"         -> "위험 요소를 찾을 수 없습니다."
        "INVALID_CREDENTIALS"    -> "이메일 또는 비밀번호가 올바르지 않습니다."
        "EXPIRED_TOKEN"          -> "세션이 만료되었습니다. 다시 로그인해주세요."
        "INVALID_TOKEN"          -> "인증 정보가 올바르지 않습니다."
        "UNAUTHORIZED"           -> "접근 권한이 없습니다."
        "RESOURCE_NOT_FOUND"     -> "요청한 리소스를 찾을 수 없습니다."
        "VALIDATION_ERROR"       -> "입력 정보를 확인해주세요."
        "FILE_SIZE_EXCEEDED"     -> "파일 크기가 20MB를 초과합니다."
        "UNSUPPORTED_FILE_TYPE"  -> "JPG, PNG, HEIC, HEIF 형식만 지원합니다."
        "ANALYSIS_NOT_COMPLETED" -> "분석이 아직 완료되지 않았습니다."
        "INTERNAL_SERVER_ERROR"  -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        else                     -> "오류가 발생했습니다."
    }

    fun handle(e: Exception): String = when (e) {
        is IOException   -> "네트워크 연결을 확인해주세요."
        is HttpException -> {
            if (e.code() >= 500) "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            else {
                val body = try {
                    Gson().fromJson(e.response()?.errorBody()?.string(), ApiError::class.java)
                } catch (_: Exception) { null }
                errorMessage(body?.code)
            }
        }
        else -> "오류가 발생했습니다."
    }
}

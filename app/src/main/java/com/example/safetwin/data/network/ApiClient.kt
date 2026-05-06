package com.example.safetwin.data.network

import com.example.safetwin.data.local.SessionManager
import com.example.safetwin.data.local.TokenManager
import com.example.safetwin.data.model.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://114.205.188.138:8080/"

    private val tokenInterceptor = Interceptor { chain ->
        val token = TokenManager.getAccessToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val refreshInterceptor = TokenRefreshInterceptor()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(tokenInterceptor)
        .addInterceptor(refreshInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

private class TokenRefreshInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val response = chain.proceed(original)

        if (response.code != 401) return response

        val refreshToken = TokenManager.getRefreshToken() ?: run {
            TokenManager.clearTokens()
            return response
        }

        response.close()

        val newToken = runBlocking {
            try {
                val refreshResponse = ApiClient.api.refresh(RefreshRequest(refreshToken))
                if (refreshResponse.success && refreshResponse.data != null) {
                    TokenManager.saveTokens(
                        refreshResponse.data.accessToken,
                        refreshResponse.data.refreshToken,
                    )
                    refreshResponse.data.accessToken
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        return if (newToken != null) {
            val retryRequest = original.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
            chain.proceed(retryRequest)
        } else {
            TokenManager.clearTokens()
            SessionManager.onSessionExpired()
            chain.proceed(original)
        }
    }
}

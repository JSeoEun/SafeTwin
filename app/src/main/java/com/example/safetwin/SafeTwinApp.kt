package com.example.safetwin

import android.app.Application
import com.example.safetwin.data.local.TokenManager

class SafeTwinApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}

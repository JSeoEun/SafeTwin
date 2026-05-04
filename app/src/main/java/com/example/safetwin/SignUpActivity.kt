package com.example.safetwin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val businessNumberEditText =
            findViewById<EditText>(R.id.businessNumberEditText)

        val verifyStatusText =
            findViewById<TextView>(R.id.verifyStatusText)

        val businessCheckGuide =
            findViewById<TextView>(R.id.businessCheckGuide)

        val loginTab = findViewById<TextView>(R.id.loginTab)

        loginTab.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 뒤로가기 시 돌아오지 않게
        }

        // 처음에는 숨김
        verifyStatusText.visibility = View.GONE
        businessCheckGuide.visibility = View.GONE

        businessNumberEditText.addTextChangedListener { text ->
            val input = text.toString()

            if (input.isNotEmpty()) {
                // 입력되면 → 확인됨 표시
                verifyStatusText.visibility = View.VISIBLE
                businessCheckGuide.visibility = View.VISIBLE
            } else {
                // 입력 없으면 → 숨김
                verifyStatusText.visibility = View.GONE
                businessCheckGuide.visibility = View.GONE
            }
        }
    }
}
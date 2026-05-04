package com.example.safetwin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        val signupTab = findViewById<TextView>(R.id.signupTab)
        val signupLink = findViewById<TextView>(R.id.signupLink)

        signupTab.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            when {
                email.isEmpty() -> {
                    Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }

                password.isEmpty() -> {
                    Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fragment", "dashboard") // ⭐ 핵심
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

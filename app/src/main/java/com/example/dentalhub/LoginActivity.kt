package com.example.dentalhub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupUI()
    }

    private fun setupUI() {
        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        finish();
    }
}
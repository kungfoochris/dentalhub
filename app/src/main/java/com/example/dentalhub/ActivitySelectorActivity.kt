package com.example.dentalhub

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ActivitySelectorActivity : AppCompatActivity() {

    private lateinit var btnGo: Button
    private lateinit var btnLogout: Button
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this
        initUI()
    }

    private fun initUI() {
        btnGo = findViewById(R.id.btnGo)
        btnLogout = findViewById(R.id.btnLogout)

        btnGo.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            DentalApp.clearAuthDetails(context)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

package com.example.dentalhub

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ActivitySelectorActivity : AppCompatActivity() {

    private lateinit var btnGo: Button
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this
        initUI()
    }

    private fun initUI() {
        btnGo = findViewById<Button>(R.id.btnGo)
        btnGo.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

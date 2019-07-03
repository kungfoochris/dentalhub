package com.example.dentalhub

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import com.example.dentalhub.utils.AdapterHelper

class SelectorActivity : AppCompatActivity() {
    private lateinit var spinnerLocation: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var btnGo: Button
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this
        initUI()
    }

    private fun initUI() {
        spinnerLocation = findViewById(R.id.spinnerLocation)
        spinnerActivity = findViewById(R.id.spinnerActivity)
        btnGo = findViewById(R.id.btnGo)

        val locations = listOf<String>("Kathmandu", "Bhaktapur", "Lalitpur")
        val activities = listOf<String>("Health Post", "School Seminar", "Community Outreach", "Training")
        spinnerLocation.adapter = AdapterHelper.createAdapter(context, locations)
        spinnerActivity.adapter = AdapterHelper.createAdapter(context, activities)

        btnGo.setOnClickListener {
            DentalApp.geography = spinnerLocation.selectedItem.toString()
            DentalApp.activity = spinnerActivity.selectedItem.toString()

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

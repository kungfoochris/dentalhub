package com.example.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ViewPatientActivity: AppCompatActivity(){
    private lateinit var btnAddNewEncounter: Button
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_patient)
        context = this
        initUI()
    }

    private fun initUI() {
        btnAddNewEncounter = findViewById(R.id.btnAddNewEncounter)
        btnAddNewEncounter.setOnClickListener {
            startActivity(Intent(context, AddEncounterActivity::class.java))
        }
    }
}
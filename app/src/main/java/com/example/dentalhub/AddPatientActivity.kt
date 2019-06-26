package com.example.dentalhub

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity

class AddPatientActivity : AppCompatActivity(){

    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerMaritalStatus: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        context = this
        initUI()
    }

    private fun initUI() {

        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerMaritalStatus = findViewById(R.id.spinnerMartialStatus)
        spinnerEducationLevel = findViewById(R.id.spinnerEducationLevel)
        spinnerGender.adapter = createAdapter(resources.getStringArray(R.array.gender_list).toList())
        spinnerMaritalStatus.adapter = createAdapter(resources.getStringArray(R.array.martial_status_list).toList())
        spinnerEducationLevel.adapter = createAdapter(resources.getStringArray(R.array.education_level_list).toList())
    }

    private fun createAdapter(values: List<String>): SpinnerAdapter? {
        var dataAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values!!.toMutableList())
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return dataAdapter
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
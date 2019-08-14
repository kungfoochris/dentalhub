package com.example.dentalhub

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.core.view.get

class ActivitySelectorActivity : AppCompatActivity() {

    private lateinit var btnGo: Button
    private lateinit var btnLogout: Button
    private lateinit var context: Context

    private lateinit var rgActivities : RadioGroup
    private lateinit var etOtherDetails: EditText
    var selectedActivity = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this
        initUI()
    }

    private fun initUI() {
        rgActivities = findViewById(R.id.rgActivities)
        etOtherDetails = findViewById(R.id.etOtherDetails)
        btnGo = findViewById(R.id.btnGo)
        btnLogout = findViewById(R.id.btnLogout)

        rgActivities.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.radioHealthPost) {
                etOtherDetails.setText("")
                etOtherDetails.visibility = View.GONE
            } else {
                etOtherDetails.visibility = View.VISIBLE
            }
            when(i){
                R.id.radioHealthPost -> selectedActivity = "0"
                R.id.radioSchoolSeminar -> selectedActivity = "1"
                R.id.radioCommunityOutreach -> selectedActivity = "2"
                R.id.radioTraining -> selectedActivity = "3"
            }


        }

        btnGo.setOnClickListener {
            DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_REMARKS,etOtherDetails.text.toString())
            DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_NAME, selectedActivity)
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

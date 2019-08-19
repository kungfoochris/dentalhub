package com.example.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.interfaces.DjangoInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.dentalhub.models.Activity as ActivityModel


class ActivitySelectorActivity : AppCompatActivity() {

    private lateinit var btnGo: Button
    private lateinit var btnLogout: Button
    private lateinit var context: Context

    private lateinit var rgActivities: RadioGroup
    private lateinit var radioHealthPost: RadioButton
    private lateinit var radioSchoolSeminar: RadioButton
    private lateinit var radioCommunityOutreach: RadioButton
    private lateinit var radioTraining: RadioButton

    private lateinit var etOtherDetails: EditText
    var selectedActivity = ""
    var activityOtherDetail = ""

    var TAG = "ActivitySelectorActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this
        initUI()
    }

    private fun initUI() {
        rgActivities = findViewById(R.id.rgActivities)
        etOtherDetails = findViewById(R.id.etOtherDetailsActivity)
        btnGo = findViewById(R.id.btnGo)
        btnLogout = findViewById(R.id.btnLogout)

        radioHealthPost = findViewById(R.id.radioHealthPostActivity)
        radioSchoolSeminar = findViewById(R.id.radioSchoolSeminar)
        radioCommunityOutreach = findViewById(R.id.radioCommunityOutreach)
        radioTraining = findViewById(R.id.radioTraining)

        rgActivities.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.radioHealthPostActivity) {
                etOtherDetails.setText("")
                etOtherDetails.visibility = View.GONE
            } else {
                etOtherDetails.visibility = View.VISIBLE
            }
            when (i) {
                R.id.radioHealthPostActivity -> {
                    selectedActivity = "Health Post"
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioSchoolSeminar -> {
                    selectedActivity = "School Seminar"
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioCommunityOutreach -> {
                    selectedActivity = "Community Outreach"
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioTraining -> {
                    selectedActivity = "Training"
                    println("Selected Activity is $selectedActivity")
                }
            }
        }

        btnGo.setOnClickListener {
            if (isFormValid()) {
                DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_REMARKS, etOtherDetails.text.toString())
                DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_NAME, selectedActivity)
                if (selectedActivity == "Health Post") {
                    saveToServerNewActivity()
                } else {
                    if (!etOtherDetails.text.isNullOrEmpty()) saveToServerNewActivity()
                    else Toast.makeText(context, "Please fill other details.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please select a activity.", Toast.LENGTH_SHORT).show()
            }
        }
        btnLogout.setOnClickListener {
            DentalApp.clearAuthDetails(context)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun saveToServerNewActivity() {
        Log.d(TAG, "saveToServerNewActivity()")
        println("Selected seminar in save to server $selectedActivity")
        val token = DentalApp.readFromPreference(this, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addActivity("JWT $token", selectedActivity, etOtherDetails.text.toString())
        call.enqueue(object : Callback<ActivityModel> {
            override fun onFailure(call: Call<ActivityModel>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }
            override fun onResponse(
                call: Call<ActivityModel>,
                response: Response<ActivityModel>
            ) {
                println("Response code is ${response.code()}")
                when (response.code()) {
                    200 -> {
                        val serverActivity = response.body() as ActivityModel
                        DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_NAME, serverActivity.id)
                        DentalApp.activity = serverActivity.id
                        println("Got the post method. ${serverActivity.id}")
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                    400 -> {
                        Toast.makeText(context, "Fail to create the activity.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Unknown problem faced.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun isFormValid(): Boolean {
        var status = false
        if (rgActivities.checkedRadioButtonId != -1) status = true
        return status
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

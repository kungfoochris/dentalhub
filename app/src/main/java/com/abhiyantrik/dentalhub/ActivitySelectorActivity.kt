package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.abhiyantrik.dentalhub.models.Activity as ActivityModel


class ActivitySelectorActivity : AppCompatActivity() {

    private lateinit var btnGo: Button
    private lateinit var btnLogout: Button
    private lateinit var context: Context

    private lateinit var rgActivities: RadioGroup
    private lateinit var radioHealthPost: RadioButton
    private lateinit var radioSchoolSeminar: RadioButton
    private lateinit var radioCommunityOutreach: RadioButton
    private lateinit var radioTraining: RadioButton

    private lateinit var progressBar: ProgressBar

    private lateinit var etOtherDetails: EditText
    var selectedActivity = ""
    var activityOtherDetail = ""
    var healthpost_id = ""
    var school_seminar_id = ""
    var communityoutreach_id = ""
    var training_id = ""
    var allAPIActivities = listOf<ActivityModel>()

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
        progressBar = findViewById(R.id.progressbarActivitySelector)

        radioHealthPost = findViewById(R.id.radioHealthPostActivity)
        radioSchoolSeminar = findViewById(R.id.radioSchoolSeminar)
        radioCommunityOutreach = findViewById(R.id.radioCommunityOutreach)
        radioTraining = findViewById(R.id.radioTraining)

        // load the id and name of the activity
        loadActivityId()

        rgActivities.setOnCheckedChangeListener { radioGroup, i ->
//            if (i == R.id.radioHealthPostActivity) {
//                etOtherDetails.setText("")
//                etOtherDetails.visibility = View.GONE
//            } else {
//                etOtherDetails.visibility = View.VISIBLE
//            }
            when (i) {
                R.id.radioHealthPostActivity -> {
                    selectedActivity = "Health Post"
                    DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_ID, healthpost_id)
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioSchoolSeminar -> {
                    selectedActivity = "School Seminar"
                    DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_ID, school_seminar_id)
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioCommunityOutreach -> {
                    selectedActivity = "Community Outreach"
                    DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_ID, communityoutreach_id)
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioTraining -> {
                    selectedActivity = "Training"
                    DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_ID, training_id)
                    println("Selected Activity is $selectedActivity")
                }
            }
        }

        btnGo.setOnClickListener {
            if (isFormValid()) {
                DentalApp.saveToPreference(
                    context,
                    Constants.PREF_ACTIVITY_REMARKS,
                    etOtherDetails.text.toString()
                )
                DentalApp.saveToPreference(context, Constants.PREF_ACTIVITY_NAME, selectedActivity)
                if (selectedActivity == "Health Post") {
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
//                } else {
//                    if (!etOtherDetails.text.isNullOrEmpty()) saveToServerNewActivity()
//                    else Toast.makeText(
//                        context,
//                        "Please fill other details.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
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

    private fun loadActivityId() {
        Log.d(TAG, "loadActivityId()")
        val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(context)
        val call = panelService.listActivityEvents("JWT $token")
        call.enqueue(object : Callback<List<ActivityModel>> {
            override fun onFailure(call: Call<List<ActivityModel>>, t: Throwable) {
                Log.d("loadActivityId()", "onFailure")
            }

            override fun onResponse(call: Call<List<ActivityModel>>, response: Response<List<ActivityModel>>) {
                when (response.code()) {
                    200 -> {
                        println("Activity list is ${response.body()}")
                        allAPIActivities = response.body() as List<ActivityModel>
                        for (eachActivity in allAPIActivities) {
                            when (eachActivity.name) {
                                "Community Outreach" -> communityoutreach_id = eachActivity.id
                                "Health Post" -> healthpost_id = eachActivity.id
                                "School Seminar" -> school_seminar_id = eachActivity.id
                                "Training" -> training_id = eachActivity.id
                            }
                        }
                        println("Community $communityoutreach_id health $healthpost_id school $school_seminar_id train $training_id")
                    }
                    400 -> {
                        Log.d("loadActivityId()", "400 error found.")
                        progressBar.visibility = View.GONE
                        Toast.makeText(context, "Failed to fetch.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.d("loadActivityId()", "Unhandled exception")
                        progressBar.visibility = View.GONE
                        Toast.makeText(context, "Failed to connect.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    private fun saveToServerNewActivity() {
        Log.d(TAG, "saveToServerNewActivity()")
        println("Selected seminar in save to server $selectedActivity")
        val token = DentalApp.readFromPreference(this, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call =
            panelService.addActivity("JWT $token", selectedActivity, etOtherDetails.text.toString())
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
                        DentalApp.saveToPreference(
                            context,
                            Constants.PREF_ACTIVITY_NAME,
                            serverActivity.id
                        )
                        DentalApp.activity_id = serverActivity.id
                        println("Got the post method. ${serverActivity.id}")
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                    400 -> {
                        Toast.makeText(context, "Fail to create the activity.", Toast.LENGTH_SHORT)
                            .show()
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

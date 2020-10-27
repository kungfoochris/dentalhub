package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.abhiyantrik.dentalhub.entities.Activity
import com.abhiyantrik.dentalhub.entities.Activity_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.ActivitySuggestion
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hornet.dateconverter.DateConverter
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_selector.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*
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

    lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var etOtherDetails: AutoCompleteTextView
    var selectedActivity = ""
    var selectedActivityId = ""
    var healthpost_id = ""
    var school_seminar_id = ""
    var communityoutreach_id = ""
    var training_id = ""
    var allAPIActivities = listOf<ActivityModel>()
    private var backDateSelected = DateHelper.getTodaysNepaliDate()

    private lateinit var activityBox: Box<Activity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this
        initUI()
        Log.d(TAG, "Backdate of today is $backDateSelected")
    }

    private fun initUI() {

        activityBox = ObjectBox.boxStore.boxFor(Activity::class.java)

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
        loadActivitySuggestions()

        Log.d(TAG, DentalApp.activitySuggestions.toString())

        arrayAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, DentalApp.activitySuggestions.toTypedArray()
        )
        etOtherDetails.threshold = 1
        etOtherDetails.setAdapter(arrayAdapter)
        arrayAdapter.notifyDataSetChanged()

        rgActivities.setOnCheckedChangeListener { _, i ->
            if (i == R.id.radioHealthPostActivity) {
                etOtherDetails.setText("")
                etOtherDetails.visibility = View.GONE
            } else {
                etOtherDetails.visibility = View.VISIBLE
            }
            when (i) {
                R.id.radioHealthPostActivity -> {
                    selectedActivity = HEALTH_POST
                    selectedActivityId = healthpost_id
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioSchoolSeminar -> {
                    selectedActivity = "School Seminar"
                    selectedActivityId = school_seminar_id
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioCommunityOutreach -> {
                    selectedActivity = "Community Outreach"
                    selectedActivityId = communityoutreach_id
                    println("Selected Activity is $selectedActivity")
                }
                R.id.radioTraining -> {
                    selectedActivity = "Training"
                    selectedActivityId = training_id
                    println("Selected Activity is $selectedActivity")
                }
            }
        }

        etBackdate.setOnClickListener {
            val someDate: Calendar = GregorianCalendar.getInstance()
            someDate.add(Calendar.DAY_OF_YEAR, -7)
            val nepaliDateConverter = DateConverter()
            val dpd =
                com.hornet.dateconverter.DatePicker.DatePickerDialog.newInstance { view, year, monthOfYear, dayOfMonth ->
                    val month = DecimalFormat("00").format(monthOfYear + 1).toString()
                    val day = DecimalFormat("00").format(dayOfMonth).toString()
                    val backDate = "$year-$month-$day"
                    if(backDate.isNotEmpty()){
                        etBackdate.setText(DateHelper.getReadableNepaliDate(backDate))
                        Log.d(TAG, "Date $year-$month-$day")
                        backDateSelected = backDate
                    }
                }
            val beforeSevenDays = nepaliDateConverter.getNepaliDate(someDate)
            dpd.setMinDate(beforeSevenDays)
            dpd.setMaxDate(nepaliDateConverter.todayNepaliDate)
            dpd.show(supportFragmentManager, "Backdate")
        }

//        etBackdate.setOnFocusChangeListener { _, b ->
//            if (b) {
//                val nepaliDateConverter = DateConverter()
//                val dpd =
//                    com.hornet.dateconverter.DatePicker.DatePickerDialog.newInstance { view, year, monthOfYear, dayOfMonth ->
//                        val month = DecimalFormat("00").format(monthOfYear + 1).toString()
//                        val day = DecimalFormat("00").format(dayOfMonth).toString()
//                        val recallDate = "$year-$month-$day"
//                        if(recallDate.isNotEmpty()){
//                            etBackdate.setText(DateHelper.getReadableNepaliDate(recallDate))
//                        }
//                    }
//                dpd.setMinDate(nepaliDateConverter.todayNepaliDate)
//                dpd.show(supportFragmentManager, "Backdate")
//
//            }
//        }

        btnGo.setOnClickListener {
            if (isFormValid()) {

                DentalApp.activity_name = selectedActivity
                DentalApp.addStringToPreference(context, etOtherDetails.text.toString())

                if (selectedActivity == HEALTH_POST) {
                    DentalApp.activity_id = selectedActivityId
                    DentalApp.activity_name = selectedActivity
                    DentalApp.saveToPreference(
                        context,
                        Constants.PREF_ACTIVITY_ID,
                        selectedActivityId
                    )
                    DentalApp.saveToPreference(
                        context,
                        Constants.PREF_ACTIVITY_NAME,
                        selectedActivity
                    )
                    saveBackDateToSharedPreference()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    if (!etOtherDetails.text.isNullOrEmpty()) saveToServerNewActivity()
                    else {
                        Toast.makeText(
                            context,
                            "Please fill other details.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

    private fun saveBackDateToSharedPreference() {
        DentalApp.saveToPreference(
            context,
            Constants.PERF_SELECTED_BACKDATE,
            backDateSelected
        )
    }

    private fun loadActivitySuggestions() {
        val panelService = DjangoInterface.create(context)
        val call = panelService.listActivities()
        call.enqueue(object : Callback<List<ActivitySuggestion>> {
            override fun onFailure(call: Call<List<ActivitySuggestion>>, t: Throwable) {
                FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ "loadActivitySuggestions " +t.message.toString())
                Log.d("loadActivityId()", "onFailure")
            }

            override fun onResponse(
                call: Call<List<ActivitySuggestion>>,
                response: Response<List<ActivitySuggestion>>
            ) {

                val allActivitySuggestions = response.body() as List<ActivitySuggestion>
                for (act in allActivitySuggestions) {
                    DentalApp.activitySuggestions.add(act.area)
                }
                arrayAdapter.notifyDataSetChanged()
            }

        })


    }

    private fun loadActivityId() {
        Log.d(TAG, "loadActivityId()")
        val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(context)
        val call = panelService.listActivityEvents("JWT $token")
        call.enqueue(object : Callback<List<ActivityModel>> {
            override fun onFailure(call: Call<List<ActivityModel>>, t: Throwable) {
                Log.d(TAG, "onFailure loadActivityId(): ${t}")
                if (BuildConfig.DEBUG) {
                    Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.could_not_load_activity),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(
                call: Call<List<ActivityModel>>,
                response: Response<List<ActivityModel>>
            ) {
                when (response.code()) {
                    200 -> {
                        Log.d(TAG, "response  ${response.body()}")
                        allAPIActivities = response.body() as List<ActivityModel>
                        for (eachActivity in allAPIActivities) {
                            when (eachActivity.name) {
                                "Community Outreach" -> communityoutreach_id = eachActivity.id
                                "Health Post" -> healthpost_id = eachActivity.id
                                "School Seminar" -> school_seminar_id = eachActivity.id
                                "Training" -> training_id = eachActivity.id
                            }

                            if (activityBox.query()
                                    .equal(Activity_.name, eachActivity.name)
                                    .build().count() == 0.toLong()) {
                                val newActivity = Activity()
                                newActivity.remote_id = eachActivity.id
                                newActivity.name = eachActivity.name
                                activityBox.put(newActivity)
                            }
                        }
                        progressBar.visibility = View.GONE
                    }
                    400 -> {
                        Log.d(TAG, "400 error found.")
                        progressBar.visibility = View.GONE
                        Toast.makeText(context, "Failed to fetch.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.d(TAG, "Unhandled exception")
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
            panelService.addActivity(
                "JWT $token",
                selectedActivityId,
                etOtherDetails.text.toString()
            )
        call.enqueue(object : Callback<ActivityModel> {
            override fun onFailure(call: Call<ActivityModel>, t: Throwable) {
                Log.d(TAG, "on Failure() : ${t.toString()}")
            }

            override fun onResponse(
                call: Call<ActivityModel>,
                response: Response<ActivityModel>
            ) {
                Log.d(TAG, "Response code is ${response.code()} and body is ${response.body()}")
                when (response.code()) {
                    200 -> {
                        val serverActivity = response.body() as ActivityModel
                        DentalApp.saveToPreference(
                            context,
                            Constants.PREF_ACTIVITY_NAME,
                            serverActivity.id
                        )
                        DentalApp.activity_id = selectedActivityId
                        DentalApp.activity_name = selectedActivity
                        DentalApp.saveToPreference(
                            context,
                            Constants.PREF_ACTIVITY_ID,
                            selectedActivityId
                        )
                        DentalApp.saveToPreference(
                            context,
                            Constants.PREF_ACTIVITY_NAME,
                            selectedActivity
                        )
                        saveBackDateToSharedPreference()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                    400 -> {
                        Log.d(TAG, "On 400 error")
                        Toast.makeText(context, "Fail to create the activity.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Log.d(TAG, "On other status code.")
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
    companion object {
        const val HEALTH_POST: String = "Health Post"
        const val TAG = "ActivitySelectorAct"
    }
}

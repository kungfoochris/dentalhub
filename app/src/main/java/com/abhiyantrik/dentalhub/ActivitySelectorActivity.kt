package com.abhiyantrik.dentalhub

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.abhiyantrik.dentalhub.entities.Activity
import com.abhiyantrik.dentalhub.entities.Activity_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.ActivityAreaModel
import com.abhiyantrik.dentalhub.utils.AdapterHelper
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.hornet.dateconverter.DateConverter
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_selector.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
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

    val schoolSeminarAreaList = mutableListOf<ActivityAreaModel>()
    val communityOutreachAreaList = mutableListOf<ActivityAreaModel>()
    val trainingAreaList = mutableListOf<ActivityAreaModel>()

    private lateinit var activityBox: Box<Activity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this
        initUI()
        Timber.d("Backdate of today is $backDateSelected")
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

        etBackdate.setText(DateHelper.getReadableNepaliDate(backDateSelected))

        // load the id and name of the activity
        loadActivityId()
        loadActivitySuggestions()

        Timber.d(DentalApp.activitySuggestions.toString())

        arrayAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, DentalApp.activitySuggestions.toTypedArray()
        )
        etOtherDetails.threshold = 1
        etOtherDetails.setAdapter(arrayAdapter)
        arrayAdapter.notifyDataSetChanged()

        rgActivities.setOnCheckedChangeListener { _, i ->
//            if (i == R.id.radioHealthPostActivity) {
//                etOtherDetails.setText("")
//                etOtherDetails.visibility = View.GONE
//            } else {
//                etOtherDetails.visibility = View.VISIBLE
//            }
            when (i) {
                R.id.radioHealthPostActivity -> {
                    selectedActivity = HEALTH_POST
                    selectedActivityId = healthpost_id
                    hideOtherThan(null)
                }
                R.id.radioSchoolSeminar -> {
                    selectedActivity = "School Seminar"
                    selectedActivityId = school_seminar_id
                    hideOtherThan(spinnerSchoolSeminar)
                }
                R.id.radioCommunityOutreach -> {
                    selectedActivity = "Community Outreach"
                    selectedActivityId = communityoutreach_id
                    hideOtherThan(spinnerCommunityOutreach)
                }
                R.id.radioTraining -> {
                    selectedActivity = "Training"
                    selectedActivityId = training_id
                    hideOtherThan(spinnerTraining)
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
                        Timber.d("Date $year-$month-$day")
                        backDateSelected = backDate
                    }
                }
            val beforeSevenDays = nepaliDateConverter.getNepaliDate(someDate)
            dpd.setMinDate(beforeSevenDays)
            dpd.setMaxDate(nepaliDateConverter.todayNepaliDate)
            dpd.show(supportFragmentManager, "Backdate")
        }

        btnGo.setOnClickListener {
            if (isFormValid()) {
                DentalApp.activity_id = selectedActivityId
                DentalApp.activity_name = selectedActivity
//                DentalApp.addStringToPreference(context, etOtherDetails.text.toString())

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

                when (selectedActivity) {
                    HEALTH_POST -> {
                        DentalApp.activity_area_name = ""
                        DentalApp.activity_area_id = 0
                        DentalApp.saveIntToPreference(
                            context,
                            Constants.PREF_ACTIVITY_AREA_ID,
                            0
                        )
                        saveBackDateToSharedPreference()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    "School Seminar" -> {

                        val selectedItemActivity = schoolSeminarAreaList[spinnerSchoolSeminar.selectedItemPosition]

                        DentalApp.activity_area_name = selectedItemActivity.area
                        DentalApp.activity_area_id = selectedItemActivity.id

                        DentalApp.saveIntToPreference(
                            context,
                            Constants.PREF_ACTIVITY_AREA_ID,
                            selectedItemActivity.id.toInt()
                        )
                        saveBackDateToSharedPreference()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    "Community Outreach"-> {
                        val selectedItemActivity = communityOutreachAreaList[spinnerCommunityOutreach.selectedItemPosition]

                        DentalApp.activity_area_name = selectedItemActivity.area
                        DentalApp.activity_area_id = selectedItemActivity.id

                        DentalApp.saveIntToPreference(
                            context,
                            Constants.PREF_ACTIVITY_AREA_ID,
                            selectedItemActivity.id.toInt()
                        )
                        saveBackDateToSharedPreference()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    "Training"-> {
                        val selectedItemActivity = trainingAreaList[spinnerTraining.selectedItemPosition]

                        DentalApp.activity_area_name = selectedItemActivity.area
                        DentalApp.activity_area_id = selectedItemActivity.id

                        DentalApp.saveIntToPreference(
                            context,
                            Constants.PREF_ACTIVITY_AREA_ID,
                            selectedItemActivity.id.toInt()
                        )
                        saveBackDateToSharedPreference()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

//                if (etOtherDetails.text.isNullOrEmpty()) {
//                    Toast.makeText(
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
        tvAddNewActivityArea.setOnClickListener {
            displayAddActivityAreaPopUp()
        }
    }

    private fun hideOtherThan(selectedItem: View?) {
        spinnerSchoolSeminar.visibility = View.GONE
        spinnerCommunityOutreach.visibility = View.GONE
        spinnerTraining.visibility = View.GONE

        selectedItem?.let {
            selectedItem.visibility = View.VISIBLE
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
        call.enqueue(object : Callback<List<ActivityAreaModel>> {
            override fun onFailure(call: Call<List<ActivityAreaModel>>, t: Throwable) {
                Timber.d(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ "loadActivitySuggestions " +t.message.toString())
                Timber.d("loadActivityId() %s", "onFailure")
                Timber.d(t.message)
            }

            override fun onResponse(
                call: Call<List<ActivityAreaModel>>,
                response: Response<List<ActivityAreaModel>>
            ) {

                val allActivitySuggestions = response.body() as List<ActivityAreaModel>
                for (act in allActivitySuggestions) {
                    DentalApp.activitySuggestions.add(act.area)
                }
                arrayAdapter.notifyDataSetChanged()

                loadAllActivitySpinner(allActivitySuggestions)

            }

        })


    }

    private fun loadAllActivitySpinner(allActivitySuggestions: List<ActivityAreaModel>) {

        val schoolSeminarList = mutableListOf<String>()
        val communityOutreachList = mutableListOf<String>()
        val trainingList = mutableListOf<String>()

        for (act in allActivitySuggestions) {
            DentalApp.activitySuggestions.add(act.area)
            if (act.activity == "School Seminar") {
                schoolSeminarAreaList.add(act)
            }
            if (act.activity == "Community Outreach") {
                communityOutreachAreaList.add(act)
            }
            if (act.activity == "Training") {
                trainingAreaList.add(act)
            }

        }

        for ( (_, schoolSeminar) in schoolSeminarAreaList.withIndex()) {
            schoolSeminarList.add(schoolSeminar.area)
        }
        for ( (_, communityOutreach) in communityOutreachAreaList.withIndex()) {
            communityOutreachList.add(communityOutreach.area)
        }
        for ( (_, training) in trainingAreaList.withIndex()) {
            trainingList.add(training.area)
        }

        spinnerSchoolSeminar.adapter = AdapterHelper.createAdapter(context, schoolSeminarList.toList())

        spinnerCommunityOutreach.adapter = AdapterHelper.createAdapter(context, communityOutreachList.toList())

        spinnerTraining.adapter = AdapterHelper.createAdapter(context, trainingList.toList())


    }

    private fun displayAddActivityAreaPopUp() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflate: LayoutInflater = layoutInflater
        val view: View = inflate.inflate(R.layout.popup_add_activity_area, null)

        // to get all id of the Button
        val rgAddActivityArea = view.findViewById<RadioGroup>(R.id.rgAddActivityArea)
//        val rbOtherProblem = view.findViewById<RadioButton>(R.id.rbOtherProblem)
        val etActivityAreaName = view.findViewById<EditText>(R.id.etActivityAreaName)
        val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnAddActivityArea = view.findViewById<Button>(R.id.btnAddActivityArea)

        builder.setView(view)
        val dialog: Dialog = builder.create()
        dialog.show()

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        btnAddActivityArea.setOnClickListener {
            val radioBtnID = rgAddActivityArea.checkedRadioButtonId
            if (radioBtnID != -1) {
                if (etActivityAreaName.text.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "Activity Area name is empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val selectedBtn: RadioButton = view.findViewById(radioBtnID)
//                    openAddEncounter(selectedBtn.text.toString(), etOtherProblem.text.toString())
                    var chooseActivityId = ""
                    when (selectedBtn.text.toString()) {
                        "School Seminar" -> chooseActivityId = school_seminar_id
                        "Community Outreach" -> chooseActivityId = communityoutreach_id
                        "Training" -> chooseActivityId = training_id
                    }
                    createNewActivityAreaToServerFor(chooseActivityId, etActivityAreaName.text.toString())
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Activity is not selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadActivityId() {
        Timber.d("loadActivityId()")
        val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(context)
        val call = panelService.listActivityEvents("JWT $token")
        call.enqueue(object : Callback<List<ActivityModel>> {
            override fun onFailure(call: Call<List<ActivityModel>>, t: Throwable) {
                Timber.d("onFailure loadActivityId(): ${t}")
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
                        Timber.d("response  ${response.body()}")
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
                        Timber.d("400 error found.")
                        progressBar.visibility = View.GONE
                        Toast.makeText(context, "Failed to fetch.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Timber.d("Unhandled exception")
                        progressBar.visibility = View.GONE
                        Toast.makeText(context, "Failed to connect.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    private fun createNewActivityAreaToServerFor(activityId: String, areaName: String) {
        Log.d(TAG, "createNewActivityAreaToServerFor()")
        val token = DentalApp.readFromPreference(this, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call =
            panelService.addActivity(
                "JWT $token",
                activityId,
                areaName
            )
        call.enqueue(object : Callback<ActivityModel> {
            override fun onFailure(call: Call<ActivityModel>, t: Throwable) {
                Timber.d("on Failure() : ${t.toString()}")
            }

            override fun onResponse(
                call: Call<ActivityModel>,
                response: Response<ActivityModel>
            ) {
                Timber.d("Response code is ${response.code()} and body is ${response.body()}")
                when (response.code()) {
                    200 -> {
                        val serverActivity = response.body() as ActivityModel
                        Toast.makeText(this@ActivitySelectorActivity, "Area created successfully.", Toast.LENGTH_SHORT)
                        startActivity(Intent(context, LocationSelectorActivity::class.java))
                        finish()
//                        DentalApp.saveToPreference(
//                            context,
//                            Constants.PREF_ACTIVITY_NAME,
//                            serverActivity.id
//                        )
//                        DentalApp.activity_id = selectedActivityId
//                        DentalApp.activity_name = selectedActivity
//                        DentalApp.saveToPreference(
//                            context,
//                            Constants.PREF_ACTIVITY_ID,
//                            selectedActivityId
//                        )
//                        DentalApp.saveToPreference(
//                            context,
//                            Constants.PREF_ACTIVITY_NAME,
//                            selectedActivity
//                        )
                    }
                    400 -> {
                        Timber.d("On 400 error")
                        Toast.makeText(context, "Fail to create the activity.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Timber.d("On other status code.")
                        Toast.makeText(context, "Unknown problem faced.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun isFormValid(): Boolean {
        var status = false
        val radioBtnID = rgActivities.checkedRadioButtonId
        if (radioBtnID != -1) status = true
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

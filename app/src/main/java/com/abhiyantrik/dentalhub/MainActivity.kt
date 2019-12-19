package com.abhiyantrik.dentalhub

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abhiyantrik.dentalhub.adapters.PatientAdapter
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.services.LocationTrackerService
import com.abhiyantrik.dentalhub.services.SyncDownloadService
import com.abhiyantrik.dentalhub.services.SyncService
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import com.abhiyantrik.dentalhub.workers.DownloadPatientWorker
import com.abhiyantrik.dentalhub.workers.UploadEncounterWorker
import com.abhiyantrik.dentalhub.workers.UploadPatientWorker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.query.Query
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var loading: ProgressBar
    private lateinit var btnAddPatient: Button
    private lateinit var fabBtnAddPatient: FloatingActionButton
    private lateinit var fabBtnSync: FloatingActionButton

    private lateinit var tvLocation: TextView
    private lateinit var tvName: TextView
    private lateinit var tvActivity: TextView

    private lateinit var context: Context
    private lateinit var patientAdapter: PatientAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var allPatients: List<Patient>

    private var encountersBox: Box<Encounter> =  ObjectBox.boxStore.boxFor(Encounter::class.java)

    private lateinit var patientsBox: Box<Patient>
    private lateinit var patientsQuery: Query<Patient>
    private lateinit var recallBox: Box<Recall>
//    private lateinit var recallQuery: Query<Recall>
    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>


    private lateinit var allPatientRecall: MutableList<Patient>

    private val TAG = "MainActivity"


    @AddTrace(name = "onCreateMainActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)

        context = this

        startService(Intent(this, LocationTrackerService::class.java))

        setupUI()

        Log.d("Location", DentalApp.ward_name)
        Log.d(
            "Proxy: ",
            DentalApp.readFromPreference(
                context,
                Constants.PREF_PROFILE_FULL_NAME,
                "Some thing is fishy."
            )
        )
        Log.d("Activity", DentalApp.activity_name)
        tvName.text = DentalApp.readFromPreference(context, Constants.PREF_PROFILE_FULL_NAME, "")
        tvLocation.text = DentalApp.ward_name
        tvActivity.text = DentalApp.activity_name
    }

    private fun listRecallPatients() {
        println("called once.")
        var c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = df.format(c)
//
//        Log.d("LocalDate", currentDate.toString())
//        // get all Address objects
//        val builder = patientsBox.query()
//        // ...which are linked from a Recall date "today"
//        builder.link(Patient_.recall).equal(Recall_.date, currentDate.toString())
//        var sesameStreetsWithElmo = builder.build().find()
//        allPatientRecall = sesameStreetsWithElmo
//
//        for (eachDay in 1..10) {
////            val days = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                LocalDate.now().plusDays(eachDay.toLong())
////            } else {
////                // do something here
////            }
//            val days= 1;
//            val builder = patientsBox.query()
//            builder.link(Patient_.recall).equal(Recall_.date, days.toString())
//            sesameStreetsWithElmo = builder.build().find()
//            allPatientRecall.addAll(sesameStreetsWithElmo)
//        }
//
//        for (recall in allPatientRecall) {
//            println("Recall patient name is ${recall.fullName()}")
//        }
        allPatientRecall = mutableListOf()
        val today = DateHelper.getCurrentNepaliDate()
        val todayPatient = patientsBox.query().equal(Patient_.recall_date, today)
            .equal(Patient_.recall_geography, DentalApp.geography_id.toLong()).order(Patient_.recall_date)
            .build().find()


        val rowToday = Patient()
        rowToday.first_name = "Recall Today"
        rowToday.content = "header"
        allPatientRecall.add(rowToday)
        allPatientRecall.addAll(todayPatient)

        val rowThisWeek = Patient()
        rowThisWeek.first_name = "Recall Next Week"
        rowThisWeek.content = "Header"
        allPatientRecall.add(rowThisWeek)

        var nextDay = DateHelper.getNextDay(today)
        Log.d("NEXT WEEK", "NEXT WEEK")
        for (i in 1..8) {
            val thisWeekPatients =
                patientsBox.query().equal(Patient_.recall_date, nextDay)
                    .equal(Patient_.recall_geography, DentalApp.geography_id.toLong())
                    .order(Patient_.recall_date).build().find()
            allPatientRecall.addAll(thisWeekPatients)
            nextDay = DateHelper.getNextDay(nextDay)
        }

        val rowRecallNextMonth = Patient()
        rowRecallNextMonth.first_name = "Recall Next Month"
        rowRecallNextMonth.content = "header"
        allPatientRecall.add(rowRecallNextMonth)
        Log.d("NEXT MONTH", "NEXT MONTH")
        for (i in 1..24) {
            val thisMonthPatients =
                patientsBox.query().equal(Patient_.recall_date, nextDay)
                    .equal(Patient_.recall_geography, DentalApp.geography_id.toLong())
                    .order(Patient_.recall_date).build().find()
            allPatientRecall.addAll(thisMonthPatients)
            nextDay = DateHelper.getNextDay(nextDay)
        }
        setupAdapter(allPatientRecall)
    }


    @AddTrace(name = "setupUIMainActivity", enabled = true /* optional */)
    private fun setupUI() {
        loading = findViewById(R.id.loading)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddPatient = findViewById(R.id.btnAddNewPatient)
        fabBtnAddPatient = findViewById(R.id.fabAddPatient)
        fabBtnSync = findViewById(R.id.fabSync)
        fabBtnSync.setBackgroundResource(R.color.blue_100)


        tvLocation = findViewById(R.id.tvLocation)
        tvActivity = findViewById(R.id.tvActivity)
        tvName = findViewById(R.id.tvFullName)

        title = getString(R.string.dashboard)

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        historyBox = ObjectBox.boxStore.boxFor(History::class.java)
        screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
        treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)
        patientsQuery = patientsBox.query().build()

        recallBox = ObjectBox.boxStore.boxFor(Recall::class.java)

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(0)
        recyclerView.addItemDecoration(divider)

        btnAddPatient.setOnClickListener {
            addNewPatient()
        }
        fabBtnAddPatient.setOnClickListener {
            addNewPatient()
        }
        fabBtnSync.setOnClickListener {
            Log.d(TAG, "startSync")

            val downloadPatientWorkRequest = OneTimeWorkRequestBuilder<DownloadPatientWorker>()
                .setInitialDelay(100, TimeUnit.MILLISECONDS)
                .setConstraints(DentalApp.downloadConstraints)
                .build()
            WorkManager.getInstance(applicationContext).enqueue(downloadPatientWorkRequest)

            // upload all patients
            val dbAllPatient =
                patientsBox.query().build().find()

            for (patient in dbAllPatient) {
                val data = Data.Builder().putLong("PATIENT_ID", patient.id)
                val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<UploadPatientWorker>()
                    .setInputData(data.build())
                    .setConstraints(DentalApp.uploadConstraints)
                    .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                WorkManager.getInstance(applicationContext).enqueue(uploadPatientWorkRequest)

                val allEncounters =
                    encountersBox.query().equal(Encounter_.patientId, patient.id).and().equal(Encounter_.uploaded, false).build().find()
                for (eachEncounter in allEncounters) {
                    if (!eachEncounter.uploaded) {
                        val data = Data.Builder().putLong("ENCOUNTER_ID", eachEncounter.id)
                            .putLong("PATIENT_ID", patient.id)
                        val uploadEncounterWorkerRequest =
                            OneTimeWorkRequestBuilder<UploadEncounterWorker>()
                                .setInputData(data.build())
                                .setConstraints(DentalApp.uploadConstraints)
                                .setInitialDelay(
                                    100,
                                    TimeUnit.MILLISECONDS
                                ).build()
                        WorkManager.getInstance(applicationContext)
                            .enqueue(uploadEncounterWorkerRequest)
                    }
                }

            }
            //Toast.makeText(context,"Work in progress", Toast.LENGTH_LONG).show()
        }

        val handler = Handler()
        val run = object : Runnable {
            override fun run() {
                checkAllUpdated()
                handler.postDelayed(this, 2000)
            }
        }
        handler.postDelayed(run, 5000)
    }


    private fun checkAllUpdated() {
        val patient = patientsBox.query().equal(Patient_.uploaded, false).build().find()
        val encounter = encounterBox.query().equal(Encounter_.uploaded, false).build().find()

        val history = historyBox.query().equal(History_.uploaded, false).build().find()
        val screening =
            screeningBox.query().equal(Screening_.uploaded, false).build().find()
        val treatment =
            treatmentBox.query().equal(Treatment_.uploaded, false).build().find()
        val referral =
            referralBox.query().equal(Referral_.uploaded, false).build().find()

        Log.d(TAG, "Patient : " + patient.toString())
        Log.d(TAG, "encounter : " + encounter.toString())
        Log.d(TAG, "history : " + history.toString())
        Log.d(TAG, "screening : " + screening.toString())
        Log.d(TAG, "treatment : " + treatment.toString())
        Log.d(TAG, "referral : " + referral.toString())

        if (patient.isNullOrEmpty() && encounter.isNullOrEmpty() && history.isNullOrEmpty() && screening.isNullOrEmpty() &&
                treatment.isNullOrEmpty() && referral.isNullOrEmpty()) {
            Log.d(TAG, "all patient uploaded.")
            fabBtnSync.background.setTint(resources.getColor(R.color.green_A200))
//            fabBtnSync.background.mutate().setTint(resources.getColor(R.color.green_A200))

//        fabBtnSync.backgroundTintList = resources.getColorStateList(R.color.blue_100)
        } else {
            Log.d(TAG, "Left to upload patient details.")
            fabBtnSync.background.setTint(resources.getColor(R.color.red_A200))
//            fabBtnSync.drawable.mutate().setTint(resources.getColor(R.color.red_A200))
        }

    }


    @AddTrace(name = "listPatientsMainActivity", enabled = true /* optional */)
    private fun listPatients() {
        if (DentalApp.activity_name == "Health Post") {
            listRecallPatients()
        } else {
            listPatientsFromLocalDB()
        }

    }

    @AddTrace(name = "listPatientsFromLocalDBMainActivity", enabled = true /* optional */)
    private fun listPatientsFromLocalDB() {
        Log.d(TAG, "listPatientsFromLocalDB()")
        try {
            allPatients =
                patientsBox.query().equal(Patient_.geography_id, DentalApp.geography_id.toLong())
                    .orderDesc(Patient_.created_at).orderDesc(Patient_.id).build().find()
            setupAdapter(allPatients)
        } catch (e: DbException) {
            Log.d("DBException", e.printStackTrace().toString())
        }

    }

    @AddTrace(name = "setupAdapterMainActivity", enabled = true /* optional */)
    private fun setupAdapter(patientList: List<Patient>) {
        var displayDelayAndRemoveBtn = false
        if (DentalApp.activity_name == "Health Post") {
            displayDelayAndRemoveBtn = true
        }
        patientAdapter =
            PatientAdapter(
                context,
                patientList,
                displayDelayAndRemoveBtn,
                object : PatientAdapter.PatientClickListener {
                    override fun onRemovePatientClick(patient: Patient) {

                        // Initialize a new instance of
                        val builder = AlertDialog.Builder(this@MainActivity)

                        // Set the alert dialog title
                        builder.setTitle("Remove patient recall.")

                        // Display a message on alert dialog
                        builder.setMessage("Are you want to remove recall?")

                        // Set a positive button and its click listener on alert dialog
                        builder.setPositiveButton("YES"){dialog, which ->
                            // Do something when user press the positive button
                            Toast.makeText(applicationContext,"Patient recall is removed.",Toast.LENGTH_SHORT).show()
                            val tempPatient =
                                patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()!!
                            tempPatient.recall_date = ""
                            patientsBox.put(tempPatient)
                            listPatients()
                        }


                        // Display a negative button on alert dialog
                        builder.setNegativeButton("No"){dialog,which ->
//                            Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
                        }

                        // Finally, make the alert dialog using builder
                        val dialog: AlertDialog = builder.create()

                        // Display the alert dialog on app interface
                        dialog.show()


                    }

                    override fun onDelayPatientClick(patient: Patient) {
                        displayDelayDialog(patient)
                    }

                    override fun onCallPatientClick(patient: Patient) {
                        if(DentalApp.canMakeCall(context)){
                            val call = Intent(Intent.ACTION_DIAL)
                            call.data = Uri.parse("tel:" + patient.phone)
                            startActivity(call)
                        }else{
                            Toast.makeText(context, getString(R.string.telephony_serivce_unavailable), Toast.LENGTH_LONG).show()
                        }

                    }

                    override fun onViewPatientDetailClick(position: Int,patient: Patient) {
                        DentalApp.saveIntToPreference(context, Constants.PREF_LAST_SELECTED_PATIENT_POSITION, position)
                        val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
                        viewPatientIntent.putExtra("PATIENT_ID", patient.id)
                        startActivity(viewPatientIntent)
                    }

                })
        recyclerView.adapter = patientAdapter
        patientAdapter.notifyDataSetChanged()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        listPatients()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(context, SearchPatientActivity::class.java))
            }

            R.id.logout -> {
                DentalApp.clearAuthDetails(context)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Goes to intent AddPatientActivity to Add New Patient
    private fun addNewPatient() {
        val addPatientActivityIntent = Intent(this, AddPatientActivity::class.java)
        addPatientActivityIntent.putExtra("ACTION", "new")
        startActivity(addPatientActivityIntent)
    }

    private fun displayDelayDialog(patient: Patient) {
        // delay recall of patient
        val grpName = arrayOf(
            "1 week",
            "2 weeks",
            "3 weeks",
            "1 month",
            "2 months",
            "3 months"
        )
        val delayChooser = androidx.appcompat.app.AlertDialog.Builder(this)
        delayChooser.setTitle(getString(R.string.delay))
        delayChooser.setSingleChoiceItems(
            grpName,
            -1,
            DialogInterface.OnClickListener { dialog, item ->
                loading.visibility = View.VISIBLE
                Log.d("DELAYED: ", patient.fullName() + " by " + grpName[item])
                val tempPatient =
                    patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()!!

                var recallDate = ""
                try {
                    recallDate = tempPatient.recall_date!!
                }catch(e: NullPointerException){
                    recallDate = ""
                }
                when (item) {
                    0 -> {
                        //calendar.add(Calendar.DAY_OF_YEAR,7)
                        for (i in 1..7) {
                            recallDate = DateHelper.getNextDay(recallDate)
                        }
                    }
                    1 -> {
                        //calendar.add(Calendar.DAY_OF_YEAR,14)
                        for (i in 1..14) {
                            recallDate = DateHelper.getNextDay(recallDate)
                        }
                    }
                    2 -> {
                        //calendar.add(Calendar.DAY_OF_YEAR,21)
                        for (i in 1..21) {
                            recallDate = DateHelper.getNextDay(recallDate)
                        }
                    }
                    3 -> {
                        //calendar.add(Calendar.DAY_OF_YEAR,28)
                        for (i in 1..28) {
                            recallDate = DateHelper.getNextDay(recallDate)
                        }
                    }
                    4 -> {
                        //calendar.add(Calendar.DAY_OF_YEAR,60)
                        for (i in 1..60) {
                            recallDate = DateHelper.getNextDay(recallDate)
                        }
                    }
                    5 -> {
                        //calendar.add(Calendar.DAY_OF_YEAR,90)
                        for (i in 1..90) {
                            recallDate = DateHelper.getNextDay(recallDate)
                        }
                    }
                }

                tempPatient.recall_date = recallDate
                patientsBox.put(tempPatient)
                loading.visibility = View.GONE
                listPatients()
                dialog.dismiss()// dismiss the alert box after chose option
            })
        val alert = delayChooser.create()
        alert.show()
    }

}

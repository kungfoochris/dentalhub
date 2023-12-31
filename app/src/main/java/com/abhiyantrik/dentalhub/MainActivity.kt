package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abhiyantrik.dentalhub.adapters.PatientAdapter
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.services.LocationTrackerService
import com.abhiyantrik.dentalhub.ui.asyncronus.AsyncActivity
import com.abhiyantrik.dentalhub.ui.flagencounterview.FlagEncounterViewActivity
import com.abhiyantrik.dentalhub.ui.synchronization.SynchronizationActivity
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import com.abhiyantrik.dentalhub.workers.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.query.Query
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>

    private lateinit var allPatientRecall: MutableList<Patient>

    private var fabButtonPressTime = false

    @AddTrace(name = "onCreateMainActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate()")
        setContentView(R.layout.activity_main)

        context = this

        startService(Intent(this, LocationTrackerService::class.java))

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Timber.d("$TAG: Message From OnCreate")

        setupUI()

        Timber.d("Location %s", DentalApp.ward_name)
        Timber.d(
            "Proxy: "+
            DentalApp.readFromPreference(
                context,
                Constants.PREF_PROFILE_FULL_NAME,
                "Some thing is fishy."
            )
        )
        Timber.d("Activity %s", DentalApp.activity_name)
        tvName.text = DentalApp.readFromPreference(context, Constants.PREF_PROFILE_FULL_NAME, "")
        tvLocation.text = DentalApp.ward_name
        tvActivity.text = DentalApp.activity_name
    }

    @AddTrace(name = "listRecallPatients", enabled = true /* optional */)
    private fun listRecallPatients() {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        df.format(c)
        allPatientRecall = mutableListOf()
        val today = DateHelper.getTodaysNepaliDate()

        val rowToday = Patient()
        rowToday.first_name = "Recall Today"
        rowToday.content = "header"
        allPatientRecall.add(rowToday)

        /* Previous days started */
        var past30DaysDate = DateHelper.getNepaliDaysLaterDate(context, -30)
        Timber.d("PREVIOUS MONTHS")
        for (i in 1..30) {
            val past90DaysPatient =
                patientsBox.query().equal(Patient_.recall_date, past30DaysDate)
                    .equal(Patient_.recall_geography, DentalApp.geography_id.toLong())
                    .order(Patient_.recall_date).build().find()
            allPatientRecall.addAll(past90DaysPatient)
            past30DaysDate = DateHelper.getNextDay(past30DaysDate)
        }
        /* Previous days ended */

        val todayPatient = patientsBox.query().equal(Patient_.recall_date, today)
            .equal(Patient_.recall_geography, DentalApp.geography_id.toLong()).order(Patient_.recall_date)
            .build().find()

        allPatientRecall.addAll(todayPatient)

        val rowThisWeek = Patient()
        rowThisWeek.first_name = "Recall Next Week"
        rowThisWeek.content = "Header"
        allPatientRecall.add(rowThisWeek)

        var nextDay = DateHelper.getNextDay(today)
        Timber.d("NEXT WEEK")
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
        Timber.d("NEXT MONTH")
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

        if (DentalApp.activity_id == "" || DentalApp.geography_id < 1) {
            Timber.d("Activity is not been selected.")
            Timber.d(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ " Activity has not been selected")
            logout()
        }

        if (DentalApp.activity_id != "" && DentalApp.activity_id != "1" && DentalApp.activity_area_id < 1) {
            logout()
        }

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
            Timber.d("startSync")
            if (!fabButtonPressTime) {

                Timber.d("5 minuted countdown starts now.")

                //after 5 minute this method will be pressed. 300000
                Handler(Looper.getMainLooper()).postDelayed({
                    fabButtonPressTime = false
                    Timber.d("5 min completed.")
                }, 300000)

                fabButtonPressTime = true


                val downloadPatientWorkRequest = OneTimeWorkRequestBuilder<DownloadPatientWorker>()
                    .setInitialDelay(100, TimeUnit.MILLISECONDS)
                    .setConstraints(DentalApp.downloadConstraints)
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(downloadPatientWorkRequest)

                // upload all patients
                val dbAllPatient =
                    patientsBox.query().build().find()

                for (patient in dbAllPatient) {

                    if (!patient.uploaded) {
                        val data = Data.Builder().putLong("PATIENT_ID", patient.id)
                        val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<UploadPatientWorker>()
                            .setInputData(data.build())
                            .setConstraints(DentalApp.uploadConstraints)
                            .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                        WorkManager.getInstance(applicationContext).enqueue(uploadPatientWorkRequest)
                    }

                    val allEncounters =
                        encountersBox.query().equal(Encounter_.patientId, patient.id).build().find()
                    for (eachEncounter in allEncounters) {

                        val data = Data.Builder().putLong("ENCOUNTER_ID", eachEncounter.id)
                            .putLong("PATIENT_ID", patient.id)

                        if (!eachEncounter.uploaded) {
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
                        } else {
                            val unuploadedHistory = historyBox.query().equal(History_.uploaded, false).and().equal(History_.encounterId, eachEncounter.id).build().find()
                            val unuploadedScreening = screeningBox.query().equal(Screening_.uploaded, false).and().equal(Screening_.encounterId, eachEncounter.id).build().find()
                            val unuploadedTreatment = treatmentBox.query().equal(Treatment_.uploaded, false).and().equal(Treatment_.encounterId, eachEncounter.id).build().find()
                            val unuploadedReferral = referralBox.query().equal(Referral_.uploaded, false).and().equal(Referral_.encounterId, eachEncounter.id).build().find()

                            Timber.d("Fab sync History " +  unuploadedHistory.toString())
                            Timber.d("Fab sync eachScreening "+ unuploadedScreening.toString())
                            Timber.d("Fab sync eachTreatment " + unuploadedTreatment.toString())
                            Timber.d("Fab sync eachReferral " + unuploadedReferral.toString())

                            if (unuploadedHistory.isNotEmpty()) {
                                Timber.d("Fab sync History: "+ unuploadedHistory[0].id.toString())

                                val uploadHistoryWorkerRequest =
                                    OneTimeWorkRequestBuilder<UploadHistoryWorker>()
                                        .setInputData(data.build())
                                        .setConstraints(DentalApp.uploadConstraints)
                                        .setInitialDelay(100, TimeUnit.MILLISECONDS).build()

                                WorkManager.getInstance(applicationContext)
                                    .enqueue(uploadHistoryWorkerRequest)
                            }
                            if (unuploadedScreening.isNotEmpty()) {
                                Timber.d("Fab sync History: "+ unuploadedScreening[0].id.toString())

                                val uploadScreeningWorkerRequest =
                                    OneTimeWorkRequestBuilder<UploadScreeningWorker>()
                                        .setInputData(data.build())
                                        .setConstraints(DentalApp.uploadConstraints)
                                        .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                                WorkManager.getInstance(applicationContext)
                                    .enqueue(uploadScreeningWorkerRequest)
                            }

                            if (unuploadedTreatment.isNotEmpty()) {
                                Timber.d("Fab sync History"+ unuploadedTreatment[0].id.toString())

                                val uploadTreatmentWorkerRequest =
                                    OneTimeWorkRequestBuilder<UploadTreatmentWorker>()
                                        .setInputData(data.build())
                                        .setConstraints(DentalApp.uploadConstraints)
                                        .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                                WorkManager.getInstance(applicationContext)
                                    .enqueue(uploadTreatmentWorkerRequest)
                            }

                            if (unuploadedReferral.isNotEmpty()) {
                                Timber.d("Fab sync History"+ unuploadedReferral[0].id.toString())

                                val uploadReferralWorkerRequest =
                                    OneTimeWorkRequestBuilder<UploadReferralWorker>()
                                        .setInputData(data.build())
                                        .setConstraints(DentalApp.uploadConstraints)
                                        .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                                WorkManager.getInstance(applicationContext)
                                    .enqueue(uploadReferralWorkerRequest)
                            }
                        }
                    }

                }
            }
        }
        try {
            val handler = Handler(Looper.getMainLooper())
            val run = object : Runnable {
                override fun run() {
                    checkAllUpdated()
                    handler.postDelayed(this, 10000)
                }
            }
            handler.postDelayed(run, 10000)
        } catch (ex: Exception) {
            Timber.e("10 sec delay run error: ${ex.message}")
        }
    }

    private fun logout() {
        DentalApp.clearAuthDetails(context)
        Toast.makeText(context, "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(context, LoginActivity::class.java))
        finish()
    }

    private fun checkAllUpdated() {
        try {
            val patient = patientsBox.query().equal(Patient_.uploaded, false).build().find()
            val encounter = encounterBox.query().equal(Encounter_.uploaded, false).build().find()

            val history = historyBox.query().equal(History_.uploaded, false).build().find()
            val screening =
                screeningBox.query().equal(Screening_.uploaded, false).build().find()
            val treatment =
                treatmentBox.query().equal(Treatment_.uploaded, false).build().find()
            val referral =
                referralBox.query().equal(Referral_.uploaded, false).build().find()

            if (patient.isNullOrEmpty() && encounter.isNullOrEmpty() && history.isNullOrEmpty() && screening.isNullOrEmpty() &&
                treatment.isNullOrEmpty() && referral.isNullOrEmpty()) {
                Timber.d("all patient uploaded.")
                fabBtnSync.background.setTint(ContextCompat.getColor(context, R.color.colorART))
            } else {
                Timber.d("Left to upload patient details.")
                fabBtnSync.background.setTint(ContextCompat.getColor(context, R.color.red_A200))
            }
        } catch (ex: Exception) {
            Timber.e("Error occurred in checkAllUpdated: ${ex.message}")
            Toast.makeText(
                context,
                "Failed to check all updates.",
                Toast.LENGTH_SHORT
            ).show()
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
        Timber.d("listPatientsFromLocalDB()")
        try {
            allPatients =
                patientsBox.query().equal(Patient_.geography_id, DentalApp.geography_id.toLong())
                    .orderDesc(Patient_.created_at).orderDesc(Patient_.id).build().find()
            setupAdapter(allPatients)
        } catch (e: DbException) {
            Timber.d(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ e.printStackTrace().toString())
            FirebaseCrashlytics.getInstance().recordException(e)
            Timber.d("DBException:"+ e.printStackTrace().toString())
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
                        builder.setPositiveButton("YES"){ _, _ ->
                            // Do something when user press the positive button
                            Toast.makeText(applicationContext,"Patient recall is removed.",Toast.LENGTH_SHORT).show()
                            patient.recall_date = ""
                            patient.called = Call.NOT_CALLED.status
                            patientsBox.put(patient)
                            listPatients()
                        }


                        // Display a negative button on alert dialog
                        builder.setNegativeButton("No"){ _, _ ->
                                // Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
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
                        showCallPopup(patient)
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

    private fun showCallPopup(patient: Patient) {

        // setup the alert builder
        val builder = AlertDialog.Builder(context)

        // add a list
        val animals = arrayOf("Call", "Didn't answer", "Not called")
        builder.setItems(animals) { dialog, which ->
            when (which) {
                0 -> {
                    if (patient.called == Call.NOT_CALLED.status) {
                        if(DentalApp.canMakeCall(context)){
                            val call = Intent(Intent.ACTION_DIAL)
                            call.data = Uri.parse("tel:" + patient.phone)
                            startActivity(call)
                            patient.called = Call.CALLED.status
                            patientsBox.put(patient)
                            patientAdapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(context, getString(R.string.telephony_service_unavailable), Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, "Already called the patient.", Toast.LENGTH_LONG).show()
                    }
                }
                1 -> {
                    patient.called = Call.NO_ANSWER.status
                    patientsBox.put(patient)
                    patientAdapter.notifyDataSetChanged()
                }
                2 -> {
                    patient.called = Call.NOT_CALLED.status
                    patientsBox.put(patient)
                    patientAdapter.notifyDataSetChanged()
                }
            }
        }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
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

            R.id.menuFlag -> {
                startActivity(Intent(context, FlagEncounterViewActivity::class.java))
            }

            R.id.menuSync -> {
                startActivity(Intent(context, SynchronizationActivity::class.java))
            }

            R.id.menuAsync -> {
                startActivity(Intent(context, AsyncActivity::class.java))
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

    @AddTrace(name = "displayDelayDialogFromMainActivity", enabled = true /* optional */)
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
                Timber.d("DELAYED: "+ patient.fullName() + " by " + grpName[item])
                val tempPatient =
                    patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()!!

                var recallDate: String
                recallDate = try {
                    tempPatient.recall_date!!
                }catch(e: NullPointerException){
                    FirebaseCrashlytics.getInstance().recordException(e)
                    ""
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
    companion object{
        const val TAG = "MainActivity"
    }

}

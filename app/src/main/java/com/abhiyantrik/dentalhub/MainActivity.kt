package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.adapters.PatientAdapter
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.entities.Recall
import com.abhiyantrik.dentalhub.services.LocationTrackerService
import com.abhiyantrik.dentalhub.services.SyncDownloadService
import com.abhiyantrik.dentalhub.services.SyncService
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.query.Query
import java.text.SimpleDateFormat
import java.util.*


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

    private lateinit var patientsBox: Box<Patient>
    private lateinit var patientsQuery: Query<Patient>
    private lateinit var recallBox: Box<Recall>
//    private lateinit var recallQuery: Query<Recall>

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

        Log.d("Location", DentalApp.geography_name)
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
        tvLocation.text = DentalApp.geography_name
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
        val todayPatient = patientsBox.query().equal(Patient_.recall_date, today).equal(Patient_.recall_geography, DentalApp.geography_name).order(Patient_.recall_date).build().find()


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
                patientsBox.query().equal(Patient_.recall_date, nextDay).equal(Patient_.recall_geography, DentalApp.geography_name).order(Patient_.recall_date).build().find()
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
                patientsBox.query().equal(Patient_.recall_date, nextDay).equal(Patient_.recall_geography, DentalApp.geography_name).order(Patient_.recall_date).build().find()
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

        tvLocation = findViewById(R.id.tvLocation)
        tvActivity = findViewById(R.id.tvActivity)
        tvName = findViewById(R.id.tvFullName)

        title = getString(R.string.dashboard)

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
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
            if(!DentalApp.uploadSyncRunning){
                startService(Intent(this, SyncService::class.java))
            }
            if(!DentalApp.downloadSyncRunning){
                startService(Intent(this, SyncDownloadService::class.java))
            }
            //Toast.makeText(context,"Work in progress", Toast.LENGTH_LONG).show()
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
                patientsBox.query().equal(Patient_.geography_id, DentalApp.geography_id)
                    .orderDesc(Patient_.created_at).build().find()
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
                        val tempPatient =
                            patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()!!
                        tempPatient.recall_date = ""
                        patientsBox.put(tempPatient)
                        listPatients()
                    }

                    override fun onDelayPatientClick(patient: Patient) {
                        displayDelayDialog(patient)
                    }

                    override fun onCallPatientClick(patient: Patient) {
                        val call = Intent(Intent.ACTION_DIAL)
                        call.data = Uri.parse("tel:" + patient.phone)
                        startActivity(call)
                    }

                    override fun onViewPatientDetailClick(patient: Patient) {
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

                var recallDate = tempPatient.recall_date
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

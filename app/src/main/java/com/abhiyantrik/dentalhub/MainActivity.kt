package com.abhiyantrik.dentalhub

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.adapters.PatientAdapter
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.entities.Recall
import com.abhiyantrik.dentalhub.entities.Recall_
import com.abhiyantrik.dentalhub.services.LocationTrackerService
import com.abhiyantrik.dentalhub.services.SyncService
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.query.Query
import java.time.LocalDate


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

    }

    private fun listRecallPatients() {
        println("called once.")
        val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            // do something here
        }
        // get all Address objects
        val builder = patientsBox.query()
//       ...which are linked from a Recall date "today"
        builder.link(Patient_.recall).equal(Recall_.date, currentDate.toString())
        var sesameStreetsWithElmo = builder.build().find()
        allPatientRecall = sesameStreetsWithElmo

        for (eachDay in 1..10) {
            val days = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().plusDays(eachDay.toLong())
            } else {
                // do something here
            }
            val builder = patientsBox.query()
            builder.link(Patient_.recall).equal(Recall_.date, days.toString())
            sesameStreetsWithElmo = builder.build().find()
            allPatientRecall.addAll(sesameStreetsWithElmo)
        }

        for (recall in allPatientRecall) {
            println("Recall patient name is ${recall.fullName()}")
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
        Log.d("PROFILE NAME", DentalApp.fullName)
        Log.d("Location", DentalApp.geography_name)
        Log.d("Activity", DentalApp.activity_name)
        tvName.text = DentalApp.fullName
        tvLocation.text = DentalApp.geography_name
        tvActivity.text = DentalApp.activity_name

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        patientsQuery = patientsBox.query().build()

        recallBox = ObjectBox.boxStore.boxFor(Recall::class.java)

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(20)
        recyclerView.addItemDecoration(divider)

        btnAddPatient.setOnClickListener {
            addNewPatient()
        }
        fabBtnAddPatient.setOnClickListener {
            addNewPatient()
        }
        fabBtnSync.setOnClickListener {
            Log.d(TAG, "startSync")
            startService(Intent(this, SyncService::class.java))
        }

    }


    @AddTrace(name = "listPatientsMainActivity", enabled = true /* optional */)
    private fun listPatients() {
        listPatientsFromLocalDB()
    }

    @AddTrace(name = "listPatientsFromLocalDBMainActivity", enabled = true /* optional */)
    private fun listPatientsFromLocalDB() {
        Log.d(TAG, "listPatientsFromLocalDB()")
        try{
            allPatients =
                patientsBox.query().equal(Patient_.geography_id, DentalApp.geography_id).build().find()
            setupAdapter(allPatients)
        }catch(e: DbException){
            Log.d("DBException", e.printStackTrace().toString())
        }

    }

    @AddTrace(name = "setupAdapterMainActivity", enabled = true /* optional */)
    private fun setupAdapter(patientList: List<Patient>) {
        patientAdapter =
            PatientAdapter(context, patientList, object : PatientAdapter.PatientClickListener {
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
                    viewPatientIntent.putExtra("patient", patient)
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
        if (DentalApp.activity_name == "Health Post") {
            listRecallPatients()
        } else {
            listPatients()
        }
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
                Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show()
                dialog.dismiss()// dismiss the alert box after chose option
            })
        val alert = delayChooser.create()
        alert.show()
    }

//    @AddTrace(name = "displaySearchDialogMainActivity", enabled = true /* optional */)
//    private fun displaySearchDialog() {
//        Log.d("TAG", "displaySearchDialog()")
//        val searchDialogView = LayoutInflater.from(this).inflate(R.layout.search_dialog, null)
//        val mBuilder =
//            AlertDialog.Builder(this).setView(searchDialogView).setTitle(getString(R.string.search))
//
//        mBuilder.show()
//    }


}

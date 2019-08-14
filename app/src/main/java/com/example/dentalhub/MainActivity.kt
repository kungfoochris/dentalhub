package com.example.dentalhub

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.adapters.PatientAdapter
import com.example.dentalhub.entities.Patient
import com.example.dentalhub.entities.Patient_
import com.example.dentalhub.services.LocationTrackerService
import com.example.dentalhub.services.SyncService
import com.example.dentalhub.utils.RecyclerViewItemSeparator
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.query.Query


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var loading: ProgressBar

    private lateinit var context: Context
    private lateinit var patientAdapter: PatientAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var allPatients: List<Patient>

    private lateinit var patientsBox: Box<Patient>
    private lateinit var patientsQuery: Query<Patient>

    private val TAG = "MainActivity"


    @AddTrace(name = "onCreateMainActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)

        context = this

        startService(Intent(this, LocationTrackerService::class.java))



        setupUI()

        listPatients()
    }

    @AddTrace(name = "setupUIMainActivity", enabled = true /* optional */)
    private fun setupUI() {
        loading = findViewById(R.id.loading)
        recyclerView = findViewById(R.id.recyclerView)

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        patientsQuery = patientsBox.query().build()

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(20)
        recyclerView.addItemDecoration(divider)
    }


    @AddTrace(name = "listPatientsMainActivity", enabled = true /* optional */)
    private fun listPatients() {
        listPatientsFromLocalDB()
    }

    @AddTrace(name = "listPatientsFromLocalDBMainActivity", enabled = true /* optional */)
    private fun listPatientsFromLocalDB() {
        Log.d(TAG, "listPatientsFromLocalDB()")
        allPatients = patientsBox.query().equal(Patient_.geography_id, DentalApp.geography).equal(Patient_.activityarea_id, DentalApp.activity).build().find()
        setupAdapter()

    }

    private fun setupAdapter() {
        patientAdapter = PatientAdapter(context, allPatients, object : PatientAdapter.PatientClickListener {
            override fun onDelayPatientClick(patient: Patient) {
                displayDelayDialog(patient)
            }

            override fun onCallPatientClick(patient: Patient) {
                // do the calling
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
        listPatients()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addPatient -> {
                val addPatientActivityIntent = Intent(this, AddPatientActivity::class.java)
                addPatientActivityIntent.putExtra("ACTION", "new")
                startActivity(addPatientActivityIntent)
            }
            R.id.refresh -> {
                listPatients()
            }
            R.id.search -> {
                Log.d("PARAS", "do the search stuff")
                //displaySearchDialog()
                startActivity(Intent(context, SearchActivity::class.java))
            }
            R.id.sync -> {
                Log.d(TAG, "startSync")
                startService(Intent(this, SyncService::class.java))
            }
            R.id.logout -> {
                DentalApp.clearAuthDetails(context)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
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
        delayChooser.setSingleChoiceItems(grpName, -1, DialogInterface.OnClickListener { dialog, item ->
            loading.visibility = View.VISIBLE
            Log.d("DELAYED: ", patient.fullName() + " by " + grpName[item])
            Toast.makeText(this, "Work in progress", Toast.LENGTH_SHORT).show()
            dialog.dismiss()// dismiss the alert box after chose option
        })
        val alert = delayChooser.create()
        alert.show()
    }

    @AddTrace(name = "displaySearchDialogMainActivity", enabled = true /* optional */)
    private fun displaySearchDialog() {
        Log.d("TAG", "displaySearchDialog()")
        val searchDialogView = LayoutInflater.from(this).inflate(R.layout.search_dialog, null)
        val mBuilder = AlertDialog.Builder(this).setView(searchDialogView).setTitle(getString(R.string.search))

        mBuilder.show()
    }


}

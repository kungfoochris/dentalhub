package com.example.dentalhub

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import com.example.dentalhub.interfaces.DjangoInterface
import com.example.dentalhub.services.LocationTrackerService
import com.example.dentalhub.services.SyncService
import com.example.dentalhub.utils.RecyclerViewItemSeparator
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.query.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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

    // lists for permissions
    private var permissionsToRequest: java.util.ArrayList<String>? = null
    private val permissionsRejected = java.util.ArrayList<String>()
    private val permissions = java.util.ArrayList<String>()


    @AddTrace(name = "onCreateMainActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)

        context = this

        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsToRequest = permissionsToRequest(permissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionsToRequest!!.size > 0) {
            requestPermissions(permissionsToRequest!!.toTypedArray(), ALL_PERMISSIONS_RESULT)
        }
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

    private fun permissionsToRequest(wantedPermissions: java.util.ArrayList<String>): java.util.ArrayList<String> {
        val result = java.util.ArrayList<String>()

        for (perm in wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }

    private fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else true

    }

    @AddTrace(name = "listPatientsMainActivity", enabled = true /* optional */)
    private fun listPatients() {
        listPatientsFromLocalDB()
    }

    @AddTrace(name = "listPatientsFromLocalDBMainActivity", enabled = true /* optional */)
    private fun listPatientsFromLocalDB() {
        Log.d(TAG, "listPatientsFromLocalDB()")
        allPatients = patientsQuery.find()
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

    @AddTrace(name = "listPatientsFromServerMainActivity", enabled = true /* optional */)
    private fun listPatientsFromServer() {
        Log.d(TAG, "listPatientsFromServer")
        val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.listPatients("JWT $token")
        call.enqueue(object : Callback<List<Patient>> {
            override fun onFailure(call: Call<List<Patient>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<List<Patient>>, response: Response<List<Patient>>) {
                Log.d(TAG, "onResponse()")
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            allPatients = response.body() as List<Patient>
                            setupAdapter()
                        }
                    }
                }
            }

        })

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
                startActivity(Intent(this, AddPatientActivity::class.java))
            }
            R.id.refresh -> {
                listPatients()
            }
            R.id.search -> {
                Log.d("PARAS", "do the search stuff")
                displaySearchDialog()
            }
            R.id.sync -> {
                startService(Intent(this, SyncService::class.java))
            }
            R.id.logout -> {
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

    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private const val UPDATE_INTERVAL: Long = 5000
        private const val FASTEST_INTERVAL: Long = 5000 // = 5 seconds
        // integer for permissions results request
        private const val ALL_PERMISSIONS_RESULT = 1011
    }


}

package com.abhiyantrik.dentalhub

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.adapters.PatientAdapter
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.query.Query

class SearchPatientActivity : AppCompatActivity() {

    private lateinit var context: Context

    private lateinit var recyclerView: RecyclerView

    private lateinit var patientsearchlist: List<Patient>

    private lateinit var patientsBox: Box<Patient>
    private lateinit var patientQuery: Query<Patient>
    private lateinit var recyclerAdapter: PatientAdapter
    private lateinit var manager: SearchManager
    private lateinit var searchView: SearchView

    private val TAG = "SearchPatientActivity"

    @AddTrace(name = "onCreateSearchPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_search_patient)

        context = this
        title = getString(R.string.search_label)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        setupUI()

    }

    private fun setupUI() {
//        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
//        patientsQuery = patientsBox.query().build()

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        patientQuery = patientsBox.query().build()


        recyclerView = findViewById(R.id.recyclerViewSearchPatient)

        val layoutManager = LinearLayoutManager(context)

        recyclerView.layoutManager = layoutManager

//        val recyclerAdapter = PatientAdapter()



    }

    override fun onResume() {
        super.onResume()
        listPatients()
        //manager.startSearch(null, false, componentName, null, false)
//        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
    }

    private fun listPatients() {
        patientsearchlist =
            patientsBox.query().equal(Patient_.geography_id, DentalApp.geography_id).build().find()
        setupAdapter()
    }

    private fun setupAdapter() {
        recyclerAdapter = PatientAdapter(context, patientsearchlist, object : PatientAdapter.PatientClickListener{
            override fun onViewPatientDetailClick(patient: Patient) {
                val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
                viewPatientIntent.putExtra("patient", patient)
                startActivity(viewPatientIntent)
            }

            override fun onCallPatientClick(patient: Patient) {
                val call = Intent(Intent.ACTION_DIAL)
                call.data = Uri.parse("tel:" + patient.phone)
                startActivity(call)
            }

            override fun onDelayPatientClick(patient: Patient) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
        recyclerView.adapter = recyclerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_patient_menu, menu)


        val searchItem = menu?.findItem(R.id.searchPatient)
        searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()

                if(BuildConfig.DEBUG){
                    Toast.makeText(context, "Looking for the $query", Toast.LENGTH_SHORT).show()
                }

                patientsearchlist = patientsBox.query()
                    .contains(Patient_.first_name, query)
                    .or()
                    .contains(Patient_.last_name, query)
                    .or()
                    .contains(Patient_.last_name, query)
                    .build().find()

                println("Query result is $patientsearchlist")

                setupAdapter()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return true

    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}

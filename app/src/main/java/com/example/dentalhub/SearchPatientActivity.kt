package com.example.dentalhub

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.adapters.PatientAdapter
import com.example.dentalhub.entities.Patient
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.query.Query

class SearchPatientActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var patientsearchlist: List<Patient>

    private lateinit var patinetBox: Box<Patient>
    private lateinit var patinetQuery: Query<Patient>

    private val TAG = "SearchPatientActivity"

    @AddTrace(name = "onCreateSearchPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_search_patient)

        setupUI()

    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerViewSearchPatient)

        val layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager

//        val recyclerAdapter = PatientAdapter()



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_patient_menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.searchPatient)
        val searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()
                Toast.makeText(this@SearchPatientActivity, "Looing for the $query", Toast.LENGTH_SHORT).show()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return true

    }
}

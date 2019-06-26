package com.example.dentalhub

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.dentalhub.adapters.PatientAdapter
import com.example.dentalhub.dbhelpers.DentalHubDBHelper
import com.example.dentalhub.interfaces.DjangoInterface
import com.example.dentalhub.models.Patient
import com.example.dentalhub.utils.RecyclerViewItemSeparator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var   dbHelper: DentalHubDBHelper

    private lateinit var context: Context
    private lateinit var patientAdapter: PatientAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var allPatients: List<Patient>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        setupApp()
        setupUI()

        recyclerView = findViewById(R.id.recyclerView)


        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(20)
        recyclerView.addItemDecoration(divider)

        listPatients()



    }

    private fun setupApp() {
        dbHelper = DentalHubDBHelper(context)
    }

    private fun listPatients() {
        if(DentalApp.isConnectedToWifi(this)){
            listPatientsFromServer()
        }else{
            listPatientsFromLocalDB()
        }
    }

    private fun listPatientsFromLocalDB() {
        allPatients = dbHelper.readAllPatients()
        patientAdapter = PatientAdapter(
            context,
            allPatients,
            object : PatientAdapter.PatientClickListener {
                override fun onAddEncounterButtonClick(patient: Patient) {
                    startActivity(Intent(context, AddEncounterActivity::class.java))
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


    private fun listPatientsFromServer() {
        val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN,"")
        val panelService = DjangoInterface.create(this)
        val call = panelService.listPatients("JWT $token")
        call.enqueue(object: Callback<List<Patient>> {
            override fun onFailure(call: Call<List<Patient>>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<List<Patient>>, response: Response<List<Patient>>) {
                if(null != response.body()){
                    when(response.code()){
                        200 -> {
                            allPatients = response.body() as List<Patient>
                            patientAdapter = PatientAdapter(context, allPatients, object: PatientAdapter.PatientClickListener{
                                override fun onViewPatientDetailClick(patient: Patient) {
                                    val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
                                    viewPatientIntent.putExtra("patient", patient)
                                    startActivity(viewPatientIntent)
                                }

                                override fun onAddEncounterButtonClick(patient: Patient) {
                                    Log.d("BTN", patient.toString())
                                    startActivity(Intent(context, AddEncounterActivity::class.java))
                                }

                            })
                            recyclerView.adapter = patientAdapter
                            patientAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })

    }


    private fun setupUI() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addPatient -> {
                startActivity(Intent(this, AddPatientActivity::class.java))
            }
            R.id.search -> {
                Log.d("PARAS","do the search stuff")
                displaySearchDialog()
            }
            R.id.logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displaySearchDialog() {
        val searchDialogView = LayoutInflater.from(this).inflate(R.layout.search_dialog, null)
        val mBuilder = AlertDialog.Builder(this).setView(searchDialogView).setTitle(getString(R.string.search))
        val mAlertDialog = mBuilder.show()
    }

}

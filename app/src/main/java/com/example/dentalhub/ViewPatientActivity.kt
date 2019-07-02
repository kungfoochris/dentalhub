package com.example.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.entities.Patient
import com.google.firebase.perf.metrics.AddTrace

class ViewPatientActivity: AppCompatActivity(){

    private lateinit var context: Context
    private lateinit var patient: Patient

    private lateinit var btnAddNewEncounter: Button
    private lateinit var tvName: TextView
    private lateinit var tvAddress: TextView

    @AddTrace(name = "onCreateViewPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_patient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        patient = intent.getParcelableExtra("patient")
        context = this

        title = patient.fullName()
        initUI()
    }
    @AddTrace(name = "initUIPatientActivity", enabled = true /* optional */)
    private fun initUI() {
        tvName = findViewById(R.id.tvName)
        tvAddress = findViewById(R.id.tvAddress)
        btnAddNewEncounter = findViewById(R.id.btnAddNewEncounter)

        updateInfo()

        btnAddNewEncounter.setOnClickListener {
            val addEncounterIntent = Intent(context, AddEncounterActivity::class.java)
            addEncounterIntent.putExtra("patient", patient)
            startActivity(addEncounterIntent)
        }
    }

    private fun updateInfo() {
        tvName.text = patient.fullName()
        //tvAddress.text = patient.address
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_patient,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.editPatient -> {
                val addPatientIntent = Intent(this, AddPatientActivity::class.java)
                startActivity(addPatientIntent)
            }
        }
        return super.onOptionsItemSelected(item)
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
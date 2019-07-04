package com.example.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.entities.Patient
import com.google.firebase.perf.metrics.AddTrace
import android.widget.Toast
import android.content.DialogInterface
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.entities.Encounter_
import io.objectbox.Box


class ViewPatientActivity: AppCompatActivity(){

    private lateinit var context: Context
    private lateinit var patient: Patient

    private lateinit var btnAddNewEncounter: Button

    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvEducation: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView

    private lateinit var encounterBox: Box<Encounter>

    val TAG = "ViewPatientActivity"

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
        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        tvAddress = findViewById(R.id.tvAddress)
        tvAge = findViewById(R.id.tvAge)
        tvGender = findViewById(R.id.tvGender)
        tvPhone = findViewById(R.id.tvPhone)
        tvEducation = findViewById(R.id.tvEducation)

        btnAddNewEncounter = findViewById(R.id.btnAddNewEncounter)

        updateInfo()

        listEncounters()

        btnAddNewEncounter.setOnClickListener {
            displayEncounterTypeSelector()
//            val addEncounterIntent = Intent(context, AddEncounterActivity::class.java)
//            addEncounterIntent.putExtra("patient", patient)
//            startActivity(addEncounterIntent)
        }
    }

    private fun listEncounters() {
        val en = patient.encounters
        Log.d(TAG, en.toString())
    }

    private fun updateInfo() {
        tvAge.text = patient.age()
        tvGender.text = patient.gender
        tvPhone.text = patient.phone
        tvEducation.text = patient.education
        tvAddress.text = patient.address()
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

    @AddTrace(name = "displaySearchDialogMainActivity", enabled = true /* optional */)
    private fun displayEncounterTypeSelector() {

        val grpName = arrayOf(getString(R.string.checkup_screening), getString(R.string.relief_of_pain), getString(R.string.continuation_of_treament_plan), getString(R.string.other_problem))
        val encounterTypeChooser = AlertDialog.Builder(this)
        encounterTypeChooser.setTitle(getString(R.string.primary_reason_for_encounter))
        encounterTypeChooser.setSingleChoiceItems(grpName, -1, DialogInterface.OnClickListener { dialog, item ->
            openAddEncounter(grpName[item])
            dialog.dismiss()// dismiss the alertbox after chose option
        })
        val alert = encounterTypeChooser.create()
        alert.show()
    }

    private fun openAddEncounter(encounterType: String) {
        val date = ""

        val encounter = Encounter(0, encounterType, date)
        encounter.patient?.target  = patient
        encounterBox.put(encounter)

        val addEncounterIntent = Intent(context, AddEncounterActivity::class.java)
        addEncounterIntent.putExtra("patient", patient)
        startActivity(addEncounterIntent)
    }
}
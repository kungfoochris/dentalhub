package com.example.dentalhub

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.adapters.EncounterAdapter
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.entities.Encounter_
import com.example.dentalhub.entities.Patient
import com.example.dentalhub.utils.DateHelper
import com.example.dentalhub.utils.RecyclerViewItemSeparator
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box


class ViewPatientActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var patient: Patient

    private lateinit var btnAddNewEncounter: Button
    private lateinit var encounterAdapter: EncounterAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration

    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvEducation: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView
    private lateinit var loading: ProgressBar
    private lateinit var recyclerView: RecyclerView

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
        recyclerView = findViewById(R.id.recyclerView)
        tvAddress = findViewById(R.id.tvAddress)
        tvAge = findViewById(R.id.tvAge)
        tvGender = findViewById(R.id.tvGender)
        tvPhone = findViewById(R.id.tvPhone)
        tvEducation = findViewById(R.id.tvEducation)
        loading = findViewById(R.id.loading)

        btnAddNewEncounter = findViewById(R.id.btnAddNewEncounter)

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(20)
        recyclerView.addItemDecoration(divider)



        updateInfo()

        listEncounters()

        btnAddNewEncounter.setOnClickListener {
            displayEncounterTypeSelector()
        }
    }

    private fun listEncounters() {
        val allEnCounters = encounterBox.query().equal(Encounter_.patientId, patient.id).orderDesc(Encounter_.id).build().find()
        encounterAdapter = EncounterAdapter(context, patient, allEnCounters, object : EncounterAdapter.EncounterClickListener {
            override fun onEncounterClick(encounter: Encounter) {
                // start the encounter view
                Log.d("View PatientActivity", "show encounter detail")
                val encounterDetailIntent: Intent = Intent(context, ViewEncounterActivity::class.java)
                encounterDetailIntent.putExtra("ENCOUNTER_ID", encounter.id.toLong())
                startActivity(encounterDetailIntent)
            }

        })
        recyclerView.adapter = encounterAdapter
        encounterAdapter.notifyDataSetChanged()
    }

    private fun updateInfo() {
        tvAge.text = patient.age()
        tvGender.text = patient.gender
        tvPhone.text = patient.phone
        tvEducation.text = patient.education
        tvAddress.text = patient.address()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_patient, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editPatient -> {
                val addPatientIntent = Intent(this, AddPatientActivity::class.java)
                addPatientIntent.putExtra("patient", patient)
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

        val grpName = arrayOf(
            getString(R.string.checkup_screening),
            getString(R.string.relief_of_pain),
            getString(R.string.continuation_of_treatment_plan),
            getString(R.string.other_problem)
        )
        val encounterTypeChooser = AlertDialog.Builder(this)
        encounterTypeChooser.setTitle(getString(R.string.primary_reason_for_encounter))
        encounterTypeChooser.setSingleChoiceItems(grpName, -1, DialogInterface.OnClickListener { dialog, item ->
            loading.visibility = View.VISIBLE
            openAddEncounter(grpName[item])
            dialog.dismiss()// dismiss the alert box after chose option
        })
        val alert = encounterTypeChooser.create()
        alert.show()
    }

    override fun onResume() {
        super.onResume()
        listEncounters()
        loading.visibility = View.GONE
    }

    private fun openAddEncounter(encounterType: String) {
        val date = DateHelper.getCurrentDate()

        val encounter = Encounter()
        encounter.id = 0
        encounter.encounter_type = encounterType
        encounter.created_at = date
        encounter.updated_at = date
        encounter.patient?.target = patient
        encounterBox.put(encounter)

        val addEncounterIntent = Intent(context, AddEncounterActivity::class.java)
        addEncounterIntent.putExtra("patient", patient)
        startActivity(addEncounterIntent)
    }
}
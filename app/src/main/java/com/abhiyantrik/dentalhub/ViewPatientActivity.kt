package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.adapters.EncounterAdapter
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.exception.DbException


class ViewPatientActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var patient: Patient

//    private lateinit var btnAddNewEncounter: Button
    private lateinit var fabAddNewEncounter: FloatingActionButton
    private lateinit var fabEditPatient: FloatingActionButton
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
    private lateinit var patientBox: Box<Patient>
    var patientId:Long = 0

    val TAG = "ViewPatientActivity"

    @AddTrace(name = "onCreateViewPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_patient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        patientId = intent.getLongExtra("PATIENT_ID", 0)
        context = this

        if(patientId==0.toLong()) {
            Toast.makeText(context, "Invalid patient id", Toast.LENGTH_LONG).show()
            finish()
        }

        supportActionBar?.setHomeButtonEnabled(true)


        initUI()
        listEncounters()
    }

    @AddTrace(name = "initUIPatientActivity", enabled = true /* optional */)
    private fun initUI() {
        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        patientBox = ObjectBox.boxStore.boxFor(Patient::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        tvAddress = findViewById(R.id.tvAddress)
        tvAge = findViewById(R.id.tvAge)
        tvGender = findViewById(R.id.tvGender)
        tvPhone = findViewById(R.id.tvPhone)
        tvEducation = findViewById(R.id.tvEducation)
        loading = findViewById(R.id.loading)

        fabAddNewEncounter = findViewById(R.id.fabAddNewEncounter)
        fabEditPatient = findViewById(R.id.fabEditPatient)

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager as RecyclerView.LayoutManager?
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(20)
        recyclerView.addItemDecoration(divider)

        getUpdatedPatient()

        fabAddNewEncounter.setOnClickListener {
            displayEncounterTypeSelector()
        }
        fabEditPatient.setOnClickListener {
            val addPatientIntent = Intent(this, AddPatientActivity::class.java)
            addPatientIntent.putExtra("PATIENT_ID", patientId)
            addPatientIntent.putExtra("ACTION", "edit")
            startActivity(addPatientIntent)
        }
    }

    private fun listEncounters() {
        if(patientId==0.toLong()){
            Toast.makeText(context, "Invalid patient id", Toast.LENGTH_LONG).show()
            finish()
        }
        val allEnCounters =
            encounterBox.query().equal(Encounter_.patientId, patientId).orderDesc(Encounter_.id).build().find()

        patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()!!

        encounterAdapter =
            EncounterAdapter(context, patient, allEnCounters, object : EncounterAdapter.EncounterClickListener {
                override fun onEncounterClick(encounter: Encounter) {
                    // start the encounter view
                    Log.d("View PatientActivity", "show encounter detail")
                    val encounterDetailIntent = Intent(context, ViewEncounterActivity::class.java)
                    encounterDetailIntent.putExtra("ENCOUNTER_ID", encounter.id)
                    encounterDetailIntent.putExtra("PATIENT_ID", patientId)
                    startActivity(encounterDetailIntent)
                }

            })
        recyclerView.adapter = encounterAdapter
        encounterAdapter.notifyDataSetChanged()
    }

    private fun updateInfo() {
        title = patient.fullName()
        tvAge.text = patient.age()
        tvGender.text = patient.gender.capitalize()
        tvPhone.text = patient.phone
        tvEducation.text = patient.education.capitalize()
//        val municipality =
        tvAddress.text = patient.address()
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
        //getUpdatedPatient()
        listEncounters()
        loading.visibility = View.GONE
    }

    private fun getUpdatedPatient() {
        Log.d("Patient ID", patientId.toString())
        if(patientId!=0.toLong()){
            try{
                patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()!!
                updateInfo()
            }catch (e: DbException){
                Log.d("DBException", e.printStackTrace().toString())
            }
        }

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
        addEncounterIntent.putExtra("PATIENT_ID", patientId)
        addEncounterIntent.putExtra("ENCOUNTER_ID", "0".toLong())
        startActivity(addEncounterIntent)
    }
}
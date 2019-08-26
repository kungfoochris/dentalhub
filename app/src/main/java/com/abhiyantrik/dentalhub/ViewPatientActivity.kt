package com.abhiyantrik.dentalhub

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var patientBox: Box<Patient>

    val TAG = "ViewPatientActivity"

    @AddTrace(name = "onCreateViewPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_patient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        patient = intent.getParcelableExtra("patient")
        context = this

        supportActionBar?.setHomeButtonEnabled(true)

        title = patient.fullName()
        initUI()
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
        val allEnCounters =
            encounterBox.query().equal(Encounter_.patientId, patient.id).orderDesc(Encounter_.id).build().find()
        encounterAdapter =
            EncounterAdapter(context, patient, allEnCounters, object : EncounterAdapter.EncounterClickListener {
                override fun onEncounterClick(encounter: Encounter) {
                    // start the encounter view
                    Log.d("View PatientActivity", "show encounter detail")
                    val encounterDetailIntent: Intent = Intent(context, ViewEncounterActivity::class.java)
                    encounterDetailIntent.putExtra("ENCOUNTER_ID", encounter.id)
                    startActivity(encounterDetailIntent)
                }

            })
        recyclerView.adapter = encounterAdapter
        encounterAdapter.notifyDataSetChanged()
    }

    private fun updateInfo() {
        title = patient.fullName()
        tvAge.text = patient.age()
        tvGender.text = patient.gender
        tvPhone.text = patient.phone
        tvEducation.text = patient.education
//        val municipality =
        tvAddress.text = patient.address()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_patient, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                Log.d(TAG, "Display Detail")
                println("Name is selected")
            }
            R.id.editPatient -> {
                val addPatientIntent = Intent(this, AddPatientActivity::class.java)
                addPatientIntent.putExtra("patient", patient)
                addPatientIntent.putExtra("ACTION", "edit")
                startActivity(addPatientIntent)
            }
            R.id.viewPatient -> {
                val builder : AlertDialog.Builder = AlertDialog.Builder(this)
                val inflate : LayoutInflater = layoutInflater
                val view : View = inflate.inflate(R.layout.popup_view_patient, null)

                // to get all id of the textView
                val tvFirstNameView = view.findViewById<TextView>(R.id.tvFirstNameView)
                val tvMiddleNameView = view.findViewById<TextView>(R.id.tvMiddleNameView)
                val tvLastNameView = view.findViewById<TextView>(R.id.tvLastNameView)
                val tvGenderpopupView = view.findViewById<TextView>(R.id.tvGenderpopupView)
                val tvDateofBirthView = view.findViewById<TextView>(R.id.tvDateofBirthView)
                val tvPhonepopupView = view.findViewById<TextView>(R.id.tvPhonepopupView)
                val tvWardView = view.findViewById<TextView>(R.id.tvWardView)
                val tvMunicipalityView = view.findViewById<TextView>(R.id.tvMunicipalityView)
                val tvDistrictView = view.findViewById<TextView>(R.id.tvDistrictView)
                val tvEducationLevelView = view.findViewById<TextView>(R.id.tvEducationLevelView)
                val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnCloseDialog)

                // to set the details of the patient on Alert Dialog i.e. View Patient
                tvFirstNameView.text = patient.first_name
                tvMiddleNameView.text = patient.middle_name
                tvLastNameView.text = patient.last_name
                tvGenderpopupView.text = patient.gender
                tvDateofBirthView.text = patient.dob
                tvPhonepopupView.text = patient.phone
                tvWardView.text = patient.ward.toString()
                tvMunicipalityView.text = patient.municipalityName()
                tvDistrictView.text = patient.districtName()
                tvEducationLevelView.text = patient.education


                builder.setView(view)
                val dialog : Dialog = builder.create()
                dialog.show()

                btnCloseDialog.setOnClickListener {
                    dialog.dismiss()
                }

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
        getUpdatedPatient()
        listEncounters()
        loading.visibility = View.GONE
    }

    private fun getUpdatedPatient() {
        patient = patientBox.query().equal(Patient_.id, patient.id).build().findFirst()!!
        updateInfo()
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
        addEncounterIntent.putExtra("ENCOUNTER_ID", "0".toLong())
        startActivity(addEncounterIntent)
    }
}
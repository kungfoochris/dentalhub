package com.abhiyantrik.dentalhub

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
    var patientId: Long = 0

    val TAG = "ViewPatientActivity"

    @AddTrace(name = "onCreateViewPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_patient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        patientId = intent.getLongExtra("PATIENT_ID", 0)
        context = this

        if (patientId == 0.toLong()) {
            Toast.makeText(context, "Invalid patient id", Toast.LENGTH_LONG).show()
            finish()
        }
        DentalApp.saveIntToPreference(context, Constants.PREF_SELECTED_PATIENT, patientId.toInt())
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
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(0)
        recyclerView.addItemDecoration(divider)

        getUpdatedPatient()

        fabAddNewEncounter.setOnClickListener {
            //            displayEncounterTypeSelector()
            displayEncounterTypeSelectorPopUp()
        }
        fabEditPatient.setOnClickListener {
            val addPatientIntent = Intent(this, AddPatientActivity::class.java)
            addPatientIntent.putExtra("PATIENT_ID", patientId)
            addPatientIntent.putExtra("ACTION", "edit")
            startActivity(addPatientIntent)
        }
    }

    private fun listEncounters() {
        if (patientId == 0.toLong()) {
            Log.d("PT ID ", patientId.toString())
            Toast.makeText(context, "Invalid patient id", Toast.LENGTH_LONG).show()
            finish()
        }
        val allEnCounters =
            encounterBox.query().equal(Encounter_.patientId, patientId).orderDesc(Encounter_.id)
                .build().find()

        patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()!!

        encounterAdapter =
            EncounterAdapter(
                context,
                patient,
                allEnCounters,
                object : EncounterAdapter.EncounterClickListener {
                    override fun onEncounterClick(encounter: Encounter) {
                        // start the encounter view
                        Log.d("View PatientActivity", "show encounter detail")
                        val encounterDetailIntent =
                            Intent(context, ViewEncounterActivity::class.java)
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
        encounterTypeChooser.setSingleChoiceItems(
            grpName,
            -1,
            DialogInterface.OnClickListener { dialog, item ->
                loading.visibility = View.VISIBLE
                openAddEncounter(grpName[item], "")
                dialog.dismiss()// dismiss the alert box after chose option
            })
        val alert = encounterTypeChooser.create()
        alert.show()
    }

    private fun displayEncounterTypeSelectorPopUp() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflate: LayoutInflater = layoutInflater
        val view: View = inflate.inflate(R.layout.popup_add_encounter_selector, null)

        // to get all id of the Button
        val rgAddEncounter = view.findViewById<RadioGroup>(R.id.rgAddEncounter)
//        val rbCheckupScreening = view.findViewById<RadioButton>(R.id.rbCheckupScreening)
//        val rbReliefOfPain = view.findViewById<RadioButton>(R.id.rbReliefOfPain)
//        val rbContinuationOfTreatmentPlan =
//            view.findViewById<RadioButton>(R.id.rbContinuationOfTreatmentPlan)
        val rbOtherProblem = view.findViewById<RadioButton>(R.id.rbOtherProblem)
        val etOtherProblem = view.findViewById<EditText>(R.id.etOtherProblemPopUp)
        val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnAddEncounter = view.findViewById<Button>(R.id.btnAddEncounter)

        etOtherProblem.visibility = View.INVISIBLE

        builder.setView(view)
        val dialog: Dialog = builder.create()
        dialog.show()

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        rgAddEncounter.setOnCheckedChangeListener { _, i ->
            if (i == R.id.rbOtherProblem) {
                etOtherProblem.visibility = View.VISIBLE
            } else {
                etOtherProblem.setText("")
                etOtherProblem.visibility = View.INVISIBLE
            }
        }

        btnAddEncounter.setOnClickListener {
            val radioBtnID = rgAddEncounter.checkedRadioButtonId
            if (radioBtnID != -1) {
                if (rbOtherProblem.isChecked && etOtherProblem.text.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "Other Problem text is empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val selectedBtn: RadioButton = view.findViewById(radioBtnID)
//                    Toast.makeText(this, "Selected is ${selectedBtn.text} ${etOtherProblem.text}", Toast.LENGTH_SHORT).show()

                    openAddEncounter(selectedBtn.text.toString(), etOtherProblem.text.toString())
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Radio button is not selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getUpdatedPatient()
        listEncounters()
        loading.visibility = View.GONE
    }

    private fun getUpdatedPatient() {
        Log.d("Patient ID", patientId.toString())
        if (patientId != 0.toLong()) {
            try {
                patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()!!
                updateInfo()
            } catch (e: DbException) {
                Log.d("DBException", e.printStackTrace().toString())
            }
        }

    }

    private fun openAddEncounter(encounterType: String, otherProblem: String) {
        val date = DateHelper.getCurrentNepaliDate()

        val encounter = Encounter()
        encounter.id = 0
        encounter.encounter_type = encounterType
        encounter.other_problem = otherProblem
        encounter.activityarea_id = DentalApp.activity_id
        encounter.ward_id = DentalApp.ward_id
        encounter.created_at = date
        encounter.updated_at = date
        encounter.author =
            DentalApp.readFromPreference(applicationContext, Constants.PREF_PROFILE_ID, "")
        encounter.updated_by =
            DentalApp.readFromPreference(applicationContext, Constants.PREF_PROFILE_ID, "")
        encounter.patient?.target = patient
        encounterBox.put(encounter)

        val addEncounterIntent = Intent(context, AddEncounterActivity::class.java)
        addEncounterIntent.putExtra("PATIENT_ID", patientId)
        addEncounterIntent.putExtra("ENCOUNTER_ID", "0".toLong())
        startActivity(addEncounterIntent)
    }
}
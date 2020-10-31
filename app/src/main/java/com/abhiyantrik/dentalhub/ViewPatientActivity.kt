package com.abhiyantrik.dentalhub

import android.app.Dialog
import android.content.Context
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
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import io.objectbox.exception.DbException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class ViewPatientActivity : AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var patient: Patient

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

    private var modifyDeleteReason: String = ""
    private var modifyDeleteOtherReason: String = ""
    private var modifyDeleteEncounterId: String = ""

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
            Timber.d("PT ID "+ patientId.toString())
            Toast.makeText(context, "Invalid patient id", Toast.LENGTH_LONG).show()
            finish()
        }
        val allEnCounters =
            encounterBox.query().equal(Encounter_.patientId, patientId)
                .orderDesc(Encounter_.created_at)
                .orderDesc(Encounter_.id)
                .build().find()

        patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()!!

        encounterAdapter =
            EncounterAdapter(
                context,
                patient,
                allEnCounters,
                object : EncounterAdapter.EncounterClickListener {
                    override fun onModificationFlagClick(encounterId: String, isModifyable: Boolean) {
                        modifyDeleteEncounterId = encounterId
                        displayModificationTypePopup(isModifyable)
                    }

                    override fun onEncounterClick(encounter: Encounter) {
                        // start the encounter view
                        Timber.d("View PatientActivity show encounter detail")
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

    private fun displayEncounterTypeSelectorPopUp() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflate: LayoutInflater = layoutInflater
        val view: View = inflate.inflate(R.layout.popup_add_encounter_selector, null)

        // to get all id of the Button
        val rgAddEncounter = view.findViewById<RadioGroup>(R.id.rgAddEncounter)
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
                    openAddEncounter(selectedBtn.text.toString(), etOtherProblem.text.toString())
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this, "Radio button is not selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun displayModificationTypePopup(isModify: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val view: View = inflater.inflate(R.layout.popup_modification_flag_choose, null)

        val tvModifyEncounter = view.findViewById<TextView>(R.id.tvModifyEncounter)
        val tvDeleteEncounter = view.findViewById<TextView>(R.id.tvDeleteEncounter)
        val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnModifyEncounterCloseDialog)

        builder.setView(view)
        val dialog: Dialog = builder.create()
        dialog.show()

        if (isModify) {
            tvModifyEncounter.visibility = View.GONE
        }

        tvModifyEncounter.setOnClickListener {
            Timber.d("Modify iv clicked.")
            displayModifyPopup()
            dialog.dismiss()
        }

        tvDeleteEncounter.setOnClickListener {
            Timber.d("Delete iv clicked.")
            displayDeletePopup()
            dialog.dismiss()
        }

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun displayModifyPopup() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val view: View = inflater.inflate(R.layout.popup_modify_request, null)

        val etModifyMessage = view.findViewById<EditText>(R.id.etModifyMessage)
        val btnRequestModify = view.findViewById<Button>(R.id.btnRequestModify)
        val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnModifyCloseDialog)

        builder.setView(view)
        val dialog: Dialog = builder.create()
        dialog.show()

        btnRequestModify.setOnClickListener {
            if (etModifyMessage.text.toString().isBlank()) {
                Toast.makeText(this, "Enter reason to modify.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Timber.d("Request Modify button clicked.")
            modifyDeleteReason = etModifyMessage.text.toString()
            displayPasswordVerifyPopup(isModify = true)
            dialog.dismiss()
        }

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun displayDeletePopup() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val view: View = inflater.inflate(R.layout.popup_delete_request, null)

        val rgDeleteRequest = view.findViewById<RadioGroup>(R.id.rgDeleteRequest)
        val rbDeleteAccidentalEntry = view.findViewById<RadioButton>(R.id.rbDeleteAccidentalEntry)
        val rbDeleteDuplicateEncounter = view.findViewById<RadioButton>(R.id.rbDeleteDuplicateEncounter)
        val rbDeleteIncorrectPatient = view.findViewById<RadioButton>(R.id.rbDeleteIncorrectPatient)
        val rbDeleteIncorrectUser = view.findViewById<RadioButton>(R.id.rbDeleteIncorrectUser)
        val rbDeleteOtherReason = view.findViewById<RadioButton>(R.id.rbDeleteOtherReason)
        val etDeleteOtherReasonMessage = view.findViewById<EditText>(R.id.etDeleteOtherReasonMessage)
        val btnRequestDelete = view.findViewById<Button>(R.id.btnRequestDelete)
        val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnDeleteEncounterCloseDialog)

        builder.setView(view)
        val dialog: Dialog = builder.create()
        dialog.show()

        rgDeleteRequest.setOnCheckedChangeListener { _, i ->
            if (i == R.id.rbDeleteOtherReason) {
                etDeleteOtherReasonMessage.visibility = View.VISIBLE
            } else {
                etDeleteOtherReasonMessage.setText("")
                etDeleteOtherReasonMessage.visibility = View.INVISIBLE
            }
        }

        btnRequestDelete.setOnClickListener {
            if (rgDeleteRequest.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Select one option.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (rbDeleteOtherReason.isChecked && etDeleteOtherReasonMessage.text.toString().isBlank()) {
                Toast.makeText(this, "Enter the reason.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Timber.d("Request Delete button clicked.")
            if (rbDeleteAccidentalEntry.isChecked) modifyDeleteReason = "accidental_entry"
            if (rbDeleteDuplicateEncounter.isChecked) modifyDeleteReason = "duplicate_encounter"
            if (rbDeleteIncorrectPatient.isChecked) modifyDeleteReason = "incorrect_patient"
            if (rbDeleteIncorrectUser.isChecked) modifyDeleteReason = "incorrect_user"
            if (rbDeleteOtherReason.isChecked) modifyDeleteReason = "other"
            displayPasswordVerifyPopup(isModify = false)
            dialog.dismiss()
        }

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun displayPasswordVerifyPopup(isModify: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val view: View = inflater.inflate(R.layout.popup_password_verify, null)

        val btnVerifyPassword = view.findViewById<Button>(R.id.btnVerifyPassword)
        val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnPasswordVerifyCloseDialog)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)

        if (isModify) {
            btnVerifyPassword.text = "Request Modify"
        } else {
            btnVerifyPassword.text = "Request Delete"
        }

        builder.setView(view)
        val dialog: Dialog = builder.create()
        dialog.show()

        btnVerifyPassword.setOnClickListener {
            if (etConfirmPassword.text.toString().isBlank()) {
                Toast.makeText(this, "Enter password.", Toast.LENGTH_SHORT).show()
            }
            if (etConfirmPassword.text.toString() ==
                DentalApp.readFromPreference(
                    this@ViewPatientActivity,
                    Constants.PREF_AUTH_PASSWORD,
                    ""
                )
            ) {
                Timber.d("Password verified.")
                GlobalScope.launch(Dispatchers.IO) {
                    val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
                    val panelService = DjangoInterface.create(context)
                    var call: Any
                    if (isModify) {
                        call = panelService.modifyEncounterFlag(
                            "JWT $token",
                            modifyDeleteEncounterId,
                            modifyDeleteReason,
                            "modify"
                        )
                    } else {
                        call = panelService.deleteEncounterFlag(
                            "JWT $token",
                            modifyDeleteEncounterId,
                            modifyDeleteReason,
                            modifyDeleteOtherReason,
                            "delete"
                        )
                    }
                    try {
                        val response = call.execute()
                        Timber.d("FlagResponse: "+ "${response.code()} and ${response.message()} then ${response.body()}")
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@ViewPatientActivity, "Successfully submitted..", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@ViewPatientActivity, "Failed. Try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@ViewPatientActivity, "Failed. Try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (ex: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(ex)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ViewPatientActivity, "Failed. Try again. ${ex.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Wrong password.", Toast.LENGTH_SHORT).show()
            }
        }

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        getUpdatedPatient()
        listEncounters()
        loading.visibility = View.GONE
    }

    private fun getUpdatedPatient() {
        Timber.d("Patient ID: %s", patientId.toString())
        if (patientId != 0.toLong()) {
            try {
                patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()!!
                updateInfo()
            } catch (e: DbException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Timber.d("DBException: %s", e.printStackTrace().toString())
            }
        }
    }

    private fun openAddEncounter(encounterType: String, otherProblem: String) {

        val addEncounterIntent = Intent(context, AddEncounterActivity::class.java)
        addEncounterIntent.putExtra("PATIENT_ID", patientId)
        addEncounterIntent.putExtra("ENCOUNTER_ID", "0".toLong())
        addEncounterIntent.putExtra("ENCOUNTER_TYPE", encounterType)
        addEncounterIntent.putExtra("OTHER_PROBLEM", otherProblem)
        addEncounterIntent.putExtra("MODIFY_DELETE", "0".toLong())
        startActivity(addEncounterIntent)
    }
}
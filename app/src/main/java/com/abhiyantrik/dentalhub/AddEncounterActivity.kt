package com.abhiyantrik.dentalhub

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abhiyantrik.dentalhub.adapters.FormPageAdapter
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.fragments.interfaces.HistoryFormCommunicator
import com.abhiyantrik.dentalhub.fragments.interfaces.ReferralFormCommunicator
import com.abhiyantrik.dentalhub.fragments.interfaces.ScreeningFormCommunicator
import com.abhiyantrik.dentalhub.fragments.interfaces.TreatmentFormCommunicator
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.workers.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AddEncounterActivity : AppCompatActivity(), TreatmentFragmentCommunicator,
    HistoryFormCommunicator,
    ScreeningFormCommunicator, TreatmentFormCommunicator, ReferralFormCommunicator {

    private lateinit var pager: ViewPager
    private lateinit var patient: Patient
    private lateinit var btnHistory: Button
    private lateinit var btnScreening: Button
    private lateinit var btnTreatment: Button
    private lateinit var btnReferral:Button

    private lateinit var patientBox: Box<Patient>
    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>
    private lateinit var recallBox: Box<Recall>

    private lateinit var context: Context

    private var history = History()
    private var screening = Screening()
    private var treatment = Treatment()
    private var referral = Referral()
    private var recall = Recall()
    private var encounter = Encounter()
    var encounterFlagId: Long = 0
    var encounterId: Long = 0
    var patientId: Long = 0

    var action = "new"


    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_encounter)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        patientBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        historyBox = ObjectBox.boxStore.boxFor(History::class.java)
        screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
        treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)
        recallBox = ObjectBox.boxStore.boxFor(Recall::class.java)

        if (DentalApp.activity_id == "" || DentalApp.geography_id < 1) {
            Timber.d("${MainActivity.TAG} Activity is not been selected.")
            FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ " Activity has not been selected")
            logout()
        }

        if (DentalApp.activity_id != "" && DentalApp.activity_id != "1" && DentalApp.activity_area_id < 1) {
            logout()
        }

        context = this
        patientId = intent.getLongExtra("PATIENT_ID", 0.toLong())
        patient = patientBox.query().equal(Patient_.id, patientId).build().findFirst()!!

        title = patient.fullName()

        encounterId = intent.getLongExtra("ENCOUNTER_ID", "0".toLong())
        encounterFlagId = intent.getLongExtra("MODIFY_DELETE", "0".toLong())
        val encounterType = intent.getStringExtra("ENCOUNTER_TYPE")
        val otherProblem = intent.getStringExtra("OTHER_PROBLEM")
        Timber.d("Encounter ID = $encounterId")

        if (encounterId == "0".toLong()) {
            action = "new"

//            val date = DateHelper.getTodaysNepaliDate()
            val date = DentalApp.readFromPreference(context, Constants.PERF_SELECTED_BACKDATE, DateHelper.getTodaysNepaliDate())

            val currentDate = SimpleDateFormat("yyyy-MM-dd").parse(date)
            if (currentDate == null) {
                Toast.makeText(context, "There is problem in Encounter date so please contact to Abhiyantrik or Saroj Dhakal.", Toast.LENGTH_LONG).show()
            }
            Timber.d("CheckingDate %s", currentDate.toString())
            val currentDateTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            Timber.d("CheckingDate %s", dateFormat.format(currentDate))
            Timber.d("CheckingDate %s", dateFormat.format(currentDate))
            val modifiedNewDate = dateFormat.format(currentDate).substring(0, 10) + dateFormat.format(currentDateTime).substring(10)

//            val encounter = Encounter()
//            encounter.id = 0
            if (!encounterType.isNullOrEmpty()) {
                encounter.encounter_type = encounterType
            } else {
                Toast.makeText(this, "Encounter Type unrecognized.", Toast.LENGTH_LONG).show()
                finish()
            }
            if (!otherProblem.isNullOrEmpty()) {
                encounter.other_problem = otherProblem
            }
            encounter.activityarea_id = DentalApp.activity_id
            encounter.area_id = DentalApp.activity_area_id.toInt()
            encounter.ward_id = DentalApp.geography_id
            encounter.created_at = modifiedNewDate
            encounter.updated_at = date
            encounter.author =
                DentalApp.readFromPreference(applicationContext, Constants.PREF_PROFILE_ID, "")
            encounter.updated_by =
                DentalApp.readFromPreference(applicationContext, Constants.PREF_PROFILE_ID, "")
            encounter.patient?.target = patient
//            encounterBox.put(encounter)

//            encounter = encounterBox.query().orderDesc(Encounter_.id).build().findFirst()!!

            history.encounter?.target = encounter
//            historyBox.put(history)

            screening.encounter?.target = encounter
//            screeningBox.put(screening)

            treatment.encounter?.target = encounter
//            treatmentBox.put(treatment)

            referral.encounter?.target = encounter
//            referralBox.put(referral)

            recall.patient?.target = patient
            recall.encounter?.target = encounter
//            recallBox.put(recall)

            DentalApp.saveToPreference(this, "Encounter_ID", "0")

        } else {
            action = "edit"
            DentalApp.saveToPreference(this, "Encounter_ID", encounterId.toString())

            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!
            encounter.updated = true
            encounter.updated_at = DateHelper.getTodaysNepaliDate()
//            encounterBox.put(encounter)

            history =
                historyBox.query().equal(
                    History_.encounterId,
                    encounter.id
                ).orderDesc(History_.id).build().findFirst()!!

            screening = screeningBox.query().equal(
                Screening_.encounterId,
                encounter.id
            ).orderDesc(Screening_.id).build().findFirst()!!

            treatment = treatmentBox.query().equal(
                Treatment_.encounterId,
                encounter.id
            ).orderDesc(Treatment_.id).build().findFirst()!!

            referral =
                referralBox.query().equal(
                    Referral_.encounterId,
                    encounter.id
                ).orderDesc(Referral_.id).build().findFirst()!!

        }

        initUI()

    }

    private fun logout() {
        DentalApp.clearAuthDetails(context)
        Toast.makeText(context, "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(context, LoginActivity::class.java))
        finish()
    }

    @AddTrace(name = "initUITrace", enabled = true /* optional */)
    private fun initUI() {

        pager = findViewById(R.id.pager)
        btnHistory = findViewById(R.id.btnHistory)
        btnScreening = findViewById(R.id.btnScreening)
        btnTreatment = findViewById(R.id.btnTreatment)
        btnReferral = findViewById(R.id.btnReferral)
        pager.beginFakeDrag()
        updateIndex(0)
        val fragmentAdapter = FormPageAdapter(supportFragmentManager)
        pager.adapter = fragmentAdapter

    }

    private fun updateIndex(i: Int) {
        btnHistory.background = ResourcesCompat.getDrawable(resources, R.drawable.default_tab_color, null)
        btnScreening.background = ResourcesCompat.getDrawable(resources, R.drawable.default_tab_color, null)
        btnTreatment.background = ResourcesCompat.getDrawable(resources, R.drawable.default_tab_color, null)
        btnReferral.background = ResourcesCompat.getDrawable(resources, R.drawable.default_tab_color, null)
        when(i){
            0 -> {
                btnHistory.background = ResourcesCompat.getDrawable(resources, R.drawable.selected_tab_color, null)
            }
            1 -> {
                btnScreening.background = ResourcesCompat.getDrawable(resources, R.drawable.selected_tab_color, null)
            }
            2 -> {
                btnTreatment.background = ResourcesCompat.getDrawable(resources, R.drawable.selected_tab_color, null)
        }
            3-> {
                btnReferral.background = ResourcesCompat.getDrawable(resources, R.drawable.selected_tab_color, null)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_patient_info, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                Timber.d("onOptionsItemSelected() Back button pressed.")
//                saveEncounter()
            }
            R.id.viewPatient -> {
                addEncounterDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Dialog started.
    private fun addEncounterDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflate: LayoutInflater = layoutInflater
        val view: View = inflate.inflate(R.layout.popup_view_patient, null)

        // to get all id of the textView
        val tvFirstNameView = view.findViewById<TextView>(R.id.tvFirstNameView)
        val tvMiddleNameView = view.findViewById<TextView>(R.id.tvMiddleNameView)
        val tvLastNameView = view.findViewById<TextView>(R.id.tvLastNameView)
        val tvGenderPopupView = view.findViewById<TextView>(R.id.tvGenderpopupView)
        val tvDateOfBirthView = view.findViewById<TextView>(R.id.tvDateofBirthView)
        val tvPhonePopupView = view.findViewById<TextView>(R.id.tvPhonepopupView)
        val tvWardView = view.findViewById<TextView>(R.id.tvWardView)
        val tvMunicipalityView = view.findViewById<TextView>(R.id.tvMunicipalityView)
        val tvDistrictView = view.findViewById<TextView>(R.id.tvDistrictView)
        val tvEducationLevelView = view.findViewById<TextView>(R.id.tvEducationLevelView)
        val btnCloseDialog = view.findViewById<ImageButton>(R.id.btnCloseDialog)

        // to set the details of the patient on Alert Dialog i.e. View Patient
        tvFirstNameView.text = patient.first_name
        tvMiddleNameView.text = patient.middle_name
        tvLastNameView.text = patient.last_name
        tvGenderPopupView.text = patient.gender.capitalize()
        tvDateOfBirthView.text = DateHelper.formatNepaliDate(context, patient.dob)
        tvPhonePopupView.text = patient.phone
        tvWardView.text = patient.wardNumber()
        tvMunicipalityView.text = patient.municipalityName()
        tvDistrictView.text = patient.districtName()
        tvEducationLevelView.text = patient.education.capitalize()

        builder.setView(view)
        val dialog: Dialog = builder.create()
        dialog.show()

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

    }
    // Dialog ended

    override fun updateHistory(
        bloodDisorders: Boolean,
        diabetes: Boolean,
        liverProblem: Boolean,
        rheumaticFever: Boolean,
        seizuresOrEpilepsy: Boolean,
        hepatitisBOrC: Boolean,
        hiv: Boolean,
        other: String,
        highBloodPressure: Boolean,
        lowBloodPressure: Boolean,
        thyroidDisorder: Boolean,
        noUnderlyingMedicalCondition: Boolean,
        medications: String,
        notTakingAnyMedications: Boolean,
        noAllergies: Boolean,
        allergies: String
    ) {

//        history =
//            historyBox.query().equal(
//                History_.encounterId,
//                encounter.id
//            ).orderDesc(History_.id).build().findFirst()!!

        history.blood_disorder = bloodDisorders
        history.diabetes = diabetes
        history.liver_problem = liverProblem
        history.rheumatic_fever = rheumaticFever
        history.seizuers_or_epilepsy = seizuresOrEpilepsy
        history.hepatitis_b_or_c = hepatitisBOrC
        history.hiv = hiv
        history.other = other
        history.high_blood_pressure = highBloodPressure
        history.low_blood_pressure = lowBloodPressure
        history.thyroid_disorder = thyroidDisorder
        history.medications = medications
        history.no_underlying_medical_condition = noUnderlyingMedicalCondition
        history.not_taking_any_medications = notTakingAnyMedications
        history.no_allergies = noAllergies
        history.allergies = allergies
        history.updated = true
//        historyBox.put(history)
    }

    override fun updateScreening(
        carriesRisk: String,
        decayedPrimaryTeeth: String,
        decayedPermanentTeeth: String,
        cavityPermanentTooth: Boolean,
        cavityPermanentAnterior: Boolean,
        reversiblePulpitis: Boolean,
        needARTFilling: Boolean,
        needSealant: Boolean,
        needSDF: Boolean,
        needExtraction: Boolean,
        activeInfection: Boolean
    ) {

//        screening = screeningBox.query().equal(
//            Screening_.encounterId,
//            encounter.id
//        ).orderDesc(Screening_.id).build().findFirst()!!

        screening.carries_risk = carriesRisk
        try {
            screening.decayed_primary_teeth = decayedPrimaryTeeth.toInt()
        } catch (e: NumberFormatException) {
            screening.decayed_primary_teeth = 0
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        try {
            screening.decayed_permanent_teeth = decayedPermanentTeeth.toInt()
        } catch (e: java.lang.NumberFormatException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            screening.decayed_permanent_teeth = 0
        }
        screening.cavity_permanent_posterior_teeth = cavityPermanentTooth
        screening.cavity_permanent_anterior_teeth = cavityPermanentAnterior
        screening.active_infection = activeInfection
        screening.need_art_filling = needARTFilling
        screening.need_sealant = needSealant
        screening.need_sdf = needSDF
        screening.reversible_pulpitis = reversiblePulpitis
        screening.need_extraction = needExtraction
        screening.updated = true

//        screeningBox.put(screening)
    }

    override fun updateTreatment(
        notes: String,
        sdfWholeMouth: Boolean,
        fvApplied: Boolean,
        treatmentPlanComplete: Boolean,
        teeth: Array<String>
    ) {
//        treatment = treatmentBox.query().equal(
//            Treatment_.encounterId,
//            encounter.id
//        ).orderDesc(Treatment_.id).build().findFirst()!!

        treatment.sdf_whole_mouth = sdfWholeMouth
        treatment.fv_applied = fvApplied
        treatment.notes = notes
        treatment.treatment_plan_complete = treatmentPlanComplete

        treatment.tooth18 = teeth[0]
        treatment.tooth17 = teeth[1]
        treatment.tooth16 = teeth[2]
        treatment.tooth15 = teeth[3]
        treatment.tooth14 = teeth[4]
        treatment.tooth13 = teeth[5]
        treatment.tooth12 = teeth[6]
        treatment.tooth11 = teeth[7]

        treatment.tooth21 = teeth[8]
        treatment.tooth22 = teeth[9]
        treatment.tooth23 = teeth[10]
        treatment.tooth24 = teeth[11]
        treatment.tooth25 = teeth[12]
        treatment.tooth26 = teeth[13]
        treatment.tooth27 = teeth[14]
        treatment.tooth28 = teeth[15]

        treatment.tooth48 = teeth[16]
        treatment.tooth47 = teeth[17]
        treatment.tooth46 = teeth[18]
        treatment.tooth45 = teeth[19]
        treatment.tooth44 = teeth[20]
        treatment.tooth43 = teeth[21]
        treatment.tooth42 = teeth[22]
        treatment.tooth41 = teeth[23]

        treatment.tooth31 = teeth[24]
        treatment.tooth32 = teeth[25]
        treatment.tooth33 = teeth[26]
        treatment.tooth34 = teeth[27]
        treatment.tooth35 = teeth[28]
        treatment.tooth36 = teeth[29]
        treatment.tooth37 = teeth[30]
        treatment.tooth38 = teeth[31]


        //primary teeth
        treatment.tooth51 = teeth[32]
        treatment.tooth52 = teeth[33]
        treatment.tooth53 = teeth[34]
        treatment.tooth54 = teeth[35]
        treatment.tooth55 = teeth[36]

        treatment.tooth61 = teeth[37]
        treatment.tooth62 = teeth[38]
        treatment.tooth63 = teeth[39]
        treatment.tooth64 = teeth[40]
        treatment.tooth65 = teeth[41]

        treatment.tooth81 = teeth[42]
        treatment.tooth82 = teeth[43]
        treatment.tooth83 = teeth[44]
        treatment.tooth84 = teeth[45]
        treatment.tooth85 = teeth[46]

        treatment.tooth71 = teeth[47]
        treatment.tooth72 = teeth[48]
        treatment.tooth73 = teeth[49]
        treatment.tooth74 = teeth[50]
        treatment.tooth75 = teeth[51]

        treatment.updated = true

//        treatmentBox.put(treatment)
    }

    override fun updateReferral(
        noReferral: Boolean,
        healthPost: Boolean,
        hygienist: Boolean,
        dentist: Boolean,
        generalPhysician: Boolean,
        other: Boolean,
        otherDetails: String
    ) {
//        referral =
//            referralBox.query().equal(
//                Referral_.encounterId,
//                encounter.id
//            ).orderDesc(Referral_.id).build().findFirst()!!

        referral.no_referral = noReferral
        referral.health_post = healthPost
        referral.hygienist = hygienist
        referral.dentist = dentist
        referral.general_physician = generalPhysician
        referral.other = other
        referral.other_details = otherDetails
        referral.updated = true

        saveEncounterToLocalDB()
//        referralBox.put(referral)
    }

    override fun updateRecall(
        recallDate: String,
        recallTime: String,
        selectedGeography: String,
        selectedActivity: String
    ) {
    }

    override fun updateRecallDate(recallDate: String, recallTime: String) {
        patient.recall_date = recallDate
        patient.recall_time = recallTime
        patient.recall_geography = DentalApp.geography_id
        patient.updated = true
        patient.updated_at = DateHelper.getTodaysNepaliDate()
        patient.updated_by = DentalApp.readFromPreference(context, Constants.PREF_PROFILE_ID, "")
        patientBox.put(patient)

        val data = Data.Builder().putLong("PATIENT_ID", patient.id)
        val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<UpdatePatientWorker>()
            .setInputData(data.build())
            .setConstraints(DentalApp.uploadConstraints)
            .setInitialDelay(100, TimeUnit.MILLISECONDS).build()

        WorkManager.getInstance(applicationContext).enqueue(uploadPatientWorkRequest)
    }

    private fun saveEncounterToLocalDB() {
        try {
            encounterBox.put(encounter)
            historyBox.put(history)
            screeningBox.put(screening)
            treatmentBox.put(treatment)
            referralBox.put(referral)
            patientBox.put(patient)

            saveEncounterToServer()
        } catch (ex: java.lang.Exception) {
            Toast.makeText(this, "Encounter creating error. Please contact to Abhiyantrik.", Toast.LENGTH_LONG).show()
            finish()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {

//        saveEncounter()

        finish()
        super.onBackPressed()
    }

    override fun goBack() {
        if (pager.currentItem == 0) {
            pager.currentItem = 3
        } else {
            pager.currentItem -= 1
        }
        Timber.d("Current Index ${pager.currentItem}")
        updateIndex(pager.currentItem)

    }

    override fun goForward() {
        if (pager.currentItem == 3) {
            pager.currentItem = 0
            onBackPressed()
        } else {
            pager.currentItem += 1
        }
        Timber.d("Current Index ${pager.currentItem}")
        updateIndex(pager.currentItem)
    }

    override fun getEncounterIdForUpdate(): Long {
        return if (action == "new") {
            0.toLong()
        } else {
            encounter.id
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(context, "Please complete encounter to exit.", Toast.LENGTH_SHORT).show()
//            saveEncounter()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun saveEncounterToServer() {

        val data = Data.Builder().putLong("ENCOUNTER_ID", encounter.id)
            .putLong("PATIENT_ID", patient.id)

        val uploadHistoryWorkerRequest =
            OneTimeWorkRequestBuilder<UploadHistoryWorker>()
                .setInputData(data.build())
                .setConstraints(DentalApp.uploadConstraints)
                .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
        val uploadScreeningWorkerRequest =
            OneTimeWorkRequestBuilder<UploadScreeningWorker>()
                .setInputData(data.build())
                .setConstraints(DentalApp.uploadConstraints)
                .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
        val uploadTreatmentWorkerRequest =
            OneTimeWorkRequestBuilder<UploadTreatmentWorker>()
                .setInputData(data.build())
                .setConstraints(DentalApp.uploadConstraints)
                .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
        val uploadReferralWorkerRequest =
            OneTimeWorkRequestBuilder<UploadReferralWorker>()
                .setInputData(data.build())
                .setConstraints(DentalApp.uploadConstraints)
                .setInitialDelay(100, TimeUnit.MILLISECONDS).build()

        when (action) {
            "new" -> {

                val uploadIndividualEncounterWorkerRequest =
                    OneTimeWorkRequestBuilder<UploadIndividualEncounterWorker>()
                        .setInputData(data.build())
                        .setConstraints(DentalApp.uploadConstraints)
                        .setInitialDelay(
                            100,
                            TimeUnit.MILLISECONDS
                        ).build()


                WorkManager.getInstance(applicationContext).beginWith(
                    uploadIndividualEncounterWorkerRequest
                )
                    .then(
                        listOf(
                            uploadHistoryWorkerRequest,
                            uploadScreeningWorkerRequest,
                            uploadTreatmentWorkerRequest,
                            uploadReferralWorkerRequest
                        )
                    )
                    .enqueue()
            }
            "edit" -> {
                val updateIndividualEncounterWorkerRequest =
                    OneTimeWorkRequestBuilder<UpdateIndividualEncounterWorker>()
                        .setInputData(data.build())
                        .setConstraints(DentalApp.uploadConstraints)
                        .setInitialDelay(
                            100,
                            TimeUnit.MILLISECONDS
                        ).build()

                WorkManager.getInstance(applicationContext).beginWith(
                    updateIndividualEncounterWorkerRequest
                )
                    .then(
                        listOf(
                            uploadHistoryWorkerRequest,
                            uploadScreeningWorkerRequest,
                            uploadTreatmentWorkerRequest,
                            uploadReferralWorkerRequest
                        )
                    )
                    .enqueue()

                Timber.d("FlagTest %s", "Edit found.")
                if (encounterFlagId != 0.toLong()) {
                    Timber.d("FlagTest %s", "Flag Id found as ${encounterFlagId}.")
                    try {
                        GlobalScope.launch(Dispatchers.IO) {
                            val token = DentalApp.readFromPreference(
                                context,
                                Constants.PREF_AUTH_TOKEN,
                                ""
                            )
                            val panelService = DjangoInterface.create(context)
                            val call = panelService.changeFlagToModified(
                                "JWT $token",
                                encounterFlagId,
                                "modified"
                            )
                            val response = call.execute()
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    Timber.d("FlagTest %s", "Encounter modified successfully.")
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@AddEncounterActivity,
                                            "Encounter modified successfully.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Timber.d("FlagTest %s", "Encounter modify failed.")
                                    Toast.makeText(
                                        context,
                                        "Encounter modify failed.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().recordException(e)
                        Toast.makeText(
                            context,
                            "IMPORTANT: Encounter modify failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    companion object{
         val TAG: String = AddEncounterActivity::class.java.simpleName
    }
}

package com.abhiyantrik.dentalhub.ui.asyncronus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_async.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class AsyncActivity : AppCompatActivity() {

    private val TAG = "AsyncActivity"

    private var patientUploading = false
    private var encounterUploading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupOnClickListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun setupOnClickListeners() {
        btnUpload.setOnClickListener {
            if (patientUploading || patientUploading) {
                Toast.makeText(this, "Uploading in progress", Toast.LENGTH_SHORT).show()
            } else {
                tvStatus.text = ""
                uploadPatientData()
            }
        }

        btnUploadEncounter.setOnClickListener {
            if (patientUploading || patientUploading) {
                Toast.makeText(this, "Uploading in progress", Toast.LENGTH_SHORT).show()
            } else {
                tvStatus.text = ""
                uploadEncounterData()
            }
        }

        btnDownload.setOnClickListener {
            //ToDo Download patient data
        }
    }

    private fun uploadEncounterData() {
        GlobalScope.launch(Dispatchers.IO) {
            encounterUploading = true
            val encountersBox: Box<Encounter> =  ObjectBox.boxStore.boxFor(Encounter::class.java)
            val historyBox = ObjectBox.boxStore.boxFor(History::class.java)
            val screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
            val treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
            val referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)

            val allEncounter = encountersBox.query().equal(Encounter_.uploaded, false).build().find()
            val allHistory = historyBox.query().equal(History_.uploaded, false).build().find()
            val allScreening = screeningBox.query().equal(Screening_.uploaded, false).build().find()
            val allTreatment = treatmentBox.query().equal(Treatment_.uploaded, false).build().find()
            val allReferral = referralBox.query().equal(Referral_.uploaded, false).build().find()

            withContext(Dispatchers.Main) {
                tvStatus.append("Found Total ${allEncounter.count()} encounters to upload.\n")
                tvStatus.append("Found Total ${allHistory.count()} history to upload.\n")
                tvStatus.append("Found Total ${allScreening.count()} screening to upload.\n")
                tvStatus.append("Found Total ${allTreatment.count()} treatment to upload.\n")
                tvStatus.append("Found Total ${allReferral.count()} referral to upload.\n")
            }

            val limit = 50.toLong()
            val offset = 0.toLong()
            val allEncounters = encountersBox.query().equal(Encounter_.uploaded, false).orderDesc(Encounter_.id).build().find(offset, limit)
            Log.d(TAG, "patient upload to 50 count ${allEncounters.count()}")
            for (eachEncounter in allEncounters) {

                val patientRemoteId = eachEncounter.patient!!.target.remote_id
                Log.d(TAG, "patient remote Id ${patientRemoteId}")

                if (patientRemoteId.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        tvStatus.append(eachEncounter.patient!!.target.fullName() + " should be uploaded first to upload encounters.\n")
                    }
                } else {
                    if (!eachEncounter.uploaded && patientRemoteId.isNotEmpty()) {
                        if (uploadEncounter(eachEncounter, patientRemoteId, encountersBox)) {
                            withContext(Dispatchers.Main) {
                                tvStatus.append(eachEncounter.patient!!.target.fullName() + " encounter uploaded.\n")
                            }
                        }
                    }


                    val unUploadedHistory = historyBox.query().equal(History_.uploaded, false).and().equal(History_.encounterId, eachEncounter.id).build().findFirst()
                    val unUploadedScreening = screeningBox.query().equal(Screening_.uploaded, false).and().equal(Screening_.encounterId, eachEncounter.id).build().findFirst()
                    val unUploadedTreatment = treatmentBox.query().equal(Treatment_.uploaded, false).and().equal(Treatment_.encounterId, eachEncounter.id).build().findFirst()
                    val unUploadedReferral = referralBox.query().equal(Referral_.uploaded, false).and().equal(Referral_.encounterId, eachEncounter.id).build().findFirst()

                    if (unUploadedHistory != null && patientRemoteId.isNotEmpty()) {
                        if (uploadHistory(unUploadedHistory, historyBox, eachEncounter.remote_id)) {
                            withContext(Dispatchers.Main) {
                                tvStatus.append(eachEncounter.patient!!.target.fullName() + " History uploaded.\n")
                            }
                        }
                    }

                    if (unUploadedScreening != null && patientRemoteId.isNotEmpty()) {
                        if (uploadScreening(unUploadedScreening, screeningBox, eachEncounter.remote_id)) {
                            withContext(Dispatchers.Main) {
                                tvStatus.append(eachEncounter.patient!!.target.fullName() + " Screening uploaded.\n")
                            }
                        }
                    }

                    if (unUploadedTreatment != null && patientRemoteId.isNotEmpty()) {
                        if (uploadTreatment(unUploadedTreatment, treatmentBox, eachEncounter.remote_id)) {
                            withContext(Dispatchers.Main) {
                                tvStatus.append(eachEncounter.patient!!.target.fullName() + " Treatment uploaded.\n")
                            }
                        }
                    }

                    if (unUploadedReferral != null && patientRemoteId.isNotEmpty()) {
                        if (uploadReferral(unUploadedReferral, referralBox, eachEncounter.remote_id)) {
                            withContext(Dispatchers.Main) {
                                tvStatus.append(eachEncounter.patient!!.target.fullName() + " Referral uploaded.\n")
                            }
                        }
                    }
                }

            }
            encounterUploading = false
        }
    }

    private fun uploadPatientData() {
        GlobalScope.launch(Dispatchers.IO) {
            patientUploading = true

            val patientBox = ObjectBox.boxStore.boxFor(Patient::class.java)

            val limit = 50.toLong()
            val offset = 0.toLong()
            val dbAllPatient = patientBox.query().orderDesc(Patient_.id).build().find(offset, limit)

            val allPatient = patientBox.query().equal(Patient_.uploaded, false).build().find()

            withContext(Dispatchers.Main) {
                tvStatus.append("Found Total ${allPatient.count()} patient to upload.\n")
            }

            Log.d(TAG, "patient upload to 50 count ${allPatient.count()}")

            for (patient in dbAllPatient) {

                if (!patient.uploaded) {
                    if (uploadPatient(patient, patientBox)) {
                        withContext(Dispatchers.Main) {
                            tvStatus.append(patient.fullName() + " uploaded." + "\n")
                        }
                    }
                }
            }
            patientUploading = false
        }
    }

    private fun uploadReferral(
        referral: Referral,
        referralBox: Box<Referral>,
        encounterRemoteId: String): Boolean {
        Log.d(TAG, "Referral uploaded started.")
        var referralStatus = false
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_referral),
            applicationContext.resources.getString(R.string.uploading_referral)
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)

        val call = panelService.addReferral(
            "JWT $token",
            encounterRemoteId,
            referral.id,
            referral.no_referral,
            referral.health_post,
            referral.hygienist,
            referral.dentist,
            referral.general_physician,
            referral.other_details
        )
        val response = call.execute()
        Log.d(TAG, "Referral response ${response.code()} ${response.message()}")
        if (response.isSuccessful) {
            when (response.code()) {
                200, 201 -> {
                    val tempReferral = response.body() as com.abhiyantrik.dentalhub.models.Referral
                    referral.remote_id = tempReferral.id
                    referral.uploaded = true
                    referral.updated = false
                    referralBox.put(referral)
                    referralStatus = true
                    Log.d(TAG, "Referral uploaded successfully.")
                }
            }
        }
        DentalApp.cancelNotification(applicationContext, 1001)
        return referralStatus
    }

    private fun uploadTreatment(
        treatment: Treatment,
        treatmentBox: Box<Treatment>,
        encounterRemoteId: String
    ): Boolean {
        Log.d(TAG, "Treatment uploaded started.")
        var treatmentStatus = false
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_treatment),
            applicationContext.resources.getString(R.string.uploading_treatment)
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.addTreatment(
            "JWT $token",
            encounterRemoteId,
            treatment.id,
            treatment.tooth18,
            treatment.tooth17,
            treatment.tooth16,
            treatment.tooth15,
            treatment.tooth14,
            treatment.tooth13,
            treatment.tooth12,
            treatment.tooth11,

            treatment.tooth21,
            treatment.tooth22,
            treatment.tooth23,
            treatment.tooth24,
            treatment.tooth25,
            treatment.tooth26,
            treatment.tooth27,
            treatment.tooth28,

            treatment.tooth48,
            treatment.tooth47,
            treatment.tooth46,
            treatment.tooth45,
            treatment.tooth44,
            treatment.tooth43,
            treatment.tooth42,
            treatment.tooth41,

            treatment.tooth31,
            treatment.tooth32,
            treatment.tooth33,
            treatment.tooth34,
            treatment.tooth35,
            treatment.tooth36,
            treatment.tooth37,
            treatment.tooth38,

            treatment.tooth55,
            treatment.tooth54,
            treatment.tooth53,
            treatment.tooth52,
            treatment.tooth51,

            treatment.tooth61,
            treatment.tooth62,
            treatment.tooth63,
            treatment.tooth64,
            treatment.tooth65,

            treatment.tooth85,
            treatment.tooth84,
            treatment.tooth83,
            treatment.tooth82,
            treatment.tooth81,

            treatment.tooth71,
            treatment.tooth72,
            treatment.tooth73,
            treatment.tooth74,
            treatment.tooth75,

            treatment.sdf_whole_mouth,
            treatment.fv_applied,
            treatment.treatment_plan_complete,
            treatment.notes
        )
        val response = call.execute()
        Log.d(TAG, "Treatment response ${response.code()} ${response.message()}")
        if (response.isSuccessful) {
            when (response.code()) {
                200, 201 -> {
                    val tempTreatment = response.body() as com.abhiyantrik.dentalhub.models.Treatment
                    treatment.remote_id = tempTreatment.id
                    treatment.uploaded = true
                    treatment.updated = false
                    treatmentBox.put(treatment)
                    treatmentStatus = true
                    Log.d(TAG, "Treatment uploaded successfully.")

                }
            }
        }
        DentalApp.cancelNotification(applicationContext, 1001)
        return treatmentStatus
    }

    private fun uploadScreening(
        screening: Screening,
        screeningBox: Box<Screening>,
        encounterRemoteId: String
    ): Boolean {
        Log.d(TAG, "Screening uploaded started.")

        var screeningStatus = false
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_screening),
            applicationContext.resources.getString(R.string.uploading_screening)
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)

        val call = panelService.addScreening(
            "JWT $token",
            encounterRemoteId,
            screening.carries_risk,
            screening.decayed_primary_teeth,
            screening.decayed_permanent_teeth,
            screening.cavity_permanent_anterior_teeth,
            screening.cavity_permanent_posterior_teeth,
            screening.reversible_pulpitis,
            screening.need_art_filling,
            screening.need_sealant,
            screening.need_sdf,
            screening.need_extraction,
            screening.active_infection
        )
        val response = call.execute()
        Log.d(TAG, "Screening response ${response.code()} ${response.message()}")
        if (response.isSuccessful) {
            when (response.code()) {
                200, 201 -> {
                    val tempScreening = response.body() as com.abhiyantrik.dentalhub.models.Screening
                    screening.remote_id = tempScreening.id
                    screening.uploaded = true
                    screening.updated = false
                    screeningBox.put(screening)
                    screeningStatus = true
                    Log.d(TAG, "Screening uploaded successfully.")
                }
            }
        }
        DentalApp.cancelNotification(applicationContext, 1001)
        return screeningStatus
    }

    private fun uploadHistory(
        history: History,
        historyBox: Box<History>,
        encounterRemoteId: String
    ) : Boolean {
        Log.d(TAG, "History uploaded started.")
        var historyStatus = false
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_history),
            applicationContext.resources.getString(R.string.uploading_history)
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        Log.d("SaveHistoryToServer", "New History Upload.")
        val call = panelService.addHistory(
            "JWT $token",
            encounterRemoteId,
            history.id,
            history.blood_disorder,
            history.diabetes,
            history.liver_problem,
            history.rheumatic_fever,
            history.seizuers_or_epilepsy,
            history.hepatitis_b_or_c,
            history.hiv,
            history.no_allergies,
            history.allergies,
            history.other,
            history.high_blood_pressure,
            history.low_blood_pressure,
            history.thyroid_disorder,
            history.medications,
            history.no_underlying_medical_condition,
            history.not_taking_any_medications
        )
        val response = call.execute()
        Log.d(TAG, "History response ${response.code()} ${response.message()}")
        if (response.isSuccessful) {
            when (response.code()) {
                200, 201 -> {
                    val tempHistory = response.body() as com.abhiyantrik.dentalhub.models.History
                    history.remote_id = tempHistory.id
                    history.uploaded = true
                    history.updated = false
                    historyBox.put(history)
                    historyStatus = true
                    Log.d(TAG, "History uploaded successfully.")
                }
                else -> {
                    Log.d("UploadHistoryWorker", response.message() + response.code())
                }
            }
        } else {
            Log.d("UploadHistoryWorker", response.message() + response.code())
        }
        DentalApp.cancelNotification(applicationContext, 1001)
        return historyStatus
    }

    private fun uploadEncounter(
        eachEncounter: Encounter,
        patientRemoteId: String,
        encountersBox: Box<Encounter>
    ) : Boolean {
        Log.d(TAG, "Encounter uploaded started.")
        var encounterStatus = false
        Log.d("EncounterDateCreated", eachEncounter.id.toString() + " " + eachEncounter.created_at!!.length)
        var correctDate = String()
        if (eachEncounter.created_at.length == 10) {
            Log.d("EncounterDateCreated", eachEncounter.id.toString())
            val currentDate = SimpleDateFormat("yyyy-MM-dd").parse(eachEncounter.created_at)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            correctDate = dateFormat.format(currentDate)
        } else {
            correctDate = eachEncounter.created_at
        }
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.addEncounter(
            "JWT $token",
            patientRemoteId,
            eachEncounter!!.id.toInt(),
            eachEncounter.ward_id,
            eachEncounter.activityarea_id,
            eachEncounter.encounter_type,
            eachEncounter.other_problem,
            eachEncounter.author,
            correctDate,
            eachEncounter.updated_at,
            eachEncounter.updated_by!!
        )
        val response = call.execute()
        Log.d(TAG, "Encounter response ${response.code()} ${response.message()}")
        if (response.isSuccessful) {
            when (response.code()) {
                200, 201 -> {
                    val tempEncounter = response.body() as com.abhiyantrik.dentalhub.models.Encounter
                    eachEncounter.remote_id = tempEncounter.id
                    eachEncounter.uploaded = true
                    encountersBox.put(eachEncounter)
                    encounterStatus = true
                    Log.d(TAG, "Encounter uploaded successfully.")
                }
            }
        }
        return encounterStatus
    }

    private fun uploadPatient(
        patient: Patient,
        patientBox: Box<Patient>) : Boolean {
        Log.d(TAG, "Patient uploaded started.")

        var responseStatus = false
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_patient),
            applicationContext.resources.getString(R.string.uploading_patient)
        )
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        var updater = patient.updated_by
        if (patient.updated_by == null) {
            updater =
                DentalApp.readFromPreference(applicationContext, Constants.PREF_PROFILE_ID, "")
        }
        val call = panelService.addPatient(
            "JWT $token",
            patient.id,
            patient.first_name,
            patient.last_name,
            patient.gender,
            patient.phone,
            patient.middle_name,
            patient.dob,
            patient.education,
            patient.ward,
            patient.municipality,
            patient.district,
            patient.latitude,
            patient.longitude,
            patient.activityarea_id,
            patient.geography_id,
            patient.recall_date!!,
            patient.recall_time!!,
            patient.recall_geography,
            patient.author,
            updater!!,
            patient.created_at,
            patient.updated_at
        )
        if(!patient.uploaded) {
            val response = call.execute()
            Log.d(TAG, "Patient response ${response.code()} ${response.message()}")
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val tempPatient = response.body()

                        if ( tempPatient?.id != null ) {
                            patient.remote_id = tempPatient.id
                            patient.uploaded = true
                            patient.updated = false

                            patientBox.put(patient)
                            responseStatus = true
                            Log.d(TAG, "Patients uploaded successfully.")
                            FirebaseCrashlytics.getInstance().log("UploadPatientWorkAsync: Message From OnCreate")
                        } else {
                            FirebaseCrashlytics.getInstance().log("UploadPatientWorkAsync: Patient uploaded but id not received ${patient.fullName()}.")
                            //Crashlytics.getInstance().crash()
                            FirebaseCrashlytics.getInstance().setCustomKey("patient_uploaded", false)
                        }

                        DentalApp.cancelNotification(applicationContext, 1001)
                    }
                }
                Log.d("UploadPatientWorkAsync", "other than 200, 201 " + response.message().toString())
            } else {
                Log.d("UploadPatientWorkAsync", response.message())
                Log.d("UploadPatientWorkAsync", response.code().toString())
                Log.d("UploadPatientWorkAsync", "Error body " + response.errorBody().toString())
            }
        }
        return responseStatus
    }



}
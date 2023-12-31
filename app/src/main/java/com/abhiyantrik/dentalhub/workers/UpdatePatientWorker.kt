package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import timber.log.Timber
import java.util.concurrent.TimeUnit

class UpdatePatientWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>
    private val ctx: Context = context

    override fun doWork(): Result {
        return try {
            patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

            val patientId = inputData.getLong("PATIENT_ID", 0)
            val dbPatientEntity =
                patientsBox.query().equal(Patient_.id, patientId).equal(Patient_.updated, true)
                    .build().findFirst()
            val responseStatus = savePatientToServer(dbPatientEntity!!)

            if (responseStatus) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Timber.d("Exception: %s", e.printStackTrace().toString())
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure()
        }
    }

    @AddTrace(name = "savePatientToServerFromUpdatePatientWorker", enabled = true /* optional */)
    private fun savePatientToServer(patient: Patient): Boolean {
        var responseStatus = false
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_patient),
            applicationContext.resources.getString(R.string.uploading_patient)
        )
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        if (token.isBlank()) return false

        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.updatePatient(
            "JWT $token",
            patient.remote_id,
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
            patient.updated_by!!,
            patient.created_at,
            patient.updated_at
        )
        val response = call.execute()
        if (response.isSuccessful) {
            when (response.code()) {
                200, 201 -> {
                    val dbPatient =
                        patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()
                    dbPatient!!.updated = false
                    patientsBox.put(dbPatient)


                    val allEncounters =
                        encountersBox.query().equal(Encounter_.patientId, patient.id).build().find()
                    for (eachEncounter in allEncounters) {
                        if (!eachEncounter.uploaded) {
                            val data = Data.Builder().putLong("ENCOUNTER_ID", eachEncounter.id)
                                .putLong("PATIENT_ID", dbPatient.id)
                            val uploadEncounterWorkerRequest =
                                OneTimeWorkRequestBuilder<UploadEncounterWorker>()
                                    .setInputData(data.build())
                                    .setConstraints(DentalApp.uploadConstraints)
                                    .setInitialDelay(
                                        100,
                                        TimeUnit.MILLISECONDS
                                    ).build()
                            WorkManager.getInstance(applicationContext)
                                .enqueue(uploadEncounterWorkerRequest)
                        }
                    }
                    responseStatus = true
                }
                else -> {
                    Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " updatePatient() HTTP Status code "+response.code())
                }
            }
        } else {
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " updatePatient() Failed to update patient.")
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " updatePatient() HTTP Status code " + response.code())
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " updatePatient() " + response.message())
            Timber.d("UpdatePatientWorker: %s", response.message())
            responseStatus = false
        }

        DentalApp.cancelNotification(applicationContext, 1001)
        return responseStatus
    }
}

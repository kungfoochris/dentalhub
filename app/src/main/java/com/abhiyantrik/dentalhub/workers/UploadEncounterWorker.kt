package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import java.util.concurrent.TimeUnit
import com.abhiyantrik.dentalhub.models.Encounter as EncounterModel

class UploadEncounterWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>

    override fun doWork(): Result {

        return try {
            patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

            val patientId = inputData.getLong("PATIENT_ID", 0)
            val encounterId = inputData.getLong("ENCOUNTER_ID", 0)

            Log.d("UploadEncounterWorker", "Upload encounter".plus(patientId).plus(" / ").plus(encounterId))
            val dbPatientEntity =
                patientsBox.query().equal(Patient_.id, patientId).build().findFirst()
            val dbEncounterEntity =
                encountersBox.query().equal(Encounter_.id, encounterId).build().findFirst()


            saveEncounterToServer(
                dbPatientEntity!!.remote_id,
                dbPatientEntity.geography_id,
                dbPatientEntity.activityarea_id,
                dbEncounterEntity
            )
            Result.success()
        } catch (e: Exception) {
            Log.d("UploadEncounterWorkerEx", e.printStackTrace().toString())
            Result.failure()
        }
    }

    private fun saveEncounterToServer(
        remoteId: String,
        geographyId: String,
        activityAreaId: String,
        dbEncounterEntity: Encounter?
    ) {
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.addEncounter(
            "JWT $token",
            remoteId,
            dbEncounterEntity!!.id.toString(),
            geographyId,
            activityAreaId,
            dbEncounterEntity.encounter_type,
            dbEncounterEntity.other_problem,
            dbEncounterEntity.author,
            dbEncounterEntity.created_at,
            dbEncounterEntity.updated_at,
            dbEncounterEntity.updated_by!!
        )
        val response = call.execute()
        if (response.isSuccessful) {
            when (response.code()) {
                200, 201 -> {
                    val tempEncounter = response.body() as EncounterModel
                    val dbEncounter =
                        encountersBox.query().equal(Encounter_.id, dbEncounterEntity.id).build()
                            .findFirst()
                    dbEncounter!!.remote_id = tempEncounter.id
                    dbEncounter.uploaded = true
                    encountersBox.put(dbEncounter)

                    val data = Data.Builder().putLong("ENCOUNTER_ID", dbEncounter.id)
                    val uploadHistoryWorkerRequest =
                        OneTimeWorkRequestBuilder<UploadHistoryWorker>()
                            .setInputData(data.build())
                            .setConstraints(DentalApp.uploadConstraints)
                            .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                    WorkManager.getInstance(applicationContext).enqueue(uploadHistoryWorkerRequest)


                    val uploadScreeningWorkerRequest =
                        OneTimeWorkRequestBuilder<UploadScreeningWorker>()
                            .setInputData(data.build())
                            .setConstraints(DentalApp.uploadConstraints)
                            .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                    WorkManager.getInstance(applicationContext)
                        .enqueue(uploadScreeningWorkerRequest)


                    val uploadTreatmentWorkerRequest =
                        OneTimeWorkRequestBuilder<UploadTreatmentWorker>()
                            .setInputData(data.build())
                            .setConstraints(DentalApp.uploadConstraints)
                            .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                    WorkManager.getInstance(applicationContext)
                        .enqueue(uploadTreatmentWorkerRequest)


                    val uploadReferralWorkerRequest =
                        OneTimeWorkRequestBuilder<UploadReferralWorker>()
                            .setInputData(data.build())
                            .setConstraints(DentalApp.uploadConstraints)
                            .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
                    WorkManager.getInstance(applicationContext).enqueue(uploadReferralWorkerRequest)


                }
            }
        }

    }
}
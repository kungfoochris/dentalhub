package com.abhiyantrik.dentalhub.workers

import android.content.Context
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

class UploadPatientWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>

    override fun doWork(): Result {
        return try {
            patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

            val patientId = inputData.getLong("PATIENT_ID", 0)
            val dbPatientEntity = patientsBox.query().equal(Patient_.id, patientId).build().findFirst()
            savePatientToServer(dbPatientEntity!!)
            return Result.success()

        }catch (e: Exception){
            Result.failure()

        }

    }

    private fun savePatientToServer(patient: Patient) {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            "Syncing...",
            "uploading patient ...",
            "uploading patient ..."
        )
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
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
            patient.author,
            patient.updated_by!!,
            patient.created_at,
            patient.updated_at
        )
        print("Response before")
        val tempPatient = call.execute().body()
        val dbPatient = patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()
        dbPatient!!.remote_id = tempPatient!!.uid
        dbPatient.uploaded = true
        dbPatient.updated = false
        println("Patient uid is ${tempPatient.uid}")
        patientsBox.put(dbPatient)

        DentalApp.cancelNotification(applicationContext, 1001)

        val allEncounters = encountersBox.query().equal(Encounter_.patientId, patient.id).build().find()
        for (eachEncounter in allEncounters) {
            if (!eachEncounter.uploaded) {
                val data = Data.Builder().putLong("ENCOUNTER_ID",eachEncounter.id).putLong("PATIENT_ID",dbPatient.id)
                val uploadEncounterWorkerRequest = OneTimeWorkRequestBuilder<UploadEncounterWorker>()
                    .setInputData(data.build())
                    .setConstraints(DentalApp.uploadConstraints)
                    .setInitialDelay(100,
                    TimeUnit.MILLISECONDS).build()
                WorkManager.getInstance(applicationContext).enqueue(uploadEncounterWorkerRequest)
            }
        }

    }

}
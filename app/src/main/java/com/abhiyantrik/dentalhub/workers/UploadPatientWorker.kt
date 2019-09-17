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
import io.objectbox.Box
import java.util.concurrent.TimeUnit

class UploadPatientWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>

    override fun doWork(): Result {
        return try {
            patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

            val patientId = inputData.getLong("PATIENT_ID", 0)
            val dbPatientEntity =
                patientsBox.query().equal(Patient_.id, patientId).build().findFirst()
            savePatientToServer(dbPatientEntity!!)
            Result.success()

        } catch (e: Exception) {
            Log.d("UploadPatientWorkerEx", e.printStackTrace().toString())
            Result.failure()

        }

    }

    private fun savePatientToServer(patient: Patient) {
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
            patient.author,
            updater!!,
            patient.created_at,
            patient.updated_at
        )
        print("Response before")
        val dbPatient =
            patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()
        if(!patient.uploaded){
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val tempPatient = response.body()

                        dbPatient!!.remote_id = tempPatient!!.id
                        dbPatient.uploaded = true
                        dbPatient.updated = false
                        println("Patient uid is ${tempPatient.id}")
                        patientsBox.put(dbPatient)

                        DentalApp.cancelNotification(applicationContext, 1001)


                    }
                }
            }
        }
        createOutputData(dbPatient!!.id)

//        val allEncounters =
//            encountersBox.query().equal(Encounter_.patientId, patient.id).build().find()
//        Log.d(patient.fullName(), allEncounters.size.toString())
//        for (eachEncounter in allEncounters) {
//            if (!eachEncounter.uploaded) {
//                Thread.sleep(500L)
//                val data = Data.Builder().putLong("ENCOUNTER_ID", eachEncounter.id)
//                    .putLong("PATIENT_ID", dbPatient!!.id)
//                val uploadEncounterWorkerRequest =
//                    OneTimeWorkRequestBuilder<UploadEncounterWorker>()
//                        .setInputData(data.build())
//                        .setConstraints(DentalApp.uploadConstraints)
//                        .setInitialDelay(
//                            100,
//                            TimeUnit.MILLISECONDS
//                        ).build()
//                WorkManager.getInstance(applicationContext)
//                    .enqueue(uploadEncounterWorkerRequest)
//            }
//        }


    }
    private fun createOutputData(patientId: Long):Data{
        return Data.Builder().putLong("PATIENT_ID",patientId).build()
    }

}
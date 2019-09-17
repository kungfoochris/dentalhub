package com.abhiyantrik.dentalhub.workers

import android.content.Context
import androidx.work.*
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Patient
import io.objectbox.Box
import java.util.concurrent.TimeUnit

class UploadWorker (context: Context, params: WorkerParameters) : Worker(context, params) {
    val patientsBox: Box<Patient> = ObjectBox.boxStore.boxFor(Patient::class.java)

    override fun doWork(): Result {
        return try{
            val allPatients = patientsBox.query().build().find()
            for (patient in allPatients) {

                val data = Data.Builder().putLong("PATIENT_ID", patient.id)
                val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<UploadPatientWorker>()
                    .setInputData(data.build())
                    .setConstraints(DentalApp.uploadConstraints)
                    .setInitialDelay(100, TimeUnit.MILLISECONDS).build()

                val uploadEncounterWorkRequest = OneTimeWorkRequestBuilder<UploadEncounterWorker>()
                    .setInputData(data.build())
                    .setConstraints(DentalApp.uploadConstraints)
                    .build()
                WorkManager.getInstance(applicationContext).beginWith(uploadPatientWorkRequest)
                    .then(uploadEncounterWorkRequest).enqueue()
            }
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }

    }
}
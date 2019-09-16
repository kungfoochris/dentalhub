package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.Patient
import com.abhiyantrik.dentalhub.utils.DateHelper
import io.objectbox.Box
import java.lang.Exception
import java.util.concurrent.TimeUnit

class DownloadPatientWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    private lateinit var patientsBox: Box<com.abhiyantrik.dentalhub.entities.Patient>

    override fun doWork(): Result {
        return try{
            patientsBox = ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Patient::class.java)

            downloadPatients()
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

    private fun downloadPatients() {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            "Syncing...",
            "Downloading patients ...",
            "Downloading patients ..."
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.getPatients("JWT $token")
        val allPatients = call.execute().body() as List<Patient>
        for (patient in allPatients){
            val existingPatient =patientsBox.query().equal(
                Patient_.remote_id,
                patient.id
            ).build().findFirst()
            if(existingPatient != null){
                Log.d("SetupActivity", existingPatient.fullName()+" already exists.")
                //tvMessage.text = tvMessage.text.toString() + existingPatient.fullName()+" already exists.\n"
                
                loadEncounterData(existingPatient.remote_id)
                
                DentalApp.displayNotification(
                    applicationContext,
                    1001,
                    "Syncing...",
                    existingPatient.fullName(),
                    "Not downloading, already exists."
                )
            }else{
                val patientEntity = com.abhiyantrik.dentalhub.entities.Patient()
                patientEntity.remote_id = patient.id
                patientEntity.first_name = patient.first_name
                patientEntity.middle_name = patient.middle_name
                patientEntity.last_name = patient.last_name
                patientEntity.gender = patient.gender
                patientEntity.dob = patient.dob
                patientEntity.phone = patient.phone
                patientEntity.education = patient.education
                patientEntity.ward = patient.ward
                patientEntity.municipality = patient.municipality
                patientEntity.district = patient.district
                patientEntity.latitude = patient.latitude
                patientEntity.longitude = patient.longitude
                patientEntity.geography_id = patient.geography
                patientEntity.activityarea_id = patient.activity_area
                patientEntity.uploaded = true
                patientEntity.updated = false
                patientEntity.recall = null
                patientEntity.author = patient.author

                if(patient.created_at==null){
                    patientEntity.created_at = DateHelper.getCurrentNepaliDate()
                }else{
                    patientEntity.created_at = patient.created_at
                }
                if(patient.updated_at==null){
                    patientEntity.updated_at = DateHelper.getCurrentNepaliDate()
                }else{
                    patientEntity.updated_at = patient.updated_at
                }
                patientEntity.updated_by = patient.updated_by

                patientsBox.put(patientEntity)
                DentalApp.displayNotification(
                    applicationContext,
                    1001,
                    "Syncing...",
                    patient.fullName(),
                    "Downloading patient detail"
                )
                loadEncounterData(patient.id)
                //tvMessage.text = tvMessage.text.toString() + patient.fullName()+" downloaded.\n"
            }

        }

        DentalApp.cancelNotification(applicationContext, 1001)
    }

    private fun loadEncounterData(patientId: String) {
        val data = Data.Builder().putString("PATIENT_ID",patientId)
        val downloadEncounterWorkerRequest = OneTimeWorkRequestBuilder<DownloadEncounterWorker>().setInputData(data.build()).setInitialDelay(100,
            TimeUnit.MILLISECONDS).build()
        WorkManager.getInstance(applicationContext).enqueue(downloadEncounterWorkerRequest)
    }
}
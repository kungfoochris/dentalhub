package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box

class UploadPatientWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>
    private val ctx: Context = context

    override fun doWork(): Result {
        return try {
            patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)


            val patientId = inputData.getLong("PATIENT_ID", 0)
            val dbPatientEntity =
                patientsBox.query().equal(Patient_.id, patientId).build().findFirst()
            Log.d("UploadPatientWorker", "Patient detail is ${dbPatientEntity?.fullName()}")
            savePatientToServer(dbPatientEntity!!)
            Result.success()

        } catch (e: Exception) {
            Log.d("UploadPatientWorkerEx", e.printStackTrace().toString())
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure()
        }
    }

    @AddTrace(name = "savePatientToServerFromUploadPatientWorker", enabled = true /* optional */)
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
            patient.recall_date!!,
            patient.recall_time!!,
            patient.recall_geography,
            patient.author,
            updater!!,
            patient.created_at,
            patient.updated_at
        )
        val dbPatient =
            patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()
        if(!patient.uploaded){
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val tempPatient = response.body()

                        if ( tempPatient?.id != null ) {
                            dbPatient!!.remote_id = tempPatient.id
                            dbPatient.uploaded = true
                            dbPatient.updated = false

                            patientsBox.put(dbPatient)
                            FirebaseCrashlytics.getInstance().log("UploadPatientWorker Patient uploaded.")
                        } else {
                            FirebaseCrashlytics.getInstance().log("UploadPatientWorker: Patient uploaded but id not received ${patient.fullName()}.")
                            FirebaseCrashlytics.getInstance().setCustomKey("patient_upload_status", false)
                        }

                        DentalApp.cancelNotification(applicationContext, 1001)
                    }
                    else -> {
                        FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addPatient() HTTP Status code "+response.code())
                    }
                }
                Log.d("UploadPatientWorker", "other than 200, 201 " + response.message().toString())
            } else {
                FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addPatient() Failed to add patient.")
                FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addPatient() "+response.code())
                FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addPatient() "+response.message())
                Log.d("UploadPatientWorker", response.message())
                Log.d("UploadPatientWorker", response.code().toString())
                Log.d("UploadPatientWorker", "Error body " + response.errorBody().toString())
            }
        }
        createOutputData(dbPatient!!.id)
    }

    private fun createOutputData(patientId: Long):Data{
        return Data.Builder().putLong("PATIENT_ID",patientId).build()
    }

}
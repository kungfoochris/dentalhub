package com.abhiyantrik.dentalhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box

class UploadPatientWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    private lateinit var patientsBox: Box<Patient>

    override fun doWork(): Result {
        return try {
            patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
            val patientId = inputData.getLong("ID", 0)
            val dbPatientEntity = patientsBox.query().equal(Patient_.id, patientId).build().findFirst()
            savePatientToServer(dbPatientEntity!!)
            return Result.success()

        }catch (e: Exception){
            Result.failure()

        }

    }

    private fun savePatientToServer(patient: Patient) {

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

        // encounter upload

//        call.enqueue(object : Callback<PatientModel> {
//            override fun onFailure(call: Call<PatientModel>, t: Throwable) {
//                print("Response in patient is fail®")
//                Log.d("onFailure", t.toString())
//            }
//
//            override fun onResponse(call: Call<PatientModel>, response: Response<PatientModel>) {
//                print("Response in patient is ${response.body()} and ${response.code()}")
//                if (null != response.body()) {
//                    when (response.code()) {
//                        200 -> {
//                            val tempPatient = response.body() as PatientModel
//                            val dbPatient =
//                                patientsBox.query().equal(Patient_.id, patient.id).build()
//                                    .findFirst()
//                            dbPatient!!.remote_id = tempPatient.uid
//                            dbPatient.uploaded = true
//                            dbPatient.updated = false
//                            println("Patient uid is ${tempPatient.uid}")
//                            patientsBox.put(dbPatient)
//                            Log.d("savePatientToServer", tempPatient.fullName() + " saved.")
//                            //checkAllEncounter(dbPatient)
//                        }
//                        400 -> {
//                            Log.d("savePatientToServer", "400 bad request")
//                        }
//                        404 -> {
//                            Log.d("savePatientToServer", "404 Page not found")
//                        }
//                        else -> {
//                            Log.d("savePatientToServer", "unhandled request")
//                        }
//                    }
//                } else {
//                    Log.d("savePatientToServer", response.code().toString())
//                    Log.d("savePatientToServer", Gson().toJson(response.body()).toString())
//                    //tvErrorMessage.text = response.message()
//                    Log.d("savePatientToServer", response.message())
//                }
//
//            }
//
//        })
    }

}
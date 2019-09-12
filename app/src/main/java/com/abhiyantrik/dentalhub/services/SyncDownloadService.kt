package com.abhiyantrik.dentalhub.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.Patient
import com.abhiyantrik.dentalhub.utils.DateHelper
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncDownloadService : Service() {

    private val TAG = "SyncDownloadService"
    private lateinit var patientsBox: Box<com.abhiyantrik.dentalhub.entities.Patient>
    var downloadedPatients = mutableListOf<Patient>()

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        loadPatientData()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        patientsBox = ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Patient::class.java)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun loadPatientData() {
        //tvMessage.text = tvMessage.text.toString() + "Loading patients...\n"
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.getPatients("JWT $token")
        call.enqueue(object: Callback<List<Patient>> {
            override fun onFailure(call: Call<List<Patient>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                //tvMessage.text = tvMessage.text.toString() + "Failed to load patients\n"
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<List<Patient>>, response: Response<List<Patient>>) {
                if(null!= response.body()){
                    Log.d("SetupActivity", response.code().toString())
                    when(response.code()){
                        200 -> {
                            val allPatients = response.body() as List<Patient>
                            for (patient in allPatients){
                                val existingPatient =patientsBox.query().equal(Patient_.remote_id,
                                    patient.id
                                ).build().findFirst()
                                if(existingPatient != null){
                                    Log.d("SetupActivity", existingPatient.fullName()+" already exists.")
                                    //tvMessage.text = tvMessage.text.toString() + existingPatient.fullName()+" already exists.\n"
                                    DentalApp.displayNotification(
                                        applicationContext,
                                        1001,
                                        "Syncing...",
                                        existingPatient.fullName(),
                                        "Not downloading, already exists."
                                    )
                                }else{
                                    downloadedPatients.add(patient)
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
                                    //tvMessage.text = tvMessage.text.toString() + patient.fullName()+" downloaded.\n"
                                }

                            }
                        }
                    }
                }else{
                    Log.d(TAG, response.code().toString())
                }
            }

        })

    }

}
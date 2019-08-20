package com.example.dentalhub.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.example.dentalhub.Constants
import com.example.dentalhub.DentalApp
import com.example.dentalhub.ObjectBox
import com.example.dentalhub.broadcastreceivers.NetworkStateReceiver
import com.example.dentalhub.entities.*
import com.example.dentalhub.models.Encounter as EncounterModel
import com.example.dentalhub.models.Patient as PatientModel
import com.example.dentalhub.interfaces.DjangoInterface
import com.google.firebase.perf.metrics.AddTrace
import com.google.gson.Gson
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SyncService : Service(), NetworkStateReceiver.NetworkStateReceiverListener {


    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>

    private lateinit var networkStateReceiver: NetworkStateReceiver
    private lateinit var allPatients: List<Patient>
    private lateinit var allEncounters: List<Encounter>
    private lateinit var encounterHistory: List<History>
    private lateinit var encounterscreening: List<Screening>
    private lateinit var encountertreatment: List<Treatment>
    private lateinit var encounterReferral: List<Referral>


    var totalTasks = 0
    var failedTasks = 0
    var successTasks = 0

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver.addListener(this)
        this.registerReceiver(networkStateReceiver, IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver.removeListener(this)
        this.unregisterReceiver(networkStateReceiver)
    }

    private fun startSync() {
        displayNotification()
    }

    override fun networkAvailable() {
        Log.d("SyncService", "networkAvailable()")
        startSync()
    }

    override fun networkUnavailable() {
        Log.d("SyncService", "networkUnavailable()")
        pauseSync()
    }

    private fun pauseSync() {
        // stop the sync
        DentalApp.cancelNotification(applicationContext, 1001)
    }

    private fun displayNotification() {
        allPatients = patientsBox.query().build().find()
        println("Display notification $allPatients")
        for (patient in allPatients) {
            DentalApp.displayNotification(
                applicationContext,
                1001,
                "Syncing...",
                patient.fullName(),
                "Uploading patient detail"
            )
            println("Patient remote id is : ${patient.remote_id}, : ${patient.first_name}")
            if(!patient.uploaded){
                savePatientToServer(patient)
            }
            val updatedLocalPatient = patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()
            println("Uploaded patient name is ${updatedLocalPatient?.fullName()}")
            val remoteId = updatedLocalPatient!!.remote_id
            allEncounters = encountersBox.query().equal(Encounter_.patientId, patient.id).equal(Encounter_.uploaded, false).build().find()
            for(tempEncounter in allEncounters){
                DentalApp.displayNotification(
                    applicationContext,
                    1001,
                    "Syncing...",
                    patient.fullName(),
                    "Uploading encounter details"
                )
                // TODO: set the patient id here also
                saveEncounterToServer(remoteId, tempEncounter)
                val updatedLocalEncounter = encountersBox.query().equal(Encounter_.id, tempEncounter.id).build().findFirst()
                // TODO: read the encounter again from local db so that you can have remote Id
                val encounterRemoteId = updatedLocalEncounter!!.remote_id
                println("Uploaded remote id of ${updatedLocalPatient?.fullName()} is : $encounterRemoteId")
                val tempHistory = historyBox.query().equal(History_.encounterId, updatedLocalEncounter.id).build().findFirst()

                // TODO: save the history using the remoteId of encoutner
//                DentalApp.displayNotification(
//                    applicationContext,
//                    1001,
//                    "Syncing...",
//                    patient.fullName(),
//                    "Uploading history details"
//                )
                // TODO: save the treatment using the remoteId of encoutner
//                DentalApp.displayNotification(
//                    applicationContext,
//                    1001,
//                    "Syncing...",
//                    patient.fullName(),
//                    "Uploading treatment details"
//                )
                // TODO: save the screening using the remoteId of encoutner
//                DentalApp.displayNotification(
//                    applicationContext,
//                    1001,
//                    "Syncing...",
//                    patient.fullName(),
//                    "Uploading screening details"
//                )
                // TODO: save the referral using the remoteId of encoutner
//                DentalApp.displayNotification(
//                    applicationContext,
//                    1001,
//                    "Syncing...",
//                    patient.fullName(),
//                    "Uploading referral details"
//                )
                // TODO: save the recall using the remoteId of encoutner
            }
        }
        DentalApp.cancelNotification(applicationContext, 1001)

        stopSelf()

//        var i = 0
//        fixedRateTimer("default", false, 0L, 1000) {
//            i += 1
//            DentalApp.displayNotification(
//                applicationContext,
//                1001,
//                "Title",
//                allPatients[i].fullName(),
//                "Long Description"
//            )
//            if (i > allPatients.size) {
//                DentalApp.cancelNotification(applicationContext, 1001)
//                cancel()
//                stopSelf()
//            }
//        }


    }

    @AddTrace(name = "syncService_savePatientToServer", enabled = true /* optional */)
    private fun savePatientToServer(patient: Patient) {
        Log.d("SyncService", "savePatientToServer()")
        Log.d("savePatientToServer", patient.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
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
            patient.geography_id
        )
        call.enqueue(object : Callback<PatientModel> {
            override fun onFailure(call: Call<PatientModel>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<PatientModel>, response: Response<PatientModel>) {
                println("Response is $response")
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val tempPatient = response.body() as PatientModel
                            val dbPatient = patientsBox.query().equal(Patient_.id, patient.id).build().findFirst()
                            dbPatient!!.remote_id = tempPatient.id
                            dbPatient.uploaded = true
                            println("Uploaded patient id : ${tempPatient.id} : remote_id : ${tempPatient.id}, Name is : ${dbPatient.fullName()}")
                            patientsBox.put(dbPatient)
                            Log.d("savePatientToServer", tempPatient.fullName() + " saved.")
                        }
                        400 -> {
                            Log.d("savePatientToServer", "400 bad request")
                        }
                        404 -> {
                            Log.d("savePatientToServer", "404 Page not found")
                        }
                        else -> {
                            Log.d("savePatientToServer", "unhandled request")
                        }
                    }
                } else {
                    println("Else part executed.")
                    Log.d("savePatientToServer", response.code().toString())
                    Log.d("savePatientToServer", Gson().toJson(response.body()).toString())
                    //tvErrorMessage.text = response.message()
                    Log.d("savePatientToServer", response.message())
                }

            }

        })
    }


    @AddTrace(name = "syncService_saveEncounterToServer", enabled = true /* optional */)
    private fun saveEncounterToServer(patientId: Int,tempEncounter: Encounter) {
        Log.d("SyncService", "saveEncounterToServer()")
        Log.d("saveEncounterToServer", tempEncounter.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addEncounter("JWT $token", patientId, tempEncounter.encounter_type)
        call.enqueue(object: Callback<EncounterModel>{
            override fun onFailure(call: Call<EncounterModel>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<EncounterModel>, response: Response<EncounterModel>) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val serverEncounter = response.body() as EncounterModel
                            val dbEncounter = encountersBox.query().equal(Encounter_.id, tempEncounter.id).build().findFirst()
                            dbEncounter!!.remote_id = serverEncounter.id
                            dbEncounter.uploaded = true
                            encountersBox.put(dbEncounter)
                        }
                    }
                }else {
                    Log.d("saveEncounterToServer", response.code().toString())
                    Log.d("saveEncounterToServer", Gson().toJson(response.body()).toString())
                    //tvErrorMessage.text = response.message()
                    Log.d("saveEncounterToServer", response.message())
                }
            }

        })
    }


}
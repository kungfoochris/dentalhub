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
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.entities.Patient
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
    private lateinit var networkStateReceiver: NetworkStateReceiver
    private lateinit var allPatients: List<Patient>
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
        for (patient in allPatients) {
            DentalApp.displayNotification(
                applicationContext,
                1001,
                "Title",
                patient.fullName(),
                "Long Description"
            )
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

    @AddTrace(name = "syncService_saveToServer", enabled = true /* optional */)
    private fun saveToServer(patient: Patient) {
        Log.d("SyncService", "saveToServer()")
        Log.d("saveToServer", patient.toString())
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
            patient.street_address,
            patient.ward,
            patient.city,
            patient.state,
            patient.country,
            patient.latitude,
            patient.longitude
        )
        call.enqueue(object : Callback<Patient> {
            override fun onFailure(call: Call<Patient>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<Patient>, response: Response<Patient>) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val tempPatient = response.body() as Patient
                            Log.d("saveToServer", tempPatient.fullName() + " saved.")
                        }
                        400 -> {
                            Log.d("saveToServer", "400 bad request")
                        }
                        404 -> {
                            Log.d("saveToServer", "404 Page not found")
                        }
                        else -> {
                            Log.d("saveToServer", "unhandled request")
                        }
                    }
                } else {
                    Log.d("saveToServer", response.code().toString())
                    Log.d("saveToServer", Gson().toJson(response.body()).toString())
                    //tvErrorMessage.text = response.message()
                }
            }
        })
    }


}
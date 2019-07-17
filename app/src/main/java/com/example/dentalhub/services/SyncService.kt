package com.example.dentalhub.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.dentalhub.DentalApp
import com.example.dentalhub.ObjectBox
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.entities.Patient
import io.objectbox.Box
import kotlin.concurrent.fixedRateTimer

class SyncService : Service() {


    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

        startSync()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startSync() {
        displayNotification()
    }

    private fun displayNotification() {
//        val mainHandler = Handler(Looper.getMainLooper())
//        mainHandler.post(object : Runnable {
//            override fun run() {
//                DentalApp.displayNotification(1001,"Title","Short Description: "+System.currentTimeMillis().toString(), "Long Description")
//                mainHandler.postDelayed(this, 1000)
//            }
//        })
        var i = 0
        fixedRateTimer("default", false, 0L, 1000) {
            i += 1
            DentalApp.displayNotification(1001, "Title", "Short Description: $i", "Long Description")
            if (i > 10) {
                DentalApp.cancelNotification(1001)
                cancel()
                stopSelf()
            }
        }


    }


}
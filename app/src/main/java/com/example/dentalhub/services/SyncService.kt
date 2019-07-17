package com.example.dentalhub.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.dentalhub.DentalApp
import com.example.dentalhub.ObjectBox
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.entities.Patient
import io.objectbox.Box

class SyncService: Service() {


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

        DentalApp.displayNotification("Title","Short Description", "Long Description")

    }




}
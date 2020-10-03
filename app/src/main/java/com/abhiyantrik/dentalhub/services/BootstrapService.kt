package com.abhiyantrik.dentalhub.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Activity
import io.objectbox.Box

class BootstrapService : Service() {
    private lateinit var activitiesBox: Box<Activity>

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        activitiesBox = ObjectBox.boxStore.boxFor(Activity::class.java)


        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        const val TAG = "BootstrapService"
    }


}
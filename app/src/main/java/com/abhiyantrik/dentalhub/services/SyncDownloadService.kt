package com.abhiyantrik.dentalhub.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.Encounter
import com.abhiyantrik.dentalhub.workers.DownloadPatientWorker
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SyncDownloadService : Service() {


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        DentalApp.downloadSyncRunning = true

        val uploadPatientWorkRequest = OneTimeWorkRequestBuilder<DownloadPatientWorker>()
            .setInitialDelay(100, TimeUnit.MILLISECONDS)
            .setConstraints(DentalApp.downloadConstraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(uploadPatientWorkRequest)

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        DentalApp.downloadSyncRunning = false
    }

}
package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.models.History as HistoryModel
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import java.lang.Exception

class UploadHistoryWorker(context: Context, params: WorkerParameters): Worker(context, params) {

    private lateinit var historyBox: Box<History>
    private lateinit var encountersBox: Box<Encounter>

    override fun doWork(): Result {
        return try {
            historyBox = ObjectBox.boxStore.boxFor(History::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
            val encounterId = inputData.getLong("ENCOUNTER_ID", 0)

            val tempHistory = historyBox.query().equal(History_.encounterId, encounterId).build().findFirst()!!
            val dbEncounterEntity = encountersBox.query().equal(Encounter_.id, encounterId).build().findFirst()
            saveHistoryToServer(dbEncounterEntity!!.remote_id, tempHistory)

            Result.success()
        }catch (e: Exception){
            Log.d("Exception", e.printStackTrace().toString())
            Result.failure()
        }
    }

    private fun saveHistoryToServer(encounterId: String,history: History) {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            "Syncing...",
            "Uploading history ...",
            "Uploading history ..."
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.addHistory(
            "JWT $token",
            encounterId,
            history.id,
            history.blood_disorder,
            history.diabetes,
            history.liver_problem,
            history.rheumatic_fever,
            history.seizuers_or_epilepsy,
            history.hepatitis_b_or_c,
            history.hiv,
            history.other,
            history.no_underlying_medical_condition,
            history.not_taking_any_medications,
            history.medications,
            history.no_allergies,
            history.allergies
        )
        val tempHistory = call.execute().body() as HistoryModel
        val dbHistoryEntity = historyBox.query().equal(History_.encounterId, encounterId).build().findFirst()!!
        dbHistoryEntity.remote_id = tempHistory.id
        historyBox.put(dbHistoryEntity)

        DentalApp.cancelNotification(applicationContext, 1001)
    }
}
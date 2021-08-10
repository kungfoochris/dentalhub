package com.abhiyantrik.dentalhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import timber.log.Timber
import com.abhiyantrik.dentalhub.models.History as HistoryModel

class UploadHistoryWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var historyBox: Box<History>
    private lateinit var encountersBox: Box<Encounter>
    private val ctx: Context = context

    override fun doWork(): Result {
        return try {
            historyBox = ObjectBox.boxStore.boxFor(History::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
            val encounterId = inputData.getLong("ENCOUNTER_ID", 0)

            val tempHistory =
                historyBox.query().equal(History_.encounterId, encounterId).build().findFirst()!!
            val dbEncounterEntity =
                encountersBox.query().equal(Encounter_.id, encounterId).build().findFirst()
            saveHistoryToServer(dbEncounterEntity, tempHistory)

            Result.success()
        } catch (e: Exception) {
            Timber.d("Exception: %s", e.printStackTrace().toString())
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure()
        }
    }

    @AddTrace(name = "saveHistoryToServerFromUploadHistoryWorker", enabled = true /* optional */)
    private fun saveHistoryToServer(encounter: Encounter?, history: History) {
        DentalApp.displayNotification(
            applicationContext,
            1001,
            applicationContext.resources.getString(R.string.sync_ticker),
            applicationContext.resources.getString(R.string.uploading_history),
            applicationContext.resources.getString(R.string.uploading_history)
        )

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        if (token.isBlank()) return

        val panelService = DjangoInterface.create(applicationContext)
        if(!history.uploaded){
            Timber.d("SaveHistoryToServer: %s", "New History Upload.")
            val call = panelService.addHistory(
                "JWT $token",
                encounter!!.remote_id,
                history.id,
                history.blood_disorder,
                history.diabetes,
                history.liver_problem,
                history.rheumatic_fever,
                history.seizuers_or_epilepsy,
                history.hepatitis_b_or_c,
                history.hiv,
                history.no_allergies,
                history.allergies,
                history.other,
                history.high_blood_pressure,
                history.low_blood_pressure,
                history.thyroid_disorder,
                history.medications,
                history.no_underlying_medical_condition,
                history.not_taking_any_medications
            )
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val tempHistory = response.body() as HistoryModel
                        val dbHistoryEntity = historyBox.query().equal(
                            History_.encounterId,
                            encounter.id
                        ).build().findFirst()!!
                        dbHistoryEntity.remote_id = tempHistory.id
                        dbHistoryEntity.uploaded = true
                        dbHistoryEntity.updated = false
                        historyBox.put(dbHistoryEntity)
                    }
                    else -> {
                        Timber.d("UploadHistoryWorker: %s", response.message() + response.code())
                        Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addHistory() HTTP Status code "+response.code())
                    }
                }
            } else {
                Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addHistory() Failed to download patients.")
                Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addHistory() "+response.code())
                Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addHistory() "+response.message())
                Timber.d("UploadHistoryWorker: %s", response.message() + response.code())
            }
        }else if(history.updated){
            Timber.d("SaveHistoryToServer: %s", "Update History.")
            val call = panelService.updateHistory(
                "JWT $token",
                encounter!!.remote_id,
                history.id,
                history.blood_disorder,
                history.diabetes,
                history.liver_problem,
                history.rheumatic_fever,
                history.seizuers_or_epilepsy,
                history.hepatitis_b_or_c,
                history.hiv,
                history.no_allergies,
                history.allergies,
                history.other,
                history.high_blood_pressure,
                history.low_blood_pressure,
                history.thyroid_disorder,
                history.medications,
                history.no_underlying_medical_condition,
                history.not_taking_any_medications
            )
            val response = call.execute()
            if (response.isSuccessful) {
                when (response.code()) {
                    200, 201 -> {
                        val dbHistoryEntity = historyBox.query().equal(
                            History_.encounterId,
                            encounter.id
                        ).build().findFirst()!!
                        dbHistoryEntity.uploaded = true
                        dbHistoryEntity.updated = false
                        historyBox.put(dbHistoryEntity)
                    }
                    else -> {
                        Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " updateHistory() HTTP Status code "+response.code())
                    }
                }
            } else {
                Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " updateHistory() Failed to update history.")
                Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " updateHistory() "+response.code())
                Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " updateHistory() "+response.message())
                Timber.d("UpdateHistoryWorker: %s", response.message() + response.code())
            }
        }
        DentalApp.cancelNotification(applicationContext, 1001)
    }
}
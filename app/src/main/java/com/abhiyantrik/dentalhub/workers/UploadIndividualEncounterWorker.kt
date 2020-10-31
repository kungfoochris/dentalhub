package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class UploadIndividualEncounterWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>
    private val ctx: Context = context

    override fun doWork(): Result {
        return try {
            patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

            val patientId = inputData.getLong("PATIENT_ID", 0)
            val encounterId = inputData.getLong("ENCOUNTER_ID", 0)

            Timber.d("Upload encounter".plus(patientId).plus(" / ").plus(encounterId))
            val dbPatientEntity =
                patientsBox.query().equal(Patient_.id, patientId).build().findFirst()
            val dbEncounterEntity =
                encountersBox.query().equal(Encounter_.id, encounterId).build().findFirst()

            saveEncounterToServer(
                dbPatientEntity!!.remote_id,
                dbEncounterEntity
            )
            Result.success()
        } catch (e: Exception) {
            Timber.d(e.printStackTrace().toString())
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure()
        }
    }

    @AddTrace(name = "saveEncounterToServerFromUploadIndividualEncounterWorker", enabled = true /* optional */)
    private fun saveEncounterToServer(
        remoteId: String,
        dbEncounterEntity: Encounter?
    ) {
        Timber.d("EncounterDateCreated %s", dbEncounterEntity?.id.toString() + " " + dbEncounterEntity?.created_at!!.length)
        val correctDate: String
        correctDate = if (dbEncounterEntity.created_at.length == 10) {
            Timber.d("EncounterDateCreated %s", dbEncounterEntity.id.toString())
            val currentDate = SimpleDateFormat("yyyy-MM-dd").parse(dbEncounterEntity.created_at)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            dateFormat.format(currentDate as Date)
        } else {
            dbEncounterEntity.created_at
        }
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.addEncounter(
            "JWT $token",
            remoteId,
            dbEncounterEntity.id.toInt(),
            dbEncounterEntity.ward_id,
            dbEncounterEntity.activityarea_id,
            dbEncounterEntity.encounter_type,
            dbEncounterEntity.other_problem,
            dbEncounterEntity.author,
            correctDate,
            dbEncounterEntity.updated_at,
            dbEncounterEntity.updated_by!!
        )
        val response = call.execute()
        if (response.isSuccessful) {
            when (response.code()) {
                200, 201 -> {
                    val tempEncounter = response.body() as com.abhiyantrik.dentalhub.models.Encounter
                    val dbEncounter =
                        encountersBox.query().equal(Encounter_.id, dbEncounterEntity.id).build()
                            .findFirst()
                    dbEncounter!!.remote_id = tempEncounter.id
                    dbEncounter.uploaded = true
                    encountersBox.put(dbEncounter)
                }
                else -> {
                    Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addEncounter() HTTP Status code "+response.code())
                }
            }
        } else {
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addEncounter() Failed to add encounter.")
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addEncounter() " + response.code())
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " addEncounter() " + response.message())
            Timber.d("saveEncounterToServer: %s", response.message() + response.code())
        }
    }
    companion object{
        const val TAG = "UploadIndEncounterWork"
    }
}

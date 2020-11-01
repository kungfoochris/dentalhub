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
import com.abhiyantrik.dentalhub.broadcastreceivers.NetworkStateReceiver
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.workers.UploadWorker
import com.google.firebase.perf.metrics.AddTrace
import com.google.gson.Gson
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit
import com.abhiyantrik.dentalhub.models.Encounter as EncounterModel
import com.abhiyantrik.dentalhub.models.History as HistoryModel
import com.abhiyantrik.dentalhub.models.Patient as PatientModel
import com.abhiyantrik.dentalhub.models.Referral as ReferralModel
import com.abhiyantrik.dentalhub.models.Screening as ScreeningModel
import com.abhiyantrik.dentalhub.models.Treatment as TreatmentModel

class SyncService : Service(){


    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>
    private lateinit var recallBox: Box<Recall>

    private lateinit var allPatients: List<Patient>
    private lateinit var allEncounters: List<Encounter>
    var successTasks = 0
    private var totalRetrofitProcessed = 0

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

        historyBox = ObjectBox.boxStore.boxFor(History::class.java)
        screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
        treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)
        recallBox = ObjectBox.boxStore.boxFor(Recall::class.java)

        DentalApp.uploadSyncRunning = true
        val uploadWorkerRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(DentalApp.uploadConstraints)
            .setInitialDelay(100, TimeUnit.MILLISECONDS).build()
        WorkManager.getInstance(applicationContext).enqueue(uploadWorkerRequest)

        return super.onStartCommand(intent, flags, startId)
    }



    override fun onDestroy() {
        DentalApp.cancelNotification(applicationContext, 1001)
        DentalApp.uploadSyncRunning = false
        super.onDestroy()
    }


    private fun displayNotification() {
        // for uploading the data
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
            if (!patient.uploaded /*|| patient.updated*/) {
                Timber.d("Processing patient : ${patient.fullName()}")
                savePatientToServer(patient)
            } else {
                Timber.d("Patient already uploaded. ${patient.fullName()}")
                checkAllEncounter(patient)
            }
        }
        // for downloading the data
        stopSelf()
    }

    private fun saveHistoryToServer(remoteId: String, history: History, encounterId: Long) {
        Timber.d("SyncService %s", "saveHistoryToServer()")
        Timber.d("saveHistoryToServer %s", history.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addHistory(
            "JWT $token",
            remoteId,
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
        call.enqueue(object : Callback<HistoryModel> {
            override fun onFailure(call: Call<HistoryModel>, t: Throwable) {
                Timber.d("History onFailure %s", t.toString())

            }

            override fun onResponse(call: Call<HistoryModel>, response: Response<HistoryModel>) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            Timber.d("Successfully added the history.")
                            val tempScreening =
                                screeningBox.query().equal(
                                    Screening_.encounterId,
                                    encounterId
                                ).build().findFirst()!!
                            saveScreeningToServer(remoteId, tempScreening, encounterId)
                        }
                    }
                }
            }

        })

    }

    @AddTrace(name = "saveScreeningToServerFromSyncService", enabled = true /* optional */)
    private fun saveScreeningToServer(remoteId: String, screening: Screening, encounterId: Long) {
        Timber.d("SyncService %s", "saveScreeningToServer()")
        Timber.d("saveScreeningToServer %s", screening.toString())
        Timber.d("saveScreeningToServer %s", screening.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addScreening(
            "JWT $token",
            remoteId,
            screening.carries_risk,
            screening.decayed_primary_teeth,
            screening.decayed_permanent_teeth,
            screening.cavity_permanent_posterior_teeth,
            screening.cavity_permanent_anterior_teeth,
            screening.need_sealant,
            screening.reversible_pulpitis,
            screening.need_art_filling,
            screening.need_extraction,
            screening.need_sdf,
            screening.active_infection
        )

        call.enqueue(object : Callback<ScreeningModel> {
            override fun onFailure(call: Call<ScreeningModel>, t: Throwable) {
                Timber.d("Screening onFailure %s", t.toString())
            }

            override fun onResponse(
                call: Call<ScreeningModel>,
                response: Response<ScreeningModel>
            ) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            Timber.d("Successfully added the Screening.")
                            Timber.d("saveScreeningToServer %s", "Screening uploaded $remoteId")
                            val tempTreatment =
                                treatmentBox.query().equal(
                                    Treatment_.encounterId,
                                    encounterId
                                ).build().findFirst()!!
                            saveTreatmentToServer(remoteId, tempTreatment, encounterId)
                        }
                    }
                } else {
                    println("Screening error body is ${response.code()} body ${response.body()}.")
                }
            }
        })
    }

    @AddTrace(name = "saveReferralToServerFromSyncService", enabled = true /* optional */)
    private fun saveReferralToServer(remoteId: String, referral: Referral) {
        Timber.d("SyncService %s", "saveReferralToServer()")
        Timber.d("saveReferralToServer %s", referral.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addReferral(
            "JWT $token",
            remoteId,
            referral.id,
            referral.no_referral,
            referral.health_post,
            referral.hygienist,
            referral.dentist,
            referral.general_physician,
            referral.other_details
        )

        totalRetrofitProcessed += 1
        call.enqueue(object : Callback<ReferralModel> {
            override fun onFailure(call: Call<ReferralModel>, t: Throwable) {
                Timber.d("Referral onFailure %s", t.toString())
            }

            override fun onResponse(call: Call<ReferralModel>, response: Response<ReferralModel>) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            Timber.d("Successfully added the Referral.")
                            Timber.d("saveReferralToServer %s", "Referral Uploaded. $remoteId")
                            successTasks += 1
                        }
                    }
                }
            }
        })
    }

    @AddTrace(name = "saveTreatmentToServerFromSyncService", enabled = true /* optional */)
    private fun saveTreatmentToServer(remoteId: String, treatment: Treatment, encounterId: Long) {
        Timber.d("SyncService %s", "saveTreatmentToServer()")
        Timber.d("saveTreatmentToServer %s", treatment.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addTreatment(
            "JWT $token",
            remoteId,
            treatment.id,
            treatment.tooth18,
            treatment.tooth17,
            treatment.tooth16,
            treatment.tooth15,
            treatment.tooth14,
            treatment.tooth13,
            treatment.tooth12,
            treatment.tooth11,

            treatment.tooth21,
            treatment.tooth22,
            treatment.tooth23,
            treatment.tooth24,
            treatment.tooth25,
            treatment.tooth26,
            treatment.tooth27,
            treatment.tooth28,

            treatment.tooth48,
            treatment.tooth47,
            treatment.tooth46,
            treatment.tooth45,
            treatment.tooth44,
            treatment.tooth43,
            treatment.tooth42,
            treatment.tooth41,

            treatment.tooth31,
            treatment.tooth32,
            treatment.tooth33,
            treatment.tooth34,
            treatment.tooth35,
            treatment.tooth36,
            treatment.tooth37,
            treatment.tooth38,

            treatment.tooth55,
            treatment.tooth54,
            treatment.tooth53,
            treatment.tooth52,
            treatment.tooth51,

            treatment.tooth61,
            treatment.tooth62,
            treatment.tooth63,
            treatment.tooth64,
            treatment.tooth65,

            treatment.tooth85,
            treatment.tooth84,
            treatment.tooth83,
            treatment.tooth82,
            treatment.tooth81,

            treatment.tooth71,
            treatment.tooth72,
            treatment.tooth73,
            treatment.tooth74,
            treatment.tooth75,

            treatment.sdf_whole_mouth,
            treatment.fv_applied,
            treatment.treatment_plan_complete,
            treatment.notes
        )
        call.enqueue(object : Callback<TreatmentModel> {
            override fun onFailure(call: Call<TreatmentModel>, t: Throwable) {
                Timber.d("Treatment onFailure %s", t.toString())
            }

            override fun onResponse(
                call: Call<TreatmentModel>,
                response: Response<TreatmentModel>
            ) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            Timber.d("saveTreatmentToServer() %s", response.message())
                            println("Successfully added the Treatment.")
                            val tempReferral =
                                referralBox.query().equal(
                                    Referral_.encounterId,
                                    encounterId
                                ).build().findFirst()!!
                            saveReferralToServer(remoteId, tempReferral)
                        }
                    }
                }
            }
        })
    }

    @AddTrace(name = "savePatientToServerFromSyncService", enabled = true /* optional */)
    private fun savePatientToServer(patient: Patient) {
        Timber.d("SyncService %s", "savePatientToServer()")
        Timber.d("savePatientToServer %s", patient.toString())
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
            patient.area_id,
            patient.geography_id,
            patient.recall_date!!,
            patient.recall_time!!,
            patient.recall_geography,
            patient.author,
            patient.updated_by!!,
            patient.created_at,
            patient.updated_at
        )
        print("Response before")
        call.enqueue(object : Callback<PatientModel> {
            override fun onFailure(call: Call<PatientModel>, t: Throwable) {
                Timber.d("onFailure %s", t.toString())
            }

            override fun onResponse(call: Call<PatientModel>, response: Response<PatientModel>) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val tempPatient = response.body() as PatientModel
                            val dbPatient =
                                patientsBox.query().equal(Patient_.id, patient.id).build()
                                    .findFirst()
                            dbPatient!!.remote_id = tempPatient.id
                            dbPatient.uploaded = true
                            dbPatient.updated = false
                            println("Patient uid is ${tempPatient.id}")
                            patientsBox.put(dbPatient)
                            Timber.d("savePatientToServer %s", tempPatient.fullName() + " saved.")
                            checkAllEncounter(dbPatient)
                        }
                        400 -> {
                            Timber.d("savePatientToServer %s", "400 bad request")
                        }
                        404 -> {
                            Timber.d("savePatientToServer %s", "404 Page not found")
                        }
                        else -> {
                            Timber.d("savePatientToServer %s", "unhandled request")
                        }
                    }
                } else {
                    Timber.d("savePatientToServer %s", response.code().toString())
                    Timber.d("savePatientToServer %s", Gson().toJson(response.body()).toString())
                    //tvErrorMessage.text = response.message()
                    Timber.d("savePatientToServer %s", response.message())
                }

            }

        })
    }

    private fun checkAllEncounter(patient: Patient) {
        allEncounters = encountersBox.query().equal(Encounter_.patientId, patient.id).build().find()
        println("already uploaded patient encounter $allEncounters")
        for (eachEncounter in allEncounters) {
            if (eachEncounter.uploaded) {
                Log.d("SyncService", "Encounter already uploaded ${eachEncounter.remote_id}")
            } else {
                saveEncounterToServer(
                    patient.remote_id,
                    patient.geography_id,
                    patient.activityarea_id,
                    eachEncounter
                )
            }
        }
    }


    @AddTrace(name = "saveEncounterToServerFromSyncService", enabled = true /* optional */)
    private fun saveEncounterToServer(
        patientId: String,
        patientGeography: Int,
        patientActivityId: String,
        tempEncounter: Encounter
    ) {
        Timber.d("SyncService %s", "saveEncounterToServer()")
        Timber.d("saveEncounterToServer %s", tempEncounter.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addEncounter(
            "JWT $token",
            patientId,
            tempEncounter.id.toInt(),
            patientGeography,
            patientActivityId,
            tempEncounter.area_id,
            tempEncounter.encounter_type,
            tempEncounter.other_problem,
            tempEncounter.author,
            tempEncounter.created_at,
            tempEncounter.updated_at,
            tempEncounter.updated_by!!
        )
        call.enqueue(object : Callback<EncounterModel> {
            override fun onFailure(call: Call<EncounterModel>, t: Throwable) {
                Timber.d("onFailure %s", t.toString())
            }

            override fun onResponse(
                call: Call<EncounterModel>,
                response: Response<EncounterModel>
            ) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val serverEncounter = response.body() as EncounterModel
                            val dbEncounter =
                                encountersBox.query().equal(Encounter_.id, tempEncounter.id).build()
                                    .findFirst()
                            dbEncounter!!.remote_id = serverEncounter.id
                            println("Encounter uid is : ${serverEncounter.id}")
                            dbEncounter.uploaded = true
                            encountersBox.put(dbEncounter)
                            saveAllFragmentsToServer(dbEncounter)
                        }
                    }
                } else {
                    Timber.d("saveEncounterToServer %s", response.code().toString())
                    Timber.d("saveEncounterToServer %s", Gson().toJson(response.body()).toString())
                    //tvErrorMessage.text = response.message()
                    Timber.d("saveEncounterToServer %s", response.message())
                }
            }

        })
    }

    private fun saveAllFragmentsToServer(encounter: Encounter) {
        // read the encounter again from local db so that you can have remote Id
        val tempHistory =
            historyBox.query().equal(History_.encounterId, encounter.id).build().findFirst()!!
        println("Till the History Master")
        saveHistoryToServer(encounter.remote_id, tempHistory, encounter.id)
    }


}

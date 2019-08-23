package com.abhiyantrik.dentalhub.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.broadcastreceivers.NetworkStateReceiver
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.google.firebase.perf.metrics.AddTrace
import com.google.gson.Gson
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.abhiyantrik.dentalhub.models.Encounter as EncounterModel
import com.abhiyantrik.dentalhub.models.History as HistoryModel
import com.abhiyantrik.dentalhub.models.Patient as PatientModel
import com.abhiyantrik.dentalhub.models.Referral as ReferralModel
import com.abhiyantrik.dentalhub.models.Screening as ScreeningModel
import com.abhiyantrik.dentalhub.models.Treatment as TreatmentModel

class SyncService : Service(), NetworkStateReceiver.NetworkStateReceiverListener {


    private lateinit var patientsBox: Box<Patient>
    private lateinit var encountersBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>
    private lateinit var recallBox: Box<Recall>

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
    var totalRetrofitProcessed = 0

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

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver.addListener(this)
        this.registerReceiver(
            networkStateReceiver,
            IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION)
        )
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
//        for uploading the data
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
            if (!patient.uploaded) {
                println("Processing patient : ${patient.fullName()}")
                savePatientToServer(patient)
                totalTasks += 1
            } else {
                println("Patient already uploaded. ${patient.fullName()}")
                checkAllEncounter(patient)
            }
        }
        DentalApp.cancelNotification(applicationContext, 1001)

//        while (totalTasks != totalRetrofitProcessed) {
//        }

        if (totalTasks == totalRetrofitProcessed) {
            println("uploading totalTask $totalTasks retrofit $totalRetrofitProcessed")
        } else {
            println("incomplete uploading totalTask $totalTasks retrofit $totalRetrofitProcessed")
        }
//        for downloading the data


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

    private fun saveHistoryToServer(remoteId: String, history: History, encounterId: Long) {
        Log.d("SyncService", "saveHistoryToServer()")
        Log.d("saveHistoryToServer", history.toString())
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
            history.other,
            history.no_underlying_medical_condition,
            history.not_taking_any_medications,
            history.medications,
            history.no_allergies,
            history.allergies
        )
        call.enqueue(object : Callback<HistoryModel> {
            override fun onFailure(call: Call<HistoryModel>, t: Throwable) {
                Log.d("History onFailure", t.toString())

            }

            override fun onResponse(call: Call<HistoryModel>, response: Response<HistoryModel>) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            println("Successfully added the history.")
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

    //    @AddTrace(name = "syncService_saveScreeningToServer()", enabled = true /* optional */)
    private fun saveScreeningToServer(remoteId: String, screening: Screening, encounterId: Long) {
        Log.d("SyncService", "saveScreeningToServer()")
        Log.d("saveScreeningToServer", screening.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addScreening(
            "JWT $token",
            remoteId,
            screening.carries_risk,
            screening.decayed_pimary_teeth,
            screening.decayed_permanent_teeth,
            screening.cavity_permanent_anterior,
            screening.cavity_permanent_tooth,
            screening.reversible_pulpitis,
            screening.need_art_filling,
            screening.need_sealant,
            screening.need_sdf,
            screening.need_extraction,
            screening.active_infection
        )

        call.enqueue(object : Callback<ScreeningModel> {
            override fun onFailure(call: Call<ScreeningModel>, t: Throwable) {
                Log.d("Screening onFailure", t.toString())
            }

            override fun onResponse(
                call: Call<ScreeningModel>,
                response: Response<ScreeningModel>
            ) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            println("Successfully added the Screening.")
                            Log.d("saveScreeningToServer", "Screening uploaded $remoteId")
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


    private fun saveReferralToServer(remoteId: String, referral: Referral, recall: Recall) {
        Log.d("SyncService", "saveReferralToServer()")
        Log.d("saveReferralToServer", referral.toString())
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
            referral.other_details,
            recall.date,
            recall.time
        )

        totalRetrofitProcessed += 1
        call.enqueue(object : Callback<ReferralModel> {
            override fun onFailure(call: Call<ReferralModel>, t: Throwable) {
                Log.d("Referral onFailure", t.toString())
            }

            override fun onResponse(call: Call<ReferralModel>, response: Response<ReferralModel>) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            println("Successfully added the Referral.")
                            Log.d("saveReferralToServer", "Referral Uploaded. $remoteId")
                            successTasks += 1
                        }
                    }
                }
            }
        })
    }


    private fun saveTreatmentToServer(remoteId: String, treatment: Treatment, encounterId: Long) {
        Log.d("SyncService", "saveTreatmentToServer()")
        Log.d("saveTreatmentToServer", treatment.toString())
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
                Log.d("Treatment onFailure", t.toString())
            }

            override fun onResponse(
                call: Call<TreatmentModel>,
                response: Response<TreatmentModel>
            ) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            Log.d("saveTreatmentToServer()", response.message())
                            println("Successfully added the Treatment.")
                            val tempReferral =
                                referralBox.query().equal(
                                    Referral_.encounterId,
                                    encounterId
                                ).build().findFirst()!!
                            val tempRecall =
                                recallBox.query().equal(
                                    Recall_.encounterId,
                                    encounterId
                                ).build().findFirst()!!
                            saveReferralToServer(remoteId, tempReferral, tempRecall)
                        }
                    }
                }
            }
        })
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
        print("Response before")
        call.enqueue(object : Callback<PatientModel> {
            override fun onFailure(call: Call<PatientModel>, t: Throwable) {
                print("Response in patient is fail®")
                Log.d("onFailure", t.toString())
            }

            override fun onResponse(call: Call<PatientModel>, response: Response<PatientModel>) {
                print("Response in patient is ${response.body()} and ${response.code()}")
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val tempPatient = response.body() as PatientModel
                            val dbPatient =
                                patientsBox.query().equal(Patient_.id, patient.id).build()
                                    .findFirst()
                            dbPatient!!.remote_id = tempPatient.uid
                            dbPatient.uploaded = true
                            println("Patient uid is ${tempPatient.uid}")
                            patientsBox.put(dbPatient)
                            Log.d("savePatientToServer", tempPatient.fullName() + " saved.")
                            checkAllEncounter(dbPatient)
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
                    Log.d("savePatientToServer", response.code().toString())
                    Log.d("savePatientToServer", Gson().toJson(response.body()).toString())
                    //tvErrorMessage.text = response.message()
                    Log.d("savePatientToServer", response.message())
                }

            }

        })
    }

    private fun checkAllEncounter(patient: Patient) {
        allEncounters = encountersBox.query().equal(Encounter_.patientId, patient.id).build().find()
        println("already uploaded patient encounter $allEncounters")
        for (eachEncounter in allEncounters) {
//            DentalApp.displayNotification(
//                applicationContext,
//                1001,
//                "Syncing...",
//                patient.fullName(),
//                "Uploading encounter details"
//            )
            if (eachEncounter.uploaded) {
                println("Encounter already uploaded ${eachEncounter.remote_id}")
            } else {
                totalTasks += 1
                println("New encounter found ${eachEncounter.id}")
                saveEncounterToServer(
                    patient.remote_id,
                    patient.geography_id,
                    patient.activityarea_id,
                    eachEncounter
                )
            }
        }
    }


    @AddTrace(name = "syncService_saveEncounterToServer", enabled = true /* optional */)
    private fun saveEncounterToServer(
        patientId: String,
        patientGoegraphy: String,
        patientActivityId: String,
        tempEncounter: Encounter
    ) {
        Log.d("SyncService", "saveEncounterToServer()")
        Log.d("saveEncounterToServer", tempEncounter.toString())
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addEncounter(
            "JWT $token",
            patientId,
            patientGoegraphy,
            patientActivityId,
            tempEncounter.encounter_type
        )
        call.enqueue(object : Callback<EncounterModel> {
            override fun onFailure(call: Call<EncounterModel>, t: Throwable) {
                Log.d("onFailure", t.toString())
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
                            dbEncounter!!.remote_id = serverEncounter.uid
                            println("Encounter uid is : ${serverEncounter.uid}")
                            dbEncounter.uploaded = true
                            encountersBox.put(dbEncounter)
                            saveAllFragmentsToServer(dbEncounter)
                        }
                    }
                } else {
                    Log.d("saveEncounterToServer", response.code().toString())
                    Log.d("saveEncounterToServer", Gson().toJson(response.body()).toString())
                    //tvErrorMessage.text = response.message()
                    Log.d("saveEncounterToServer", response.message())
                }
            }

        })
    }

    private fun saveAllFragmentsToServer(encounter: Encounter) {
//         read the encounter again from local db so that you can have remote Id
        val tempHistory =
            historyBox.query().equal(History_.encounterId, encounter.id).build().findFirst()!!
//        DentalApp.displayNotification(
//            applicationContext,
//            1001,
//            "Syncing...",
//            encounter.encounter_type,
//            "Uploading history, Screening, Treatment details"
//        )
        println("Till the History Master")
        saveHistoryToServer(encounter.remote_id, tempHistory, encounter.id)

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

package com.abhiyantrik.dentalhub.ui.synchronization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.adapters.SynchronizationAdapter
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.models.Sync
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import kotlinx.android.synthetic.main.activity_synchronization.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SynchronizationActivity : AppCompatActivity() {

    private lateinit var adapter: SynchronizationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronization)
        title = getString(R.string.synchronization)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBarSync.visibility = View.VISIBLE

//        val sync1 = Sync("MIlan", "Ghimire", "1231", true)
//        val sync2 = Sync("Ghana", "Chimire", "1231", false)
//        val sync3 = Sync("Prabin", "Mirmire", "1231", false)
//        val sync4 = Sync("paras", "Ghimire", "1231", true)

        val synList = mutableListOf<Sync>() //(sync1, sync2, sync3, sync4)

        adapter = SynchronizationAdapter(synList)
        rvSync.adapter = adapter
        rvSync.layoutManager = LinearLayoutManager(this)
        val divider = RecyclerViewItemSeparator(10)
        rvSync.addItemDecoration(divider)

        populateRecyclerView(synList)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun populateRecyclerView(syncList: MutableList<Sync>) {
        val patientBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        val encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        val historyBox = ObjectBox.boxStore.boxFor(History::class.java)
        val screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
        val treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
        val referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)

        GlobalScope.launch(Dispatchers.IO) {

            val notSyncedPatient = patientBox.query().orderDesc(Patient_.id).build().find()

            notSyncedPatient.forEach { patient ->

                val allUploadedData = mutableListOf<Sync>()
                val allNotUploadedData = mutableListOf<Sync>()

                if (patient.uploaded) {
                    allUploadedData.add(Sync(patient.fullName(), "Patient", patient.created_at.toString(), true))
                } else {
                    allNotUploadedData.add(Sync(patient.fullName(), "Patient", patient.created_at.toString(), false))
                }

                val notSyncedEncounter = encounterBox.query().equal(Encounter_.patientId, patient.id).orderDesc(Encounter_.id).build().find()

                notSyncedEncounter.forEach { encounter ->

                    if (encounter.uploaded) {
                        allUploadedData.add(Sync(patient.fullName(), "Encounter - ${encounter.encounter_type}", encounter.created_at, true))
                    } else {
                        allNotUploadedData.add(Sync(patient.fullName(), "Encounter - ${encounter.encounter_type}", encounter.created_at, false))
                    }

                    val notSyncedHistory = historyBox.query().equal(History_.encounterId, encounter.id).orderDesc(History_.id).build().find()
                    val notSyncedScreening = screeningBox.query().equal(Screening_.encounterId, encounter.id).orderDesc(Screening_.id).build().find()
                    val notSyncedTreatment = treatmentBox.query().equal(Treatment_.encounterId, encounter.id).orderDesc(Treatment_.id).build().find()
                    val notSyncedReferral = referralBox.query().equal(Referral_.encounterId, encounter.id).orderDesc(Referral_.id).build().find()

                    notSyncedHistory.forEach { history ->
                        if (history.uploaded) {
                            allUploadedData.add(Sync(patient.fullName(), "History - ${encounter.encounter_type}", encounter.created_at, true))
                        } else {
                            allNotUploadedData.add(Sync(patient.fullName(), "History - ${encounter.encounter_type}", encounter.created_at, false))
                        }
                    }

                    notSyncedScreening.forEach { screening ->
                        if (screening.uploaded) {
                            allUploadedData.add(Sync(patient.fullName(), "Screening - ${encounter.encounter_type}", encounter.created_at, true))
                        } else {
                            allNotUploadedData.add(Sync(patient.fullName(), "Screening - ${encounter.encounter_type}", encounter.created_at, false))
                        }
                    }

                    notSyncedTreatment.forEach { treatment ->
                        if (treatment.uploaded) {
                            allUploadedData.add(Sync(patient.fullName(), "Treatment - ${encounter.encounter_type}", encounter.created_at, true))
                        } else {
                            allNotUploadedData.add(Sync(patient.fullName(), "Treatment - ${encounter.encounter_type}", encounter.created_at, false))
                        }
                    }

                    notSyncedReferral.forEach { referral ->
                        if (referral.uploaded) {
                            allUploadedData.add(Sync(patient.fullName(), "Referral - ${encounter.encounter_type}", encounter.created_at, true))
                        } else {
                            allNotUploadedData.add(Sync(patient.fullName(), "Referral - ${encounter.encounter_type}", encounter.created_at, false))
                        }
                    }
                } // encounter

                allNotUploadedData.forEach {
                    Log.d("NotUploaded", "${it.patient_name}, ${it.encounter_type}")
                }

                syncList.addAll(allUploadedData)
                syncList.addAll(0, allNotUploadedData)

            }// patient

            withContext(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
                progressBarSync.visibility = View.GONE
            }

        } // coroutineScope
    }
}


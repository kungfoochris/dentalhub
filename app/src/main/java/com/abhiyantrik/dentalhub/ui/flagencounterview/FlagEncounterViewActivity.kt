package com.abhiyantrik.dentalhub.ui.flagencounterview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhiyantrik.dentalhub.*
import com.abhiyantrik.dentalhub.adapters.FlagAdapter
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.FlagEncounter
import com.abhiyantrik.dentalhub.models.FlagModifyDelete
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import kotlinx.android.synthetic.main.activity_flag_encounter_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FlagEncounterViewActivity : AppCompatActivity() {

    val encounterBox: Box<Encounter> = ObjectBox.boxStore.boxFor(Encounter::class.java)
    private lateinit var context: Context

    @AddTrace(name = "onCreateFlagEncounterViewActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flag_encounter_view)

        context = this

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.flag)

        val flagEncounterList = mutableListOf<FlagEncounter>()

        val adapter = FlagAdapter(
            this, flagEncounterList,
            object : FlagAdapter.FlagClickListner {
                override fun onEditButtonClick(flagEncounter: FlagEncounter) {
                    val queryResult = encounterBox.query().equal(Encounter_.remote_id, flagEncounter.encounter_remote_id).build().findFirst()
                    if (queryResult != null) {
                        Toast.makeText(this@FlagEncounterViewActivity, "Encounter remote_id found with patient ID: ${queryResult.patient?.targetId}", Toast.LENGTH_SHORT).show()
                        Timber.d("EncounterAdapter" +"do the edit operation")
                        val patientId = queryResult.patient?.targetId.toString()
                        DentalApp.saveIntToPreference(this@FlagEncounterViewActivity, Constants.PREF_SELECTED_PATIENT, patientId.toInt())
                        val addEncounterActivityIntent = Intent(this@FlagEncounterViewActivity, AddEncounterActivity::class.java)
                        addEncounterActivityIntent.putExtra("ENCOUNTER_ID", queryResult.id)
                        addEncounterActivityIntent.putExtra("PATIENT_ID", queryResult.patient?.targetId)
                        addEncounterActivityIntent.putExtra("MODIFY_DELETE", flagEncounter.id.toLong())
                        this@FlagEncounterViewActivity.startActivity(addEncounterActivityIntent)
                        finish()
                    } else {
                        Toast.makeText(this@FlagEncounterViewActivity, "Encounter not found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        rvFlagEncounter.adapter = adapter
        rvFlagEncounter.layoutManager = LinearLayoutManager(this)
        val divider = RecyclerViewItemSeparator(10)
        rvFlagEncounter.addItemDecoration(divider)

        try {
            GlobalScope.launch(Dispatchers.IO) {
                val token = DentalApp.readFromPreference(this@FlagEncounterViewActivity, Constants.PREF_AUTH_TOKEN, "")
                val panelService = DjangoInterface.create(this@FlagEncounterViewActivity)
                val call = panelService.listFlaggedData("JWT $token")
                val response = call.execute()
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val data = response.body() as List<FlagModifyDelete>
                        Timber.d("Received data are $data")
                        data.forEach { eachFlagData ->
                            Timber.d("FlagData %s", eachFlagData.toString())
                            if (eachFlagData.flag.isNotEmpty()) {
                                if (eachFlagData.flag == "delete") {
                                    flagEncounterList.add(
                                        FlagEncounter(
                                            eachFlagData.id.toString(),
                                            eachFlagData.encounter.id,
                                            eachFlagData.encounter.patient.full_name,
                                            eachFlagData.encounter.encounter_type,
                                            eachFlagData.flag,
                                            eachFlagData.delete_status,
                                            "${eachFlagData.reason_for_deletion}  ${eachFlagData.other_reason_for_deletion}"
                                        )
                                    )
                                } else {
                                    flagEncounterList.add(
                                        FlagEncounter(
                                            eachFlagData.id.toString(),
                                            eachFlagData.encounter.id,
                                            eachFlagData.encounter.patient.full_name,
                                            eachFlagData.encounter.encounter_type,
                                            eachFlagData.flag,
                                            eachFlagData.modify_status,
                                            eachFlagData.reason_for_modification
                                        )
                                    )
                                }
                            } else {
                                if (eachFlagData.modify_status != "") {
                                    flagEncounterList.add(
                                        FlagEncounter(
                                            eachFlagData.id.toString(),
                                            eachFlagData.encounter.id,
                                            eachFlagData.encounter.patient.full_name,
                                            eachFlagData.encounter.encounter_type,
                                            "modify",
                                            eachFlagData.modify_status,
                                            eachFlagData.reason_for_modification
                                        )
                                    )
                                } else {
                                    flagEncounterList.add(
                                        FlagEncounter(
                                            eachFlagData.id.toString(),
                                            eachFlagData.encounter.id,
                                            eachFlagData.encounter.patient.full_name,
                                            eachFlagData.encounter.encounter_type,
                                            "delete",
                                            eachFlagData.delete_status,
                                            eachFlagData.reason_for_modification
                                        )
                                    )
                                }

                            } // eachFlagData.flag == ""
                        } // end of Foreach
                        withContext(Dispatchers.Main) {
                            adapter.notifyDataSetChanged()
                        }
                    }else{
                        FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ " listFlaggedData() HTTP Status code "+response.code())
                    }
                }else{
                    FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ " listFlaggedData() Failed to list flagged data.")
                    FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ " listFlaggedData() HTTP Status code "+response.code())
                    FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ " listFlaggedData() "+response.message())
                }
            }
        } catch (ex : Exception) {
            Timber.d("Error Please try again.")
            Toast.makeText(this, "Error occurred please try again.", Toast.LENGTH_SHORT).show()
            FirebaseCrashlytics.getInstance().log(DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")+ " listFlaggedData() Exception"+ ex.printStackTrace().toString())
        }

        Timber.d("FlagData $flagEncounterList")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    companion object{
        const val TAG = "FlagEncounterViewAct"
    }
}

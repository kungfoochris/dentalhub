package com.abhiyantrik.dentalhub.ui.flagencounterview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.adapters.FlagAdapter
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.FlagEncounter
import com.abhiyantrik.dentalhub.models.FlagModifyDelete
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import kotlinx.android.synthetic.main.activity_flag_encounter_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlagEncounterViewActivity : AppCompatActivity() {

    private val TAG: String = FlagEncounterViewActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flag_encounter_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val flag1 = FlagEncounter("1", "Milan", "Checkup and screening", "modify", "Pending", "This is the testing of the flag")
//        val flag2 = FlagEncounter("2", "Paras Nath Chaudhary", "Relif of pain", "modify", "Expired", "This is the testing of the flag")
//        val flag3 = FlagEncounter("3", "Ghana Shyam", "Checkup and screening", "Delete", "Pending", "This is the testing of the flag")
//        val flag4 = FlagEncounter("4", "Prabin", "Checkup and screening", "modify", "Approved", "This is the testing of the flag")

        val flagEncounterList = mutableListOf<FlagEncounter>()

        val adapter = FlagAdapter(this, flagEncounterList)
        rvFlagEncounter.adapter = adapter
        rvFlagEncounter.layoutManager = LinearLayoutManager(this)
        val divider = RecyclerViewItemSeparator(10)
        rvFlagEncounter.addItemDecoration(divider)

        GlobalScope.launch(Dispatchers.IO) {
            val token = DentalApp.readFromPreference(this@FlagEncounterViewActivity, Constants.PREF_AUTH_TOKEN, "")
            val panelService = DjangoInterface.create(this@FlagEncounterViewActivity)
            val call = panelService.listFlagedData("JWT $token")
            val response = call.execute()
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    val data = response.body() as List<FlagModifyDelete>
                    Log.d(TAG, "Received data are $data")
                    data.forEach { eachFlagData ->
                        if (eachFlagData.flag == "delete") {
                            flagEncounterList.add(
                                FlagEncounter(
                                    eachFlagData.id.toString(),
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
                                    eachFlagData.encounter.patient.full_name,
                                    eachFlagData.encounter.encounter_type,
                                    eachFlagData.flag,
                                    eachFlagData.modify_status,
                                    eachFlagData.reason_for_modification
                                )
                            )
                        }
                    } // end of Foreach
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}

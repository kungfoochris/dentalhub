package com.abhiyantrik.dentalhub.ui.flagencounterview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.adapters.FlagAdapter
import com.abhiyantrik.dentalhub.models.FlagEncounter
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import kotlinx.android.synthetic.main.activity_flag_encounter_view.*

class ActivityFlagEncounterView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flag_encounter_view)

        val flag1 = FlagEncounter("1", "Milan", "Checkup and screening", "modify", "Pending", "This is the testing of the flag")
        val flag2 = FlagEncounter("2", "Paras Nath Chaudhary", "Relif of pain", "modify", "Expired", "This is the testing of the flag")
        val flag3 = FlagEncounter("3", "Ghana Shyam", "Checkup and screening", "Delete", "Pending", "This is the testing of the flag")
        val flag4 = FlagEncounter("4", "Prabin", "Checkup and screening", "modify", "Approved", "This is the testing of the flag")

        val flagEcounterList = mutableListOf<FlagEncounter>(flag1, flag2, flag3, flag4)

        val adapter = FlagAdapter(this, flagEcounterList)
        rvFlagEncounter.adapter = adapter
        rvFlagEncounter.layoutManager = LinearLayoutManager(this)
        val divider = RecyclerViewItemSeparator(10)
        rvFlagEncounter.addItemDecoration(divider)
    }
}

package com.abhiyantrik.dentalhub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.models.FlagEncounter
import kotlinx.android.synthetic.main.single_flaged_encounter.view.*

class FlagAdapter(
    val context: Context,
    val data: List<FlagEncounter>
    ) : RecyclerView.Adapter<FlagAdapter.FlagAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.single_flaged_encounter, parent, false)
        return FlagAdapterViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FlagAdapterViewHolder, position: Int) {
        val flagEncounter: FlagEncounter = data[position]
        holder.itemView.apply {
            tvPatientName.text = flagEncounter.patient_name
            tvEncounterType.text = flagEncounter.encounter_type
            tvFlagType.text = flagEncounter.flag_type
            tvFlagEncounterStatus.text = flagEncounter.flag_status
            tvFlagReasonDescription.text = flagEncounter.description
        }
    }

    inner class FlagAdapterViewHolder(viewHolder: View) : RecyclerView.ViewHolder(viewHolder)
}
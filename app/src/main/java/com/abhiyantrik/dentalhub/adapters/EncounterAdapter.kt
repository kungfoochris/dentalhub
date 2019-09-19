package com.abhiyantrik.dentalhub.adapters

import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.AddEncounterActivity
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.utils.DateHelper
import kotlinx.android.synthetic.main.single_encounter.view.*


class EncounterAdapter(
    var context: Context,
    var patient: Patient,
    private var data: List<Encounter>,
    listener: EncounterClickListener
) :
    RecyclerView.Adapter<EncounterAdapter.EncounterViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var encounterClickListener: EncounterClickListener = listener

    interface EncounterClickListener {
        fun onEncounterClick(encounter: Encounter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EncounterViewHolder {
        val view = inflater.inflate(R.layout.single_encounter, parent, false)
        return EncounterViewHolder(view)
    }

    override fun onBindViewHolder(holder: EncounterViewHolder, position: Int) {
        val encounterItem: Encounter = data[position]
        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true
        holder.itemView.ibEdit.setOnClickListener {
            Log.d("EncounterAdapter", "do the edit operation")
            val addEncounterActivityIntent = Intent(context, AddEncounterActivity::class.java)
            addEncounterActivityIntent.putExtra("ENCOUNTER_ID", encounterItem.id)
            addEncounterActivityIntent.putExtra("PATIENT_ID", patient.id)
            context.startActivity(addEncounterActivityIntent)
        }
        holder.itemView.setOnClickListener {
            Log.d("EncounterAdapter", "itemView clicked")
            encounterClickListener.onEncounterClick(encounterItem)
        }
        holder.bindEncounter(encounterItem)
    }

    override fun getItemCount() = data.size

    inner class EncounterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        private var tvEncounterName: TextView = itemView.findViewById(R.id.tvEncounterName)
        private var tvEncounterDate: TextView = itemView.findViewById(R.id.tvEncounterDate)
        private var ibEdit: ImageButton = itemView.findViewById(R.id.ibEdit)

        fun bindEncounter(encounter: Encounter) {
            val encounterType: String = context.getString(R.string.other_problem)
            if (encounter.encounter_type == encounterType) {
                tvEncounterName.text = encounter.encounter_type + " - " + encounter.other_problem
            } else {
                tvEncounterName.text = encounter.encounter_type
            }
            tvEncounterDate.text = DateHelper.formatNepaliDate(context, encounter.created_at)
            if (encounter.isEditable()) {
                ibEdit.visibility = View.VISIBLE
            } else {
                ibEdit.visibility = View.INVISIBLE
            }
        }
    }
}

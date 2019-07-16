package com.example.dentalhub.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.R
import com.example.dentalhub.entities.Encounter


class EncounterAdapter(var context: Context, private var data: List<Encounter>, listener: EncounterClickListener) :
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
        holder.itemView.setOnClickListener {
            Log.d("EncounterAdapter", "itemView clicked")
            encounterClickListener.onEncounterClick(encounterItem)
        }
        holder.bindEncounter(encounterItem)
    }

    override fun getItemCount() = data.size

    inner class EncounterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        private var tvEncounterName: TextView = itemView.findViewById(R.id.tvEncounterName)
        private var tvEncounterDate: TextView = itemView.findViewById(R.id.tvEncounterDate)

        fun bindEncounter(encounter: Encounter) {
            tvEncounterName.text = encounter.encounter_type
            tvEncounterDate.text = encounter.created_at

        }


    }
}
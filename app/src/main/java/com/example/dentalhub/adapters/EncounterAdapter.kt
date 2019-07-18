package com.example.dentalhub.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.AddEncounterActivity
import com.example.dentalhub.R
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.entities.Patient
import kotlinx.android.synthetic.main.single_encounter.view.*


class EncounterAdapter(var context: Context, var patient: Patient, private var data: List<Encounter>, listener: EncounterClickListener) :
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
            addEncounterActivityIntent.putExtra("ENCOUNTER_ID", 0.toLong())
            addEncounterActivityIntent.putExtra("patient", patient)
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
        private var ibEdit : ImageButton = itemView.findViewById(R.id.ibEdit)

        fun bindEncounter(encounter: Encounter) {
            tvEncounterName.text = encounter.encounter_type
            tvEncounterDate.text = encounter.created_at
            if(encounter.isEditable()){
                ibEdit.visibility = View.VISIBLE
            }else{
                ibEdit.visibility = View.INVISIBLE
            }

        }


    }
}
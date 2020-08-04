package com.abhiyantrik.dentalhub.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.*
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.models.FlagEncounter
import kotlinx.android.synthetic.main.single_flaged_encounter.view.*

class FlagAdapter(
    val context: Context,
    val data: List<FlagEncounter>
    ) : RecyclerView.Adapter<FlagAdapter.FlagAdapterViewHolder>() {

    val encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)

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
            val input = flagEncounter.flag_type
            val capitalizedWord = input.substring(0, 1).toUpperCase() + input.substring(1);
            tvFlagType.text = capitalizedWord
            tvFlagEncounterStatus.text = flagEncounter.flag_status
            tvFlagReasonDescription.text = flagEncounter.description
            ibEdit.visibility = View.INVISIBLE
            when (flagEncounter.flag_status) {
                "approved" -> {
                    ibEdit.visibility = View.VISIBLE
                    tvFlagEncounterStatus.setTextColor(resources.getColor(R.color.flag_approved_green))
                }
                "pending" -> {
                    tvFlagEncounterStatus.setTextColor(resources.getColor(R.color.flag_pending_yellow))
                }
                "deleted" -> {
                    tvFlagEncounterStatus.setTextColor(resources.getColor(R.color.flag_expired_red))
                }
            }

            ibEdit.setOnClickListener {
//                Toast.makeText(context, "Encounter remote_id: ${flagEncounter.remote_id}", Toast.LENGTH_SHORT).show()
                val queryResult = encounterBox.query().equal(Encounter_.remote_id, flagEncounter.encounter_remote_id).build().findFirst()
                if (queryResult != null) {
                    Toast.makeText(context, "Encounter remote_id found with patient ID: ${queryResult.patient?.targetId}", Toast.LENGTH_SHORT).show()
                    Log.d("EncounterAdapter", "do the edit operation")
                    val patientId = queryResult.patient?.targetId.toString()
                    DentalApp.saveIntToPreference(context, Constants.PREF_SELECTED_PATIENT, patientId.toInt())
                    val addEncounterActivityIntent = Intent(context, AddEncounterActivity::class.java)
                    addEncounterActivityIntent.putExtra("ENCOUNTER_ID", queryResult.id)
                    addEncounterActivityIntent.putExtra("PATIENT_ID", queryResult.patient?.targetId)
                    context.startActivity(addEncounterActivityIntent)
                } else {
                    Toast.makeText(context, "Encounter not found.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    inner class FlagAdapterViewHolder(viewHolder: View) : RecyclerView.ViewHolder(viewHolder)
}

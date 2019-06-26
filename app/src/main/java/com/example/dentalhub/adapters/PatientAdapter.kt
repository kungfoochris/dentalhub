package com.example.dentalhub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.R
import com.example.dentalhub.models.Patient

class PatientAdapter(var context: Context, private var data:List<Patient>, var listener: PatientClickListener): RecyclerView.Adapter<PatientAdapter.PatientViewHolder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var patientClickListener: PatientClickListener = listener

    interface PatientClickListener{
        fun onAddEncounterButtonClick(patient: Patient)
        fun onViewPatientDetailClick(patient: Patient)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = inflater.inflate(R.layout.single_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patientItem: Patient = data[position]
        holder.bindPatient(patientItem)
    }

    override fun getItemCount() = data.size

    inner class PatientViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private var tvName: TextView = itemView.findViewById(R.id.tvName)
        private var tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private var btnViewPatientDetail: Button = itemView.findViewById(R.id.btnViewPatientDetail)
        private var btnAddNewEncounter: Button = itemView.findViewById(R.id.btnAddNewEncounter)
        init {

        }
        fun bindPatient(patient: Patient){
            tvName.text = patient.full_name
            tvAddress.text = patient.address
            btnAddNewEncounter.setOnClickListener {
                patientClickListener.onAddEncounterButtonClick(patient)
            }
            btnViewPatientDetail.setOnClickListener {
                patientClickListener.onViewPatientDetailClick(patient)
            }
        }



    }
}
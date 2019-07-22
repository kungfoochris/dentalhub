package com.example.dentalhub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.R
import com.example.dentalhub.entities.Patient

class PatientAdapter(var context: Context, private var data: List<Patient>, var listener: PatientClickListener) :
    RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var patientClickListener: PatientClickListener = listener

    interface PatientClickListener {
        fun onViewPatientDetailClick(patient: Patient)

        fun onCallPatientClick(patient: Patient)
        fun onDelayPatientClick(patient: Patient)
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

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvName: TextView = itemView.findViewById(R.id.tvName)
        private var tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private var tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private var btnViewPatientDetail: Button = itemView.findViewById(R.id.btnViewPatientDetail)
        private var btnCall: Button = itemView.findViewById(R.id.btnCall)
        private var btnDelay: Button = itemView.findViewById(R.id.btnDelay)

        init {

        }

        fun bindPatient(patient: Patient) {
            tvName.text = "${patient.first_name} ${patient.last_name}"
            tvAddress.text = ""
            tvPhone.text = patient.phone
            btnCall.setOnClickListener {
                //patientClickListener.onAddEncounterButtonClick(patient)
                patientClickListener.onCallPatientClick(patient)
            }
            btnDelay.setOnClickListener {
                patientClickListener.onDelayPatientClick(patient)
            }
            btnViewPatientDetail.setOnClickListener {
                patientClickListener.onViewPatientDetailClick(patient)
            }
        }


    }
}
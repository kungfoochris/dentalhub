package com.abhiyantrik.dentalhub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Patient

class PatientAdapter(context: Context, private var data: List<Patient>, listener: PatientClickListener) :
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
        private var btnViewPatientDetail: ImageButton = itemView.findViewById(R.id.btnViewPatientDetail)
        private var btnCall: ImageButton = itemView.findViewById(R.id.btnCall)
        private var btnDelay: ImageButton = itemView.findViewById(R.id.btnDelay)
        private var border: View = itemView.findViewById(R.id.border)
        private var patientInfo: View = itemView.findViewById(R.id.patientInfo)
        private var patientHeader: View = itemView.findViewById(R.id.patientHeader)
        private var patientHeaderTitle: TextView = itemView.findViewById(R.id.patientHeaderTitle)

        init {

        }

        fun bindPatient(patient: Patient) {
            if(patient.content=="patient"){
                tvName.text = patient.fullName()
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
                patientHeader.visibility = View.GONE
                patientInfo.visibility = View.VISIBLE
                border.visibility = View.VISIBLE
            }else{
                patientHeaderTitle.text = patient.first_name
                patientHeader.visibility = View.VISIBLE
                patientInfo.visibility = View.GONE
                border.visibility = View.GONE
            }
        }


    }
}
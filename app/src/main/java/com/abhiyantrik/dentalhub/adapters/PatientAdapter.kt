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
import com.abhiyantrik.dentalhub.utils.DateHelper

class PatientAdapter(context: Context, private var data: List<Patient>, displayDelay: Boolean, listener: PatientClickListener) :
    RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var patientClickListener: PatientClickListener = listener
    private val displayDelay: Boolean = displayDelay
    private val context: Context = context

    interface PatientClickListener {
        fun onViewPatientDetailClick(patient: Patient)
        fun onCallPatientClick(patient: Patient)
        fun onDelayPatientClick(patient: Patient)
        fun onRemovePatientClick(patient: Patient)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = inflater.inflate(R.layout.single_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patientItem: Patient = data[position]
        holder.bindPatient(context, patientItem, displayDelay)
    }

    override fun getItemCount() = data.size

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var tvRecallDate: TextView = itemView.findViewById(R.id.tvRecallDate)
        private var tvName: TextView = itemView.findViewById(R.id.tvName)
        private var tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private var tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private var btnViewPatientDetail: ImageButton = itemView.findViewById(R.id.btnViewPatientDetail)
        private var btnCall: ImageButton = itemView.findViewById(R.id.btnCall)
        private var btnDelay: ImageButton = itemView.findViewById(R.id.btnDelay)
        private var btnDelete: ImageButton = itemView.findViewById(R.id.btnRemove)
        private var border: View = itemView.findViewById(R.id.border)
        private var recallInfo: View = itemView.findViewById(R.id.recallInfo)
        private var patientInfo: View = itemView.findViewById(R.id.patientInfo)
        private var patientHeader: View = itemView.findViewById(R.id.patientHeader)
        private var patientHeaderTitle: TextView = itemView.findViewById(R.id.patientHeaderTitle)


        fun bindPatient(context: Context, patient: Patient, displayDelay: Boolean) {
            if(patient.content=="patient"){
                tvName.text = patient.fullName()
                tvAddress.text = ""
                tvRecallDate.text = DateHelper.formatNepaliDate(context, patient.referall())+ " "+patient.recall_time
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
                btnDelete.setOnClickListener {
                    patientClickListener.onRemovePatientClick(patient)
                }
                if(displayDelay){
                    recallInfo.visibility = View.VISIBLE
                    btnDelay.visibility = View.VISIBLE
                    btnDelete.visibility = View.VISIBLE
                }else{
                    recallInfo.visibility = View.GONE
                    btnDelay.visibility = View.GONE
                    btnDelete.visibility = View.GONE
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
package com.example.dentalhub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.fragments.interfaces.HistoryFormCommunicator

class HistoryFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var historyFormCommunicator: HistoryFormCommunicator

    private lateinit var checkBoxBloodDisorderOrBleedingProblem: CheckBox
    private lateinit var checkBoxDiabetes: CheckBox
    private lateinit var checkBoxLiverProblem: CheckBox
    private lateinit var checkBoxRheumaticFever: CheckBox
    private lateinit var checkBoxSeizuresOrEpilepsy: CheckBox
    private lateinit var checkBoxHepatitisBOrC: CheckBox
    private lateinit var checkBoxHIV: CheckBox
    private lateinit var checkBoxNoUnderlyingMedicalCondition: CheckBox
    private lateinit var checkBoxNotTakingAnyMedications: CheckBox
    private lateinit var checkBoxNoAllergies: CheckBox

    private lateinit var etOther: EditText
    private lateinit var etMedications: EditText
    private lateinit var etAllergies: EditText

    private lateinit var btnNext: Button
    private lateinit var btnBack: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        checkBoxBloodDisorderOrBleedingProblem = view.findViewById(R.id.checkBoxBloodDisorderOrBleedingProblem)
        checkBoxDiabetes = view.findViewById(R.id.checkBoxDiabetes)
        checkBoxLiverProblem = view.findViewById(R.id.checkBoxLiverProblem)
        checkBoxRheumaticFever = view.findViewById(R.id.checkBoxRheumaticFever)
        checkBoxSeizuresOrEpilepsy = view.findViewById(R.id.checkBoxSeizuresOrEpilepsy)
        checkBoxHepatitisBOrC = view.findViewById(R.id.checkBoxHepatitisBOrC)
        checkBoxHIV = view.findViewById(R.id.checkBoxHIV)
        checkBoxNoUnderlyingMedicalCondition = view.findViewById(R.id.checkBoxNoUnderlyingMedicalCondition)
        checkBoxNotTakingAnyMedications = view.findViewById(R.id.checkBoxNotTakingAnyMedications)
        checkBoxNoAllergies = view.findViewById(R.id.checkBoxNoAllergies)

        etOther = view.findViewById(R.id.etOther)
        etMedications = view.findViewById(R.id.etMedications)
        etAllergies = view.findViewById(R.id.etAllergies)

        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        historyFormCommunicator = activity as HistoryFormCommunicator

        setupUI()

        checkBoxNoAllergies.setOnCheckedChangeListener { compoundButton, _ ->
            if (!compoundButton.isChecked) {
                etAllergies.visibility = View.VISIBLE
            } else {
                etAllergies.visibility = View.GONE
            }
        }

        btnNext.setOnClickListener {
            val bloodDisorders = checkBoxBloodDisorderOrBleedingProblem.isChecked
            val diabetes = checkBoxDiabetes.isChecked
            val liverProblem = checkBoxLiverProblem.isChecked
            val rheumaticFever = checkBoxRheumaticFever.isChecked
            val seizuresOrEpilepsy = checkBoxSeizuresOrEpilepsy.isChecked
            val hepatitisBOrC = checkBoxHepatitisBOrC.isChecked
            val hiv = checkBoxHIV.isChecked
            val other = etOther.text.toString()
            val noUnderlyingMedicalCondition = checkBoxNoUnderlyingMedicalCondition.isChecked
            val medications = etMedications.text.toString()
            val notTakingAnyMedications = checkBoxNotTakingAnyMedications.isChecked
            val noAllergies = checkBoxNoAllergies.isChecked
            val allergies = etAllergies.text.toString()

            historyFormCommunicator.updateHistory(
                bloodDisorders, diabetes, liverProblem, rheumaticFever, seizuresOrEpilepsy,
                hepatitisBOrC, hiv, other, noUnderlyingMedicalCondition, medications, notTakingAnyMedications, noAllergies, allergies
            )
            fragmentCommunicator.goForward()
        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }

    }

    private fun setupUI() {

    }


}

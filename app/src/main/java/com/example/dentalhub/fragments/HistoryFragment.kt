package com.example.dentalhub.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dentalhub.DentalApp
import com.example.dentalhub.ObjectBox
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.entities.Encounter_
import com.example.dentalhub.entities.History
import com.example.dentalhub.entities.History_
import com.example.dentalhub.fragments.interfaces.HistoryFormCommunicator
import io.objectbox.Box
import kotlinx.android.synthetic.main.fragment_history.*
import android.text.TextWatcher as TextWatcher

class HistoryFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var historyFormCommunicator: HistoryFormCommunicator

    private lateinit var encounterBox: Box<Encounter>
    private var encounter = Encounter()
    private lateinit var historyBox: Box<History>
    private var history = History()

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
    private lateinit var btnSave: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        historyBox = ObjectBox.boxStore.boxFor(History::class.java)

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

        btnSave = view.findViewById(R.id.btnSave)
        btnNext = view.findViewById(R.id.btnNext)
        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        historyFormCommunicator = activity as HistoryFormCommunicator

        setupUI(activity as Context)

        uncheckNoUnderlyingMedicalCon(checkBoxBloodDisorderOrBleedingProblem)
        uncheckNoUnderlyingMedicalCon(checkBoxDiabetes)
        uncheckNoUnderlyingMedicalCon(checkBoxLiverProblem)
        uncheckNoUnderlyingMedicalCon(checkBoxRheumaticFever)
        uncheckNoUnderlyingMedicalCon(checkBoxSeizuresOrEpilepsy)
        uncheckNoUnderlyingMedicalCon(checkBoxHepatitisBOrC)
        uncheckNoUnderlyingMedicalCon(checkBoxHIV)

        checkBoxNoUnderlyingMedicalCondition.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isChecked) {
                checkBoxBloodDisorderOrBleedingProblem.isChecked = false
                checkBoxDiabetes.isChecked = false
                checkBoxLiverProblem.isChecked = false
                checkBoxRheumaticFever.isChecked = false
                checkBoxSeizuresOrEpilepsy.isChecked = false
                checkBoxHepatitisBOrC.isChecked = false
                checkBoxHIV.isChecked = false
                etOther.setText("")
                etOther.visibility = View.GONE
                tvOther.visibility = View.GONE
            } else {
                etOther.visibility = View.VISIBLE
                tvOther.visibility = View.VISIBLE
            }
        }

        checkBoxNotTakingAnyMedications.setOnCheckedChangeListener { compoundButton, _ ->
            if (!compoundButton.isChecked) {
                etMedications.visibility = View.VISIBLE
                tvMedications.visibility = View.VISIBLE
            } else {
                etMedications.visibility = View.GONE
                tvMedications.visibility = View.GONE
            }
        }

        checkBoxNoAllergies.setOnCheckedChangeListener { compoundButton, _ ->
            if (!compoundButton.isChecked) {
                etAllergies.visibility = View.VISIBLE
                tvAllergies.visibility = View.VISIBLE
            } else {
                etAllergies.visibility = View.GONE
                tvAllergies.visibility = View.GONE
            }
        }

        btnNext.setOnClickListener {
            saveHistoryData()
            fragmentCommunicator.goForward()
        }
        btnSave.setOnClickListener {
            saveHistoryData()
            fragmentCommunicator.goBack()
        }
    }

    private fun saveHistoryData() {

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
            bloodDisorders,
            diabetes,
            liverProblem,
            rheumaticFever,
            seizuresOrEpilepsy,
            hepatitisBOrC,
            hiv,
            other,
            noUnderlyingMedicalCondition,
            medications,
            notTakingAnyMedications,
            noAllergies,
            allergies
        )
    }

    private fun uncheckNoUnderlyingMedicalCon(checkbox: CheckBox) {
        checkbox.setOnCheckedChangeListener { compoundButton, _ ->
            if (checkBoxNoUnderlyingMedicalCondition.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(activity, "Please uncheck the Not underlying medical condition.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI(applicationContext: Context) {
        val encounterId = DentalApp.readFromPreference(applicationContext, "Encounter_ID", "0").toLong()

        if (encounterId != 0.toLong()) {
            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!

            history =
                historyBox.query().equal(
                    History_.encounterId,
                    encounter.id
                ).orderDesc(History_.id).build().findFirst()!!

            if (history.blood_disorder) checkBoxBloodDisorderOrBleedingProblem.isChecked = true
            if (history.diabetes) checkBoxDiabetes.isChecked = true
            if (history.liver_problem) checkBoxLiverProblem.isChecked = true
            if (history.rheumatic_fever) checkBoxRheumaticFever.isChecked = true
            if (history.seizuers_or_epilepsy) checkBoxSeizuresOrEpilepsy.isChecked = true
            if (history.hepatitis_b_or_c) checkBoxHepatitisBOrC.isChecked = true
            if (history.hiv) checkBoxHIV.isChecked = true
            etOther.setText(history.other)
            if (history.no_underlying_medical_condition) checkBoxNoUnderlyingMedicalCondition.isChecked = true
            etMedications.setText(history.medications)
            if (history.not_taking_any_medications) checkBoxNotTakingAnyMedications.isChecked = true

            if (history.not_taking_any_medications) {
                checkBoxNotTakingAnyMedications.isChecked = true
                etMedications.visibility = View.GONE
                tvMedications.visibility = View.GONE
            } else {
                etMedications.setText(history.medications)
            }
            if (history.no_allergies) {
                checkBoxNoAllergies.isChecked = true
                etAllergies.visibility = View.GONE
                tvAllergies.visibility = View.GONE
            } else {
                etAllergies.setText(history.allergies)
            }
        }
        else {
            history = historyBox.query().orderDesc(History_.id).build().findFirst()!!
        }
    }
}

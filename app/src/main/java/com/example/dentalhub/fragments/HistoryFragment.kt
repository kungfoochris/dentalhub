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


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HistoryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HistoryFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator

    private lateinit var checkBoxBloodDisorderOrBleedingProblem: CheckBox
    private lateinit var checkBoxDiabetes: CheckBox
    private lateinit var checkBoxLiverProblem: CheckBox
    private lateinit var checkBoxRheumaticFever: CheckBox
    private lateinit var checkBoxSeizuersOrEpilepsy: CheckBox
    private lateinit var checkBoxHepatitisBOrC: CheckBox
    private lateinit var checkBoxHIV: CheckBox
    private lateinit var checkBoxNoUnderlyingMedicalRecord: CheckBox
    private lateinit var checkBoxNotTakingAnyMedications: CheckBox

    private lateinit var etOther: EditText
    private lateinit var etMedications: EditText
    private lateinit var etAllergies: EditText

    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        checkBoxSeizuersOrEpilepsy = view.findViewById(R.id.checkBoxSeizuersOrEpilepsy)
        checkBoxHepatitisBOrC = view.findViewById(R.id.checkBoxHepatitisBOrC)
        checkBoxHIV = view.findViewById(R.id.checkBoxHIV)
        checkBoxNoUnderlyingMedicalRecord = view.findViewById(R.id.checkBoxNoUnderlyingMedicalRecord)
        checkBoxNotTakingAnyMedications = view.findViewById(R.id.checkBoxNotTakingAnyMedications)

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

        btnNext.setOnClickListener {
            fragmentCommunicator.goForward()
        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }

    }



}

package com.example.dentalhub.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.utils.AdapterHelper

class ScreeningFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator

    private lateinit var spinnerRisk: Spinner
    private lateinit var etDecayedPrimaryTeeth: EditText
    private lateinit var etDecayedPermanentTeeth: EditText

    private lateinit var checkBoxCavityPermanentTooth: CheckBox
    private lateinit var checkBoxCavityPermanentAnterior: CheckBox
    private lateinit var checkBoxActiveInfection: CheckBox
    private lateinit var checkBoxNeedARTFilling: CheckBox
    private lateinit var checkBoxNeedSealant: CheckBox
    private lateinit var checkBoxNeedSDF: CheckBox
    private lateinit var checkBoxNeedExtraction: CheckBox

    private lateinit var btnNext: Button
    private lateinit var btnBack: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_screening, container, false)

        spinnerRisk = view.findViewById(R.id.spinnerRisk)
        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)

        if (container != null) {
            spinnerRisk.adapter = AdapterHelper.createAdapter(container.context, resources.getStringArray(R.array.carries_risk).toList())
        }
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

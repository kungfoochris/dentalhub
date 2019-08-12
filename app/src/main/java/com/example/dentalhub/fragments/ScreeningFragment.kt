package com.example.dentalhub.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.dentalhub.DentalApp
import com.example.dentalhub.ObjectBox
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.entities.*
import com.example.dentalhub.fragments.interfaces.ScreeningFormCommunicator
import com.example.dentalhub.utils.AdapterHelper
import io.objectbox.Box

class ScreeningFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var screeningFormCommunicator: ScreeningFormCommunicator

    private lateinit var encounterBox: Box<Encounter>
    private var encounter = Encounter()
    private lateinit var screeningBox: Box<Screening>
    private var screening = Screening()


    private lateinit var spinnerRisk: Spinner
    private lateinit var spinnerNoOfDecayedPrimaryTeeth: Spinner
    private lateinit var spinnerNoOfDecayedPermanentTeeth: Spinner

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

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)

        spinnerRisk = view.findViewById(R.id.spinnerRisk)
        spinnerNoOfDecayedPrimaryTeeth = view.findViewById(R.id.spinnerNoOfDecayedPrimaryTeeth)
        spinnerNoOfDecayedPermanentTeeth = view.findViewById(R.id.spinnerNoOfDecayedPermanentTeeth)
        checkBoxCavityPermanentTooth = view.findViewById(R.id.checkBoxCavityPermanentTooth)
        checkBoxCavityPermanentAnterior = view.findViewById(R.id.checkBoxCavityPermanentAnterior)
        checkBoxActiveInfection = view.findViewById(R.id.checkBoxActiveInfection)
        checkBoxNeedARTFilling = view.findViewById(R.id.checkBoxNeedARTFilling)
        checkBoxNeedSealant = view.findViewById(R.id.checkBoxNeedSealant)
        checkBoxNeedSDF = view.findViewById(R.id.checkBoxNeedSDF)
        checkBoxNeedExtraction = view.findViewById(R.id.checkBoxNeedExtraction)

        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)

        if (container != null) {
            spinnerRisk.adapter =
                AdapterHelper.createAdapter(container.context, resources.getStringArray(R.array.carries_risk).toList())
            spinnerNoOfDecayedPrimaryTeeth.adapter =
                AdapterHelper.createAdapterWithInts(container.context, (0..20).toList())
            spinnerNoOfDecayedPermanentTeeth.adapter =
                AdapterHelper.createAdapterWithInts(container.context, (0..32).toList())
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        screeningFormCommunicator = activity as ScreeningFormCommunicator

        setupUI(activity as Context)

        btnNext.setOnClickListener {
            val carriesRisk = spinnerRisk.selectedItem.toString()
            val noOfdecayedPrimaryTeeth = spinnerNoOfDecayedPrimaryTeeth.selectedItem.toString()
            val noOfdecayedPermanentTeeth = spinnerNoOfDecayedPermanentTeeth.selectedItem.toString()
            val cavityPermanentTooth = checkBoxCavityPermanentTooth.isChecked
            val cavityPermanentAnterior = checkBoxCavityPermanentAnterior.isChecked
            val activeInfection = checkBoxActiveInfection.isChecked
            val needARTFilling = checkBoxNeedARTFilling.isChecked
            val needSealant = checkBoxNeedSealant.isChecked
            val needSDF = checkBoxNeedSDF.isChecked
            val needExtraction = checkBoxNeedSDF.isChecked

            screeningFormCommunicator.updateScreening(
                carriesRisk, noOfdecayedPrimaryTeeth, noOfdecayedPermanentTeeth,
                cavityPermanentTooth, cavityPermanentAnterior, activeInfection, needARTFilling, needSealant, needSDF,
                needExtraction
            )
            fragmentCommunicator.goForward()
        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }

    }

    private fun setupUI(applicationContext: Context) {

        val encounterId = DentalApp.readFromPreference(applicationContext, "Encounter_ID", "0").toLong()
        println("Encounter Id is in screening $encounterId")

        if (encounterId != 0.toLong()) {

            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!

            screening = screeningBox.query().equal(
                Screening_.encounterId,
                encounter.id
            ).orderDesc(Screening_.id).build().findFirst()!!

            if (!screening.carries_risk.isNullOrEmpty()) {
                val riskValue = resources.getStringArray(R.array.carries_risk).toList()
                val indexofRisk = riskValue.indexOf(screening.carries_risk)
                println("Carrier rist index is $indexofRisk and the value is ${screening.carries_risk}")
                spinnerRisk.setSelection(indexofRisk)
            }
        }
    }

}

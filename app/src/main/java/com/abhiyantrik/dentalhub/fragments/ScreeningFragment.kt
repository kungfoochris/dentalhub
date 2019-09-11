package com.abhiyantrik.dentalhub.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.TreatmentFragmentCommunicator
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Screening
import com.abhiyantrik.dentalhub.entities.Screening_
import com.abhiyantrik.dentalhub.fragments.interfaces.ScreeningFormCommunicator
import com.abhiyantrik.dentalhub.utils.AdapterHelper
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
    private lateinit var checkBoxReversiblePulpitis: CheckBox
    private lateinit var checkBoxNeedARTFilling: CheckBox
    private lateinit var checkBoxNeedSealant: CheckBox
    private lateinit var checkBoxNeedSDF: CheckBox
    private lateinit var checkBoxNeedExtraction: CheckBox
    private lateinit var checkBoxActiveInfection: CheckBox
    private lateinit var checkBoxLowBP: CheckBox
    private lateinit var checkBoxHighBP: CheckBox
    private lateinit var checkBoxThyroidDisorder: CheckBox

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
        checkBoxReversiblePulpitis = view.findViewById(R.id.checkBoxReversiblePulpitis)
        checkBoxNeedARTFilling = view.findViewById(R.id.checkBoxNeedARTFilling)
        checkBoxNeedSealant = view.findViewById(R.id.checkBoxNeedSealant)
        checkBoxNeedSDF = view.findViewById(R.id.checkBoxNeedSDF)
        checkBoxNeedExtraction = view.findViewById(R.id.checkBoxNeedExtraction)
        checkBoxActiveInfection = view.findViewById(R.id.checkBoxActiveInfection)
        checkBoxLowBP = view.findViewById(R.id.checkBoxLowBP)
        checkBoxHighBP = view.findViewById(R.id.checkBoxHighBP)
        checkBoxThyroidDisorder = view.findViewById(R.id.checkBoxThyroidDisorder)

        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)

        if (container != null) {
            spinnerRisk.adapter =
                AdapterHelper.createAdapter(
                    container.context,
                    resources.getStringArray(R.array.carries_risk).toList()
                )
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
            val reversiblePulpitis = checkBoxReversiblePulpitis.isChecked
            val needARTFilling = checkBoxNeedARTFilling.isChecked
            val needSealant = checkBoxNeedSealant.isChecked
            val needSDF = checkBoxNeedSDF.isChecked
            val needExtraction = checkBoxNeedSDF.isChecked
            val activeInfection = checkBoxActiveInfection.isChecked
            val highBP = checkBoxHighBP.isChecked
            val lowBP = checkBoxLowBP.isChecked
            val thyroidDisorder = checkBoxThyroidDisorder.isChecked

            screeningFormCommunicator.updateScreening(
                carriesRisk,
                noOfdecayedPrimaryTeeth,
                noOfdecayedPermanentTeeth,
                cavityPermanentTooth,
                cavityPermanentAnterior,
                reversiblePulpitis,
                needARTFilling,
                needSealant,
                needSDF,
                needExtraction,
                activeInfection,
                highBP,
                lowBP,
                thyroidDisorder
            )
            fragmentCommunicator.goForward()
        }

        checkBoxHighBP.setOnCheckedChangeListener { compoundButton, b ->
            if (b && checkBoxLowBP.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(
                    context,
                    resources.getString(R.string.low_bp_is_checked),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        checkBoxLowBP.setOnCheckedChangeListener { compoundButton, b ->
            if (b && checkBoxHighBP.isChecked) {
                compoundButton.isChecked = false
                Toast.makeText(
                    context,
                    resources.getString(R.string.high_bp_is_checked),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }

    }

    private fun setupUI(applicationContext: Context) {

        val encounterId =
            DentalApp.readFromPreference(applicationContext, "Encounter_ID", "0").toLong()
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

            if (screening.decayed_pimary_teeth != 0) spinnerNoOfDecayedPrimaryTeeth
                .setSelection(screening.decayed_pimary_teeth)
            if (screening.decayed_permanent_teeth != 0) spinnerNoOfDecayedPermanentTeeth
                .setSelection(screening.decayed_permanent_teeth)

            if (screening.cavity_permanent_anterior) checkBoxCavityPermanentAnterior.isChecked =
                true
            if (screening.cavity_permanent_tooth) checkBoxCavityPermanentTooth.isChecked = true
            if (screening.reversible_pulpitis) checkBoxReversiblePulpitis.isChecked = true
            if (screening.need_art_filling) checkBoxNeedARTFilling.isChecked = true
            if (screening.need_sealant) checkBoxNeedSealant.isChecked = true
            if (screening.need_sdf) checkBoxNeedSDF.isChecked = true
            if (screening.need_extraction) checkBoxNeedExtraction.isChecked = true
            if (screening.active_infection) checkBoxActiveInfection.isChecked = true
            if (screening.low_blood_pressure) checkBoxLowBP.isChecked = true
            if (screening.high_blood_pressure) checkBoxHighBP.isChecked = true
            if (screening.thyroid_disorder) checkBoxThyroidDisorder.isChecked = true
        }
    }

}
package com.example.dentalhub.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.fragments.interfaces.TreatmentFormCommunicator
import info.hoang8f.android.segmented.SegmentedGroup

class TreatmentFragment : Fragment(), View.OnClickListener {


    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var treatmentFormCommunicator: TreatmentFormCommunicator

    private lateinit var treatmentSelector: SegmentedGroup
    private lateinit var checkBoxFVApplied: CheckBox
    private lateinit var checkBoxTreatmentPlanComplete: CheckBox
    private lateinit var etNotes: EditText
    private lateinit var btnNext: Button
    private lateinit var btnBack: Button


    private lateinit var btnId18: Button
    private lateinit var btnId17: Button
    private lateinit var btnId16: Button
    private lateinit var btnId15: Button
    private lateinit var btnId14: Button
    private lateinit var btnId13: Button
    private lateinit var btnId12: Button
    private lateinit var btnId11: Button
    private lateinit var btnId21: Button
    private lateinit var btnId22: Button
    private lateinit var btnId23: Button
    private lateinit var btnId24: Button
    private lateinit var btnId25: Button
    private lateinit var btnId26: Button
    private lateinit var btnId27: Button
    private lateinit var btnId28: Button


    private lateinit var btnId48: Button
    private lateinit var btnId47: Button
    private lateinit var btnId46: Button
    private lateinit var btnId45: Button
    private lateinit var btnId44: Button
    private lateinit var btnId43: Button
    private lateinit var btnId42: Button
    private lateinit var btnId41: Button
    private lateinit var btnId31: Button
    private lateinit var btnId32: Button
    private lateinit var btnId33: Button
    private lateinit var btnId34: Button
    private lateinit var btnId35: Button
    private lateinit var btnId36: Button
    private lateinit var btnId37: Button
    private lateinit var btnId38: Button

    private var selectedTreatment = ""
    private var btnBackground: Drawable? = null

    private var teeth = Array(32) { "NONE" }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_treatment, container, false)

        btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_none, null)
        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)
        checkBoxFVApplied = view.findViewById(R.id.checkBoxFVApplied)
        checkBoxTreatmentPlanComplete = view.findViewById(R.id.checkBoxTreatmentPlanComplete)
        etNotes = view.findViewById(R.id.etNotes)

        btnId18 = view.findViewById(R.id.btnId18)
        btnId17 = view.findViewById(R.id.btnId17)
        btnId16 = view.findViewById(R.id.btnId16)
        btnId15 = view.findViewById(R.id.btnId15)
        btnId14 = view.findViewById(R.id.btnId14)
        btnId13 = view.findViewById(R.id.btnId13)
        btnId12 = view.findViewById(R.id.btnId12)
        btnId11 = view.findViewById(R.id.btnId11)

        btnId21 = view.findViewById(R.id.btnId21)
        btnId22 = view.findViewById(R.id.btnId22)
        btnId23 = view.findViewById(R.id.btnId23)
        btnId24 = view.findViewById(R.id.btnId24)
        btnId25 = view.findViewById(R.id.btnId25)
        btnId26 = view.findViewById(R.id.btnId26)
        btnId27 = view.findViewById(R.id.btnId27)
        btnId28 = view.findViewById(R.id.btnId28)

        btnId48 = view.findViewById(R.id.btnId48)
        btnId47 = view.findViewById(R.id.btnId47)
        btnId46 = view.findViewById(R.id.btnId46)
        btnId45 = view.findViewById(R.id.btnId45)
        btnId44 = view.findViewById(R.id.btnId44)
        btnId43 = view.findViewById(R.id.btnId43)
        btnId42 = view.findViewById(R.id.btnId42)
        btnId41 = view.findViewById(R.id.btnId41)

        btnId31 = view.findViewById(R.id.btnId31)
        btnId32 = view.findViewById(R.id.btnId32)
        btnId33 = view.findViewById(R.id.btnId33)
        btnId34 = view.findViewById(R.id.btnId34)
        btnId35 = view.findViewById(R.id.btnId35)
        btnId36 = view.findViewById(R.id.btnId36)
        btnId37 = view.findViewById(R.id.btnId37)
        btnId38 = view.findViewById(R.id.btnId38)

        btnId11.setOnClickListener(this)
        btnId12.setOnClickListener(this)
        btnId13.setOnClickListener(this)
        btnId14.setOnClickListener(this)
        btnId15.setOnClickListener(this)
        btnId16.setOnClickListener(this)
        btnId17.setOnClickListener(this)
        btnId18.setOnClickListener(this)

        btnId21.setOnClickListener(this)
        btnId22.setOnClickListener(this)
        btnId23.setOnClickListener(this)
        btnId24.setOnClickListener(this)
        btnId25.setOnClickListener(this)
        btnId26.setOnClickListener(this)
        btnId27.setOnClickListener(this)
        btnId28.setOnClickListener(this)

        btnId31.setOnClickListener(this)
        btnId32.setOnClickListener(this)
        btnId33.setOnClickListener(this)
        btnId34.setOnClickListener(this)
        btnId35.setOnClickListener(this)
        btnId36.setOnClickListener(this)
        btnId37.setOnClickListener(this)
        btnId38.setOnClickListener(this)

        btnId41.setOnClickListener(this)
        btnId42.setOnClickListener(this)
        btnId43.setOnClickListener(this)
        btnId44.setOnClickListener(this)
        btnId45.setOnClickListener(this)
        btnId46.setOnClickListener(this)
        btnId47.setOnClickListener(this)
        btnId48.setOnClickListener(this)

        treatmentSelector = view.findViewById(R.id.treatmentSelector)
        treatmentSelector.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btnSDF -> {
                    selectedTreatment = "SDF"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_sdf, null)
                }
                R.id.btnSEAL -> {
                    selectedTreatment = "SEAL"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_seal, null)
                }
                R.id.btnART -> {
                    selectedTreatment = "ART"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_art, null)
                }
                R.id.btnEXO -> {
                    selectedTreatment = "EXO"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_exo, null)
                }
                R.id.btnUNTR -> {
                    selectedTreatment = "UNTR"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_untr, null)
                }
                else -> {
                    selectedTreatment = "NONE"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_none, null)
                }
            }

        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        treatmentFormCommunicator = activity as TreatmentFormCommunicator

        val notes = etNotes.text.toString()
        val fvApplied = checkBoxFVApplied.isChecked
        val treatmentPlanComplete = checkBoxTreatmentPlanComplete.isChecked
        treatmentFormCommunicator.updateTreatment(notes, fvApplied, treatmentPlanComplete, teeth)

        btnNext.setOnClickListener {
            fragmentCommunicator.goForward()
        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }

    }

    override fun onClick(v: View) {
        val buttons = arrayOf(
            R.id.btnId11, R.id.btnId12, R.id.btnId13, R.id.btnId14, R.id.btnId15, R.id.btnId16,
            R.id.btnId17, R.id.btnId18, R.id.btnId21, R.id.btnId22, R.id.btnId23, R.id.btnId24, R.id.btnId25,
            R.id.btnId26, R.id.btnId27, R.id.btnId28, R.id.btnId31, R.id.btnId32, R.id.btnId33, R.id.btnId34,
            R.id.btnId35, R.id.btnId36, R.id.btnId37, R.id.btnId38, R.id.btnId41, R.id.btnId42, R.id.btnId43,
            R.id.btnId44, R.id.btnId45, R.id.btnId46, R.id.btnId47, R.id.btnId48
        )
        if (buttons.contains(v.id)) {
            v.background = btnBackground
        }
        when (v.id) {
            R.id.btnId18 -> teeth[0] = selectedTreatment
            R.id.btnId17 -> teeth[1] = selectedTreatment
            R.id.btnId16 -> teeth[2] = selectedTreatment
            R.id.btnId15 -> teeth[3] = selectedTreatment
            R.id.btnId14 -> teeth[4] = selectedTreatment
            R.id.btnId13 -> teeth[5] = selectedTreatment
            R.id.btnId12 -> teeth[6] = selectedTreatment
            R.id.btnId11 -> teeth[7] = selectedTreatment

            R.id.btnId21 -> teeth[8] = selectedTreatment
            R.id.btnId22 -> teeth[9] = selectedTreatment
            R.id.btnId23 -> teeth[10] = selectedTreatment
            R.id.btnId24 -> teeth[11] = selectedTreatment
            R.id.btnId25 -> teeth[12] = selectedTreatment
            R.id.btnId26 -> teeth[13] = selectedTreatment
            R.id.btnId27 -> teeth[14] = selectedTreatment
            R.id.btnId28 -> teeth[15] = selectedTreatment

            R.id.btnId48 -> teeth[16] = selectedTreatment
            R.id.btnId47 -> teeth[17] = selectedTreatment
            R.id.btnId46 -> teeth[18] = selectedTreatment
            R.id.btnId45 -> teeth[19] = selectedTreatment
            R.id.btnId44 -> teeth[20] = selectedTreatment
            R.id.btnId43 -> teeth[21] = selectedTreatment
            R.id.btnId42 -> teeth[22] = selectedTreatment
            R.id.btnId41 -> teeth[23] = selectedTreatment

            R.id.btnId31 -> teeth[24] = selectedTreatment
            R.id.btnId32 -> teeth[25] = selectedTreatment
            R.id.btnId33 -> teeth[26] = selectedTreatment
            R.id.btnId34 -> teeth[27] = selectedTreatment
            R.id.btnId35 -> teeth[28] = selectedTreatment
            R.id.btnId36 -> teeth[29] = selectedTreatment
            R.id.btnId37 -> teeth[30] = selectedTreatment
            R.id.btnId38 -> teeth[31] = selectedTreatment

        }
    }

}

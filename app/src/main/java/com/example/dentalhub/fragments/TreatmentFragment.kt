package com.example.dentalhub.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.fragments.interfaces.TreatmentFormCommunicator
import info.hoang8f.android.segmented.SegmentedGroup

class TreatmentFragment : Fragment(), View.OnClickListener {


    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var treatmentFormCommunicator: TreatmentFormCommunicator

//    private lateinit var treatmentSelector: SegmentedGroup

    // Treatment Type buttons initialization
    private  lateinit var btnSDF: Button
    private  lateinit var btnSEAL: Button
    private  lateinit var btnART: Button
    private  lateinit var btnEXO: Button
    private  lateinit var btnUNTR: Button
    private  lateinit var btnSMART: Button


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

    private lateinit var btnId55: Button
    private lateinit var btnId54: Button
    private lateinit var btnId53: Button
    private lateinit var btnId52: Button
    private lateinit var btnId51: Button
    private lateinit var btnId61: Button
    private lateinit var btnId62: Button
    private lateinit var btnId63: Button
    private lateinit var btnId64: Button
    private lateinit var btnId65: Button

    private lateinit var btnId85: Button
    private lateinit var btnId84: Button
    private lateinit var btnId83: Button
    private lateinit var btnId82: Button
    private lateinit var btnId81: Button
    private lateinit var btnId71: Button
    private lateinit var btnId72: Button
    private lateinit var btnId73: Button
    private lateinit var btnId74: Button
    private lateinit var btnId75: Button

    private var selectedTreatment = ""
    private var defaultTreatment = "NONE"
    private var btnBackground: Drawable? = null
    private var defaultBackground: Drawable? = null
    private var btnDefaultBackground: Drawable? = null

    private var teeth = Array(52) { "NONE" }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_treatment, container, false)

        btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_none, null)
        btnDefaultBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_button_default_color, null)
        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)
        checkBoxFVApplied = view.findViewById(R.id.checkBoxFVApplied)
        checkBoxTreatmentPlanComplete = view.findViewById(R.id.checkBoxTreatmentPlanComplete)
        etNotes = view.findViewById(R.id.etNotes)

        // finding Treatment button from View
        btnSDF = view.findViewById(R.id.btnSDF)
        btnSEAL = view.findViewById(R.id.btnSEAL)
        btnART = view.findViewById(R.id.btnART)
        btnEXO = view.findViewById(R.id.btnEXO)
        btnUNTR = view.findViewById(R.id.btnUNTR)
        btnSMART = view.findViewById(R.id.btnSMART)

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

        // primary teeth
        btnId51 = view.findViewById(R.id.btnId51)
        btnId52 = view.findViewById(R.id.btnId52)
        btnId53 = view.findViewById(R.id.btnId53)
        btnId54 = view.findViewById(R.id.btnId54)
        btnId55 = view.findViewById(R.id.btnId55)

        btnId61 = view.findViewById(R.id.btnId61)
        btnId62 = view.findViewById(R.id.btnId62)
        btnId63 = view.findViewById(R.id.btnId63)
        btnId64 = view.findViewById(R.id.btnId64)
        btnId65 = view.findViewById(R.id.btnId65)

        btnId81 = view.findViewById(R.id.btnId81)
        btnId82 = view.findViewById(R.id.btnId82)
        btnId83 = view.findViewById(R.id.btnId83)
        btnId84 = view.findViewById(R.id.btnId84)
        btnId85 = view.findViewById(R.id.btnId85)

        btnId71 = view.findViewById(R.id.btnId71)
        btnId72 = view.findViewById(R.id.btnId72)
        btnId73 = view.findViewById(R.id.btnId73)
        btnId74 = view.findViewById(R.id.btnId74)
        btnId75 = view.findViewById(R.id.btnId75)

        // for Treatment Type button onClick event listener
        btnSDF.setOnClickListener(this)
        btnSEAL.setOnClickListener(this)
        btnART.setOnClickListener(this)
        btnEXO.setOnClickListener(this)
        btnUNTR.setOnClickListener(this)
        btnSMART.setOnClickListener(this)

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

        // primary teeth
        btnId51.setOnClickListener(this)
        btnId52.setOnClickListener(this)
        btnId53.setOnClickListener(this)
        btnId54.setOnClickListener(this)
        btnId55.setOnClickListener(this)

        btnId61.setOnClickListener(this)
        btnId62.setOnClickListener(this)
        btnId63.setOnClickListener(this)
        btnId64.setOnClickListener(this)
        btnId65.setOnClickListener(this)

        btnId81.setOnClickListener(this)
        btnId82.setOnClickListener(this)
        btnId83.setOnClickListener(this)
        btnId84.setOnClickListener(this)
        btnId85.setOnClickListener(this)

        btnId71.setOnClickListener(this)
        btnId72.setOnClickListener(this)
        btnId73.setOnClickListener(this)
        btnId74.setOnClickListener(this)
        btnId75.setOnClickListener(this)


//        treatmentSelector = view.findViewById(R.id.treatmentSelector)
//        treatmentSelector.setOnCheckedChangeListener { _, checkedId ->
//            when (checkedId) {
//                R.id.btnSDF -> {
//                    selectedTreatment = "SDF"
//                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_sdf, null)
//                }
//                R.id.btnSEAL -> {
//                    selectedTreatment = "SEAL"
//                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_seal, null)
//                }
//                R.id.btnART -> {
//                    selectedTreatment = "ART"
//                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_art, null)
//                }
//                R.id.btnEXO -> {
//                    selectedTreatment = "EXO"
//                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_exo, null)
//                }
//                R.id.btnUNTR -> {
//                    selectedTreatment = "UNTR"
//                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_untr, null)
//                }
//                R.id.btnSMART -> {
//                    selectedTreatment = "SMART"
//                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_smart, null)
//
//                }
//                else -> {
//                    selectedTreatment = "NONE"
//                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_none, null)
//                }
//            }
//        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        defaultBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_none, null)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        treatmentFormCommunicator = activity as TreatmentFormCommunicator

        btnNext.setOnClickListener {
            val notes = etNotes.text.toString()
            val fvApplied = checkBoxFVApplied.isChecked
            val treatmentPlanComplete = checkBoxTreatmentPlanComplete.isChecked

            Log.d("TPC", treatmentPlanComplete.toString())
            Log.d("notes", notes)
            Log.d("fvApplied", fvApplied.toString())
            Log.d("teeth", teeth.toString())

            treatmentFormCommunicator.updateTreatment(notes, fvApplied, treatmentPlanComplete, teeth)
            fragmentCommunicator.goForward()
        }
        btnBack.setOnClickListener {
            val notes = etNotes.text.toString()
            val fvApplied = checkBoxFVApplied.isChecked
            val treatmentPlanComplete = checkBoxTreatmentPlanComplete.isChecked

            Log.d("TPC", treatmentPlanComplete.toString())
            Log.d("notes", notes)
            Log.d("fvApplied", fvApplied.toString())
            Log.d("teeth", teeth.toString())

            treatmentFormCommunicator.updateTreatment(notes, fvApplied, treatmentPlanComplete, teeth)
            fragmentCommunicator.goBack()
        }
    }

    override fun onClick(v: View) {
        val buttons = arrayOf(
            R.id.btnId11, R.id.btnId12, R.id.btnId13, R.id.btnId14, R.id.btnId15, R.id.btnId16,
            R.id.btnId17, R.id.btnId18, R.id.btnId21, R.id.btnId22, R.id.btnId23, R.id.btnId24, R.id.btnId25,
            R.id.btnId26, R.id.btnId27, R.id.btnId28, R.id.btnId31, R.id.btnId32, R.id.btnId33, R.id.btnId34,
            R.id.btnId35, R.id.btnId36, R.id.btnId37, R.id.btnId38, R.id.btnId41, R.id.btnId42, R.id.btnId43,
            R.id.btnId44, R.id.btnId45, R.id.btnId46, R.id.btnId47, R.id.btnId48,

            R.id.btnId55, R.id.btnId54, R.id.btnId53, R.id.btnId52, R.id.btnId51,
            R.id.btnId65, R.id.btnId64, R.id.btnId63, R.id.btnId62, R.id.btnId61,
            R.id.btnId85, R.id.btnId84, R.id.btnId83, R.id.btnId82, R.id.btnId81,
            R.id.btnId75, R.id.btnId74, R.id.btnId73, R.id.btnId72, R.id.btnId71
        )
        // for Treatment Type OnClick event array
        val buttonsTreatmentType = arrayOf(
            R.id.btnSDF, R.id.btnSEAL, R.id.btnART, R.id.btnEXO, R.id.btnUNTR, R.id.btnSMART
        )

        // for Treatment Type color change according to its name
        if (buttonsTreatmentType.contains(v.id)) {

            when (v.id) {
                R.id.btnSDF -> {
                    Toast.makeText(context, "Treatment SDF Type clicked", Toast.LENGTH_SHORT).show()
                    selectedTreatment = "SDF"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_sdf, null)
                    treatmentTypeBackgroundColor()
                    btnSDF.background = ResourcesCompat.getDrawable(resources, R.drawable.treatment_sdf, null)
                }

                R.id.btnSEAL -> {
                    Toast.makeText(context, "Treatment SEAL Type clicked", Toast.LENGTH_SHORT).show()
                    selectedTreatment = "SEAL"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_seal, null)
                    treatmentTypeBackgroundColor()
                    btnSEAL.background = ResourcesCompat.getDrawable(resources, R.drawable.treatment_seal, null)
                }

                R.id.btnART -> {
                    Toast.makeText(context, "Treatment btnART Type clicked", Toast.LENGTH_SHORT).show()

                    selectedTreatment = "ART"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_art, null)
                    treatmentTypeBackgroundColor()
                    btnART.background = ResourcesCompat.getDrawable(resources, R.drawable.treatment_art, null)
                }

                R.id.btnEXO -> {
                    Toast.makeText(context, "Treatment btnEXO Type clicked", Toast.LENGTH_SHORT).show()

                    selectedTreatment = "EXO"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_exo, null)
                    treatmentTypeBackgroundColor()
                    btnEXO.background = ResourcesCompat.getDrawable(resources, R.drawable.treatment_exo, null)
                }

                R.id.btnUNTR -> {
                    Toast.makeText(context, "Treatment btnUNTR Type clicked", Toast.LENGTH_SHORT).show()

                    selectedTreatment = "UNTR"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_untr, null)
                    treatmentTypeBackgroundColor()
                    btnUNTR.background = ResourcesCompat.getDrawable(resources, R.drawable.treatment_untr, null)
                }

                R.id.btnSMART -> {
                    Toast.makeText(context, "Treatment btnSMART Type clicked", Toast.LENGTH_SHORT).show()

                    selectedTreatment = "SMART"
                    btnBackground = ResourcesCompat.getDrawable(resources, R.drawable.treatment_smart, null)
                    treatmentTypeBackgroundColor()
                    btnSMART.background = ResourcesCompat.getDrawable(resources, R.drawable.treatment_smart, null)
                }

            }
        }

        if (buttons.contains(v.id)) {
            if (v.background == btnBackground) {
                v.background = defaultBackground
            } else {
                v.background = btnBackground
            }
        }
        when (v.id) {
            //secondary teeth
            R.id.btnId18 -> toggleTreatment(0)
            R.id.btnId17 -> toggleTreatment(1)
            R.id.btnId16 -> toggleTreatment(2)
            R.id.btnId15 -> toggleTreatment(3)
            R.id.btnId14 -> toggleTreatment(4)
            R.id.btnId13 -> toggleTreatment(5)
            R.id.btnId12 -> toggleTreatment(6)
            R.id.btnId11 -> toggleTreatment(7)

            R.id.btnId21 -> toggleTreatment(8)
            R.id.btnId22 -> toggleTreatment(9)
            R.id.btnId23 -> toggleTreatment(10)
            R.id.btnId24 -> toggleTreatment(11)
            R.id.btnId25 -> toggleTreatment(12)
            R.id.btnId26 -> toggleTreatment(13)
            R.id.btnId27 -> toggleTreatment(14)
            R.id.btnId28 -> toggleTreatment(15)

            R.id.btnId48 -> toggleTreatment(16)
            R.id.btnId47 -> toggleTreatment(17)
            R.id.btnId46 -> toggleTreatment(18)
            R.id.btnId45 -> toggleTreatment(19)
            R.id.btnId44 -> toggleTreatment(20)
            R.id.btnId43 -> toggleTreatment(21)
            R.id.btnId42 -> toggleTreatment(22)
            R.id.btnId41 -> toggleTreatment(23)

            R.id.btnId31 -> toggleTreatment(24)
            R.id.btnId32 -> toggleTreatment(25)
            R.id.btnId33 -> toggleTreatment(26)
            R.id.btnId34 -> toggleTreatment(27)
            R.id.btnId35 -> toggleTreatment(28)
            R.id.btnId36 -> toggleTreatment(29)
            R.id.btnId37 -> toggleTreatment(30)
            R.id.btnId38 -> toggleTreatment(31)

            // primary teeth
            R.id.btnId51 -> toggleTreatment(32)
            R.id.btnId52 -> toggleTreatment(33)
            R.id.btnId53 -> toggleTreatment(34)
            R.id.btnId54 -> toggleTreatment(35)
            R.id.btnId55 -> toggleTreatment(36)

            R.id.btnId61 -> toggleTreatment(37)
            R.id.btnId62 -> toggleTreatment(38)
            R.id.btnId63 -> toggleTreatment(39)
            R.id.btnId64 -> toggleTreatment(40)
            R.id.btnId65 -> toggleTreatment(41)

            R.id.btnId81 -> toggleTreatment(42)
            R.id.btnId82 -> toggleTreatment(43)
            R.id.btnId83 -> toggleTreatment(44)
            R.id.btnId84 -> toggleTreatment(45)
            R.id.btnId85 -> toggleTreatment(46)

            R.id.btnId71 -> toggleTreatment(47)
            R.id.btnId72 -> toggleTreatment(48)
            R.id.btnId73 -> toggleTreatment(49)
            R.id.btnId74 -> toggleTreatment(50)
            R.id.btnId75 -> toggleTreatment(51)
        }
    }

    private fun toggleTreatment(teethNumber: Int) {
        if (teeth[teethNumber] == selectedTreatment) {
            teeth[teethNumber] = defaultTreatment
        } else {
            teeth[teethNumber] = selectedTreatment

        }
    }

    private fun treatmentTypeBackgroundColor() {
        btnSDF.background = btnDefaultBackground
        btnSEAL.background = btnDefaultBackground
        btnART.background = btnDefaultBackground
        btnEXO.background = btnDefaultBackground
        btnUNTR.background = btnDefaultBackground
        btnSMART.background = btnDefaultBackground
    }
}

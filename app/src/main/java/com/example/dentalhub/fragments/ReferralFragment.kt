package com.example.dentalhub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.fragments.interfaces.ReferralFormCommunicator

class ReferralFragment: Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var referralFormCommunicator: ReferralFormCommunicator

    private lateinit var checkBoxNoReferral: CheckBox
    private lateinit var checkBoxHealthPost: CheckBox
    private lateinit var checkBoxHygienist: CheckBox
    private lateinit var checkBoxDentist: CheckBox
    private lateinit var checkBoxGeneralPhysician: CheckBox
    private lateinit var checkBoxOther: CheckBox

    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_referral, container, false)

        checkBoxNoReferral = view.findViewById(R.id.checkBoxNoReferral)
        checkBoxHealthPost = view.findViewById(R.id.checkBoxHealthPost)
        checkBoxHygienist = view.findViewById(R.id.checkBoxHygienist)
        checkBoxDentist = view.findViewById(R.id.checkBoxDentist)
        checkBoxGeneralPhysician = view.findViewById(R.id.checkBoxGeneralPhysician)
        checkBoxOther = view.findViewById(R.id.checkBoxOther)

        btnBack = view.findViewById(R.id.btnBack)
        btnNext = view.findViewById(R.id.btnNext)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentCommunicator = activity as TreatmentFragmentCommunicator
        referralFormCommunicator = activity as ReferralFormCommunicator

        btnNext.setOnClickListener {

            val noReferral = checkBoxNoReferral.isChecked
            val healthPost = checkBoxHealthPost.isChecked
            val hygienist = checkBoxHygienist.isChecked
            val dentist = checkBoxDentist.isChecked
            val generalPhysician = checkBoxGeneralPhysician.isChecked
            val other = checkBoxOther.isChecked

            referralFormCommunicator.updateReferral(noReferral, healthPost, hygienist, dentist, generalPhysician, other)
            fragmentCommunicator.goForward()
        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }

    }



}

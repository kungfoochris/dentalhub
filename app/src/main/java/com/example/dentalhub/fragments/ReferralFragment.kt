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
import com.example.dentalhub.fragments.interfaces.ReferralFormCommunicator
import android.widget.DatePicker
import android.app.DatePickerDialog
import java.util.*
import android.widget.TimePicker
import android.app.TimePickerDialog
import android.content.Context
import androidx.fragment.app.FragmentActivity
import java.text.DecimalFormat


class ReferralFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var referralFormCommunicator: ReferralFormCommunicator

    private lateinit var checkBoxNoReferral: CheckBox
    private lateinit var checkBoxHealthPost: CheckBox
    private lateinit var checkBoxHygienist: CheckBox
    private lateinit var checkBoxDentist: CheckBox
    private lateinit var checkBoxGeneralPhysician: CheckBox
    private lateinit var checkBoxOther: CheckBox
    private lateinit var etOtherDetails: EditText

    private lateinit var etRecallDate: EditText
    private lateinit var etRecallTime: EditText



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
        etOtherDetails = view.findViewById(R.id.etOtherDetails)

        etRecallDate = view.findViewById(R.id.etRecallDate)
        etRecallTime = view.findViewById(R.id.etRecallTime)

        checkBoxOther.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                etOtherDetails.visibility = View.VISIBLE
            } else {
                etOtherDetails.visibility = View.GONE
            }
        }

        etRecallDate.setOnFocusChangeListener { view, b ->
            if(b){
                val c = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(activity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> etRecallDate.setText( year.toString() +"-"+ DecimalFormat("00").format(monthOfYear+1).toString()+ "-"+DecimalFormat("00").format(dayOfMonth).toString()) },
                    mYear,
                    mMonth,
                    mDay
                )
                datePickerDialog.show()
            }
        }
       etRecallTime.setOnFocusChangeListener { view, b ->
           if(b){
               // Get Current Time
               val c = Calendar.getInstance()
               val mHour = c.get(Calendar.HOUR_OF_DAY)
               val mMinute = c.get(Calendar.MINUTE)

               // Launch Time Picker Dialog
               val timePickerDialog = TimePickerDialog(activity,
                   TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> etRecallTime.setText(DecimalFormat("00").format(hourOfDay)+":"+DecimalFormat("00").format(minute)) },
                   mHour,
                   mMinute,
                   false
               )
               timePickerDialog.show()
           }
       }

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
            val otherDetails = etOtherDetails.text.toString()

            referralFormCommunicator.updateReferral(
                noReferral,
                healthPost,
                hygienist,
                dentist,
                generalPhysician,
                other,
                otherDetails
            )
            fragmentCommunicator.goForward()
        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }

    }


}

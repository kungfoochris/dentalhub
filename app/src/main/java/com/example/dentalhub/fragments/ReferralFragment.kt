package com.example.dentalhub.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.dentalhub.ObjectBox
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.entities.Activity
import com.example.dentalhub.entities.Geography
import com.example.dentalhub.fragments.interfaces.ReferralFormCommunicator
import com.example.dentalhub.utils.AdapterHelper
import io.objectbox.Box
import io.objectbox.query.Query
import java.text.DecimalFormat
import java.util.*


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
    private lateinit var spinnerLocation: Spinner
    private lateinit var spinnerActivity: Spinner

    private lateinit var activitiesBox: Box<Activity>
    private lateinit var geographiesBox: Box<Geography>
    private lateinit var activitiesQuery: Query<Activity>
    private lateinit var geographiesQuery: Query<Geography>


    private lateinit var btnNext: Button
    private lateinit var btnBack: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_referral, container, false)

        activitiesBox = ObjectBox.boxStore.boxFor(Activity::class.java)
        geographiesBox = ObjectBox.boxStore.boxFor(Geography::class.java)

        activitiesQuery = activitiesBox.query().build()
        geographiesQuery = geographiesBox.query().build()

        checkBoxNoReferral = view.findViewById(R.id.checkBoxNoReferral)
        checkBoxHealthPost = view.findViewById(R.id.checkBoxHealthPost)
        checkBoxHygienist = view.findViewById(R.id.checkBoxHygienist)
        checkBoxDentist = view.findViewById(R.id.checkBoxDentist)
        checkBoxGeneralPhysician = view.findViewById(R.id.checkBoxGeneralPhysician)
        checkBoxOther = view.findViewById(R.id.checkBoxOther)
        etOtherDetails = view.findViewById(R.id.etOtherDetails)

        etRecallDate = view.findViewById(R.id.etRecallDate)
        etRecallTime = view.findViewById(R.id.etRecallTime)
        spinnerLocation = view.findViewById(R.id.spinnerLocation)
        spinnerActivity = view.findViewById(R.id.spinnerActivity)

        setupActivities(activity as Context)
        setupGeographies(activity as Context)


        checkBoxOther.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                etOtherDetails.setText("")
                etOtherDetails.visibility = View.VISIBLE
            } else {
                etOtherDetails.visibility = View.GONE
            }
        }

        etRecallDate.setOnFocusChangeListener { _, b ->
            if (b) {
                val c = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    activity,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        etRecallDate.setText(
                            year.toString() + "-" + DecimalFormat("00").format(monthOfYear + 1).toString() + "-" + DecimalFormat(
                                "00"
                            ).format(dayOfMonth).toString()
                        )
                    },
                    mYear,
                    mMonth,
                    mDay
                )
                datePickerDialog.datePicker.minDate = Date().time
                datePickerDialog.show()
            }
        }
        etRecallTime.setOnFocusChangeListener { view, b ->
            if (b) {
                // Get Current Time
                val c = Calendar.getInstance()
                val mHour = c.get(Calendar.HOUR_OF_DAY)
                val mMinute = c.get(Calendar.MINUTE)

                // Launch Time Picker Dialog
                val timePickerDialog = TimePickerDialog(
                    activity,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        etRecallTime.setText(
                            DecimalFormat("00").format(
                                hourOfDay
                            ) + ":" + DecimalFormat("00").format(minute)
                        )
                    },
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



            if(isFormValid()){
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

                val recallDate = etRecallDate.text.toString()
                val recallTime = etRecallTime.text.toString()
                val selectedGeography = spinnerLocation.selectedItem.toString()
                val selectedActivity = spinnerActivity.selectedItem.toString()
                referralFormCommunicator.updateRecall(recallDate, recallTime, selectedGeography, selectedActivity)

                fragmentCommunicator.goForward()

            }else{
                // form is not valid
            }


        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }

    }



    private fun isFormValid(): Boolean {
        var status = false

        if( (checkBoxOther.isChecked && etOtherDetails.text.toString().isNotEmpty()) || (!checkBoxOther.isChecked) && etOtherDetails.text.isEmpty()){
            status = true
        }
        return status

    }

    private fun setupActivities(context: Context) {
        val allActivities = activitiesQuery.find()
        val activities = mutableListOf<String>()
        for (activity in allActivities) {
            activities.add(activity.name)
        }

        spinnerActivity.adapter = AdapterHelper.createAdapter(context, activities)
    }

    private fun setupGeographies(context: Context) {
        val allGeographies = geographiesQuery.find()
        val geographies = mutableListOf<String>()
        for (geography in allGeographies) {
            geographies.add(geography.address())
        }
        spinnerLocation.adapter = AdapterHelper.createAdapter(context, geographies)
    }


}

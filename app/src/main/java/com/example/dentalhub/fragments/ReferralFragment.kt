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
import com.example.dentalhub.DentalApp
import com.example.dentalhub.ObjectBox
import com.example.dentalhub.R
import com.example.dentalhub.TreatmentFragmentCommunicator
import com.example.dentalhub.entities.*
import com.example.dentalhub.fragments.interfaces.ReferralFormCommunicator
import com.example.dentalhub.utils.AdapterHelper
import io.objectbox.Box
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.fragment_referral.*
import java.text.DecimalFormat
import java.util.*


class ReferralFragment : Fragment() {
    private lateinit var fragmentCommunicator: TreatmentFragmentCommunicator
    private lateinit var referralFormCommunicator: ReferralFormCommunicator

    private lateinit var encounterBox: Box<Encounter>
    private var encounter = Encounter()
    private lateinit var referralBox: Box<Referral>
    private var referral = Referral()

//    private lateinit var radioButtonNoReferral: RadioButton
//    private lateinit var radioButtonHealthPost: RadioButton
//    private lateinit var radioButtonHygienist: RadioButton
//    private lateinit var radioButtonDentist: RadioButton
//    private lateinit var radioButtonGeneralPhysician: RadioButton
//    private lateinit var radioButtonOther: RadioButton
    private lateinit var rgReferrals : RadioGroup
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

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)

        activitiesQuery = activitiesBox.query().build()
        geographiesQuery = geographiesBox.query().build()

//        radioButtonNoReferral = view.findViewById(R.id.radioNoReferral)
//        radioButtonHealthPost = view.findViewById(R.id.radioHealthPost)
//        radioButtonHygienist = view.findViewById(R.id.radioHygienist)
//        radioButtonDentist = view.findViewById(R.id.radioDentist)
//        radioButtonGeneralPhysician = view.findViewById(R.id.radioGeneralPhysician)
//        radioButtonOther = view.findViewById(R.id.radioOther)
        rgReferrals = view.findViewById(R.id.rgReferrals)
        etOtherDetails = view.findViewById(R.id.etOtherDetails)


        etRecallDate = view.findViewById(R.id.etRecallDate)
        etRecallTime = view.findViewById(R.id.etRecallTime)
        spinnerLocation = view.findViewById(R.id.spinnerLocation)
        spinnerActivity = view.findViewById(R.id.spinnerActivity)

        setupActivities(activity as Context)
        setupGeographies(activity as Context)

        rgReferrals.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.radioOther) {
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
        
        setupUI(activity as Context)

        btnNext.setOnClickListener {
            if(isFormValid()){
                val noReferral = radioNoReferral.isChecked
                val healthPost = radioHealthPost.isChecked
                val hygienist = radioHygienist.isChecked
                val dentist = radioDentist.isChecked
                val generalPhysician = radioGeneralPhysician.isChecked
                val other = radioOther.isChecked
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

    private fun setupUI(applicationContext: Context) {

        val encounterId = DentalApp.readFromPreference(applicationContext, "Encounter_ID", "0").toLong()

        if (encounterId != 0.toLong()) {

            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!



            referral = referralBox.query()
                .equal(Referral_.encounterId, encounter.id)
                .orderDesc(Referral_.id).build().findFirst()!!

            var radioButtonMap = mapOf(radioNoReferral to referral.no_referral,
                radioHealthPost to referral.health_post, radioHygienist to referral.hygienist,
                radioDentist to referral.dentist, radioGeneralPhysician to referral.general_physician,
                radioOther to referral.other)

            for (radioButton in radioButtonMap) {
                if (radioButton.value) {
                    radioButton.key.isChecked = true
                    break
                }
            }

            if (!referral.other_details.isNullOrEmpty()) etOtherDetails.setText(referral.other_details)
        }
    }

    private fun isFormValid(): Boolean {
        var status = false

        if( (radioOther.isChecked && etOtherDetails.text.toString().isNotEmpty()) || (!radioOther.isChecked) && etOtherDetails.text.isEmpty()){
            status = true
        }

        // For checking if any radio button is clicked or not
        if (rgReferrals.checkedRadioButtonId == -1) {
//            Toast.makeText(activity, "Radio button is not selected", Toast.LENGTH_SHORT).show()
            status = false
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

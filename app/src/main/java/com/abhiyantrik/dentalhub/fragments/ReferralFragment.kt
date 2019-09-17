package com.abhiyantrik.dentalhub.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.abhiyantrik.dentalhub.*
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.fragments.interfaces.ReferralFormCommunicator
import com.hornet.dateconverter.DateConverter
import io.objectbox.Box
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
    private lateinit var tvRecallDateReferral: TextView
    private lateinit var rgReferrals: RadioGroup
    private lateinit var rgRecalls: RadioGroup
    private lateinit var etOtherDetails: EditText

    private lateinit var etRecallDate: EditText
    private lateinit var etRecallTime: EditText

    private lateinit var activitiesBox: Box<Activity>
    private lateinit var geographiesBox: Box<Geography>
    private lateinit var patientBox: Box<Patient>
//    private lateinit var activitiesQuery: Query<Activity>
//    private lateinit var geographiesQuery: Query<Geography>


    private lateinit var patient: Patient
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
        patientBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)

//        activitiesQuery = activitiesBox.query().build()
//        geographiesQuery = geographiesBox.query().build()

//        radioButtonNoReferral = view.findViewById(R.id.radioNoReferral)
//        radioButtonHealthPost = view.findViewById(R.id.radioHealthPost)
//        radioButtonHygienist = view.findViewById(R.id.radioHygienist)
//        radioButtonDentist = view.findViewById(R.id.radioDentist)
//        radioButtonGeneralPhysician = view.findViewById(R.id.radioGeneralPhysician)
//        radioButtonOther = view.findViewById(R.id.radioOther)
        tvRecallDateReferral = view.findViewById(R.id.tvRecallDateReferral)
        rgReferrals = view.findViewById(R.id.rgReferrals)
        rgRecalls = view.findViewById(R.id.rgRecalls)
        etOtherDetails = view.findViewById(R.id.etOtherDetails)


        rgReferrals.check(R.id.radioHealthPost)

        etRecallDate = view.findViewById(R.id.etRecallDate)
        etRecallTime = view.findViewById(R.id.etRecallTime)

        etRecallDate.setText(DentalApp.lastRecallDate)
        etRecallTime.setText(DentalApp.lastRecallDate)

        rgReferrals.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.radioOther) {
                etOtherDetails.setText("")
                etOtherDetails.visibility = View.VISIBLE
            } else {
                etOtherDetails.visibility = View.GONE
            }
            if (i == R.id.radioHealthPost) {
                etRecallDate.setText("")
                tvRecallDateReferral.visibility = View.VISIBLE
                etRecallDate.visibility = View.VISIBLE
                rgRecalls.visibility = View.VISIBLE
                etRecallTime.visibility = View.VISIBLE
            } else {
                tvRecallDateReferral.visibility = View.GONE
                rgRecalls.visibility = View.GONE
                etRecallDate.visibility = View.GONE
                etRecallTime.visibility = View.GONE
            }
        }
        rgRecalls.setOnCheckedChangeListener { radioGroup, i ->
            var recallDate = ""
            val nepaliCalender = DateConverter()

            val todayNepali = nepaliCalender.todayNepaliDate

            val yearToday = todayNepali.year
            val monthToday = todayNepali.month + 1
            val dayToday = todayNepali.day

            when (i) {
                R.id.radioOneWeek -> {
                    recallDate = if (dayToday + 7 > 30) {
                        "$yearToday-" + DecimalFormat("00").format(monthToday + 1) + "-" + DecimalFormat(
                            "00"
                        ).format((dayToday + 7) % 30)
                    } else {

                        if (monthToday + 1 > 12) {
                            "${yearToday + 1}-" + DecimalFormat("00").format("01") + "-" + DecimalFormat(
                                "00"
                            ).format((dayToday + 7) % 30)
                        } else {
                            "$yearToday-" + DecimalFormat("00").format(monthToday + 1) + "-" + DecimalFormat(
                                "00"
                            ).format((dayToday + 7) % 30)
                        }

                    }

                }
                R.id.radioOneMonth -> {
                    recallDate = if (monthToday == 12) {
                        "${yearToday + 1}-" + DecimalFormat("00").format("01") + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                    } else {
                        "${yearToday}-" + DecimalFormat("00").format(monthToday + 1) + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                    }
                }
                R.id.radioSixMonths -> {
                    recallDate = if (monthToday + 6 > 12) {
                        "${yearToday + 1}-" + DecimalFormat("00").format(((monthToday + 6) % 12)) + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                    } else {
                        "$yearToday-" + DecimalFormat("00").format(monthToday + 6) + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                    }
                }
                R.id.radioOneYear -> {
                    recallDate =
                        "${yearToday + 1}-" + DecimalFormat("00").format(monthToday) + "-" + DecimalFormat(
                            "00"
                        ).format(dayToday)
                }
            }
            etRecallDate.setText(recallDate)
        }

        etRecallDate.setOnFocusChangeListener { _, b ->
            if (b) {
                val nepaliDateConverter = DateConverter()
                val dpd =
                    com.hornet.dateconverter.DatePicker.DatePickerDialog.newInstance { view, year, monthOfYear, dayOfMonth ->
                        val month = DecimalFormat("00").format(monthOfYear + 1).toString()
                        val day = DecimalFormat("00").format(dayOfMonth).toString()
                        etRecallDate.setText("$year-$month-$day")
                    }
                dpd.setMinDate(nepaliDateConverter.todayNepaliDate)
                dpd.show(fragmentManager, "RecallDate")

            }
        }
        etRecallTime.setOnFocusChangeListener { _, b ->
            if (b) {
                // Get Current Time
                val c = Calendar.getInstance()
                val mHour = c.get(Calendar.HOUR_OF_DAY)
                val mMinute = c.get(Calendar.MINUTE)

                // Launch Time Picker Dialog
                val timePickerDialog = TimePickerDialog(
                    activity,
                    TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
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
            if (isFormValid()) {
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
                DentalApp.lastRecallDate = recallDate
                DentalApp.lastRecallTime = recallTime
                //referralFormCommunicator.updateRecall(recallDate, recallTime, selectedGeography, selectedActivity)
                referralFormCommunicator.updateRecallDate(recallDate, recallTime)

                fragmentCommunicator.goForward()

            } else {
                // form is not valid
            }
        }
        btnBack.setOnClickListener {
            fragmentCommunicator.goBack()
        }
    }

    private fun setupUI(applicationContext: Context) {

        val encounterId =
            DentalApp.readFromPreference(applicationContext, "Encounter_ID", "0").toLong()

        if (encounterId != 0.toLong()) {

            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!


            patient = patientBox.query().equal(
                Patient_.id,
                DentalApp.readIntFromPreference(
                    applicationContext,
                    Constants.PREF_SELECTED_PATIENT
                ).toLong()
            ).build().findFirst()!!
            referral = referralBox.query()
                .equal(Referral_.encounterId, encounter.id)
                .orderDesc(Referral_.id).build().findFirst()!!


            val radioButtonMap = mapOf(
                radioNoReferral to referral.no_referral,
                radioHealthPost to referral.health_post,
                radioHygienist to referral.hygienist,
                radioDentist to referral.dentist,
                radioGeneralPhysician to referral.general_physician,
                radioOther to referral.other
            )

            for (radioButton in radioButtonMap) {
                if (radioButton.value) {
                    radioButton.key.isChecked = true
                    break
                }
            }

            if (!referral.other_details.isNullOrEmpty()) etOtherDetails.setText(referral.other_details)
            etRecallDate.setText(patient.recall_date)
            etRecallTime.setText(patient.recall_time)
        }
    }

    private fun isFormValid(): Boolean {
        var status = false

        if ((radioOther.isChecked && etOtherDetails.text.toString().isNotEmpty()) || (!radioOther.isChecked) && etOtherDetails.text.isEmpty()) {
            status = true
        }

        // For checking if any radio button is clicked or not
        if (rgReferrals.checkedRadioButtonId == -1) {
            Toast.makeText(activity, "Radio button is not selected", Toast.LENGTH_SHORT).show()
            status = false
        }

        if (radioHealthPost.isChecked) {
            if (etRecallDate.text.isNullOrBlank()) { // || etRecallTime.text.isNullOrBlank()
                Toast.makeText(
                    activity,
                    "Recall Date and Time should be specified.",
                    Toast.LENGTH_SHORT
                ).show()
                status = false
            }
        }
        return status
    }
}

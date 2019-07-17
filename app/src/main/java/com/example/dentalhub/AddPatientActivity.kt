package com.example.dentalhub

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.entities.Patient
import com.example.dentalhub.interfaces.DjangoInterface
import com.example.dentalhub.utils.AdapterHelper
import com.example.dentalhub.utils.DateHelper
import com.example.dentalhub.utils.DateValidator
import com.google.firebase.perf.metrics.AddTrace
import com.google.gson.Gson
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class AddPatientActivity : AppCompatActivity() {

    private lateinit var btnAddPatient: Button
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerMaritalStatus: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etDOB: EditText
    private lateinit var etPhone: EditText
    private lateinit var etStreetAddress: EditText
    private lateinit var etWard: EditText
    private lateinit var etCity: EditText
    private lateinit var etState: EditText
    private lateinit var etCountry: EditText

    private lateinit var loading: ProgressBar
    private lateinit var tvErrorMessage: TextView

    private lateinit var context: Context
    private var patient: Patient? = null
    private val TAG = "AddPatientActivity"

    private lateinit var patientsBox: Box<Patient>

    @AddTrace(name = "onCreateAddPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)
        patient = intent.getParcelableExtra("patient")
        context = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()
    }

    @AddTrace(name = "initUIAddPatientActivity", enabled = true /* optional */)
    private fun initUI() {
        loading = findViewById(R.id.loading)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etLastName = findViewById(R.id.etLastName)

        etStreetAddress = findViewById(R.id.etStreetAddress)
        etWard = findViewById(R.id.etWard)
        etCity = findViewById(R.id.etCity)
        etState = findViewById(R.id.etState)
        etCountry = findViewById(R.id.etCountry)

        etPhone = findViewById(R.id.etPhone)
        etDOB = findViewById(R.id.etDOB)

        btnAddPatient = findViewById(R.id.btnAddPatient)
        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerMaritalStatus = findViewById(R.id.spinnerMartialStatus)
        spinnerEducationLevel = findViewById(R.id.spinnerEducationLevel)

        etDOB.setOnFocusChangeListener { view, b ->
            if(b){
                val c = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(context,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> etDOB.setText(year.toString() +"-"+ DecimalFormat("00").format(monthOfYear+1).toString()+ "-"+DecimalFormat("00").format(dayOfMonth).toString()) },
                    mYear,
                    mMonth,
                    mDay
                )
                datePickerDialog.show()
            }
        }
        spinnerGender.adapter =
            AdapterHelper.createAdapter(context, resources.getStringArray(R.array.gender_list).toList())
        spinnerMaritalStatus.adapter =
            AdapterHelper.createAdapter(context, resources.getStringArray(R.array.martial_status_list).toList())
        spinnerEducationLevel.adapter =
            AdapterHelper.createAdapter(context, resources.getStringArray(R.array.education_level_list).toList())

        updateUI()
        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)

        btnAddPatient.setOnClickListener {
            if (isFormValid()) {
                savePatient()
            }
        }
    }

    private fun updateUI() {
        if (patient != null) {
            etFirstName.setText(patient!!.first_name)
            etMiddleName.setText(patient!!.middle_name)
            etLastName.setText(patient!!.last_name)
            etDOB.setText(patient!!.dob)
            etPhone.setText(patient!!.phone)
            etWard.setText(patient!!.ward.toString())
            etStreetAddress.setText(patient!!.street_address)
            etCity.setText(patient!!.city)
            etState.setText(patient!!.state)
            etCountry.setText(patient!!.country)
            spinnerGender.setSelection(resources.getStringArray(R.array.gender_list).indexOf(patient!!.gender))
            spinnerMaritalStatus.setSelection(resources.getStringArray(R.array.martial_status_list).indexOf(patient!!.marital_status))
            spinnerEducationLevel.setSelection(resources.getStringArray(R.array.education_level_list).indexOf(patient!!.education))
        }
    }

    @AddTrace(name = "savePatientAddPatientActivity", enabled = true /* optional */)
    private fun savePatient() {
        Log.d(TAG, "savePatient()")
        loading.visibility = View.VISIBLE
        tvErrorMessage.visibility = View.GONE
        val patient = createPatient()
//        if(DentalApp.isConnectedToWifi(this)){
//            saveToServer(patient)
//        }else{
//            saveToLocalDB(patient)
//        }
        saveToLocalDB(patient)
        loading.visibility = View.GONE
    }

    @AddTrace(name = "createPatientAddPatientActivity", enabled = true /* optional */)
    private fun createPatient(): Patient {
        Log.d(TAG, "createPatient()")
        val id: Long = 0
        val firstName = etFirstName.text.toString()
        val middleName = etMiddleName.text.toString()
        val lastName = etLastName.text.toString()
        val gender = spinnerGender.selectedItem.toString()
        val dob = etDOB.text.toString()
        val phone = etPhone.text.toString()
        val education = spinnerEducationLevel.selectedItem.toString()
        val maritalStatus = spinnerMaritalStatus.selectedItem.toString()
        val streetAddress = etStreetAddress.text.toString()
        val ward = etWard.text.toString().toInt()
        val city = etCity.text.toString()
        val state = etState.text.toString()
        val country = etCountry.text.toString()
        val latitude = DentalApp.location.latitude
        val longitude = DentalApp.location.longitude
        val date = DateHelper.getCurrentDate()
        if (patient != null) {
            patient = patientsBox.get(patient!!.id)
            patient!!.first_name = firstName
            patient!!.middle_name = middleName
            patient!!.last_name = lastName
            patient!!.gender = gender
            patient!!.dob = dob
            patient!!.phone = phone
            patient!!.education = education
            patient!!.marital_status
            patient!!.street_address = streetAddress
            patient!!.state = state
            patient!!.city = city
            patient!!.country = country
            patient!!.latitude = latitude
            patient!!.longitude = longitude
            patient!!.created_at = date
            patient!!.updated_at = date
            return patient!!
        } else {
            return Patient(
                id,
                firstName,
                middleName,
                lastName,
                gender,
                dob,
                phone,
                education,
                maritalStatus,
                streetAddress,
                ward,
                city,
                state,
                country,
                latitude,
                longitude,
                date,
                date,
                false
            )
        }

    }

    @AddTrace(name = "saveToLocalDBAddPatientActivity", enabled = true /* optional */)
    private fun saveToLocalDB(patient: Patient) {
        Log.d(TAG, "saveToLocalDB")
        patientsBox.put(patient)
        val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
        viewPatientIntent.putExtra("patient", patient)
        startActivity(viewPatientIntent)

    }

    @AddTrace(name = "saveToServerAddPatientActivity", enabled = true /* optional */)
    private fun saveToServer(patient: Patient) {
        Log.d(TAG, "saveToServer()")
        Log.d(TAG, patient.toString())
        val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addPatient(
            "JWT $token",
            patient.id,
            patient.first_name,
            patient.last_name,
            patient.gender,
            patient.phone,
            patient.middle_name,
            patient.dob,
            patient.education,
            patient.street_address,
            patient.ward,
            patient.city,
            patient.state,
            patient.country,
            patient.latitude,
            patient.longitude
        )
        call.enqueue(object : Callback<Patient> {
            override fun onFailure(call: Call<Patient>, t: Throwable) {
                Log.d("onFailure", t.toString())
                tvErrorMessage.text = t.message.toString()
                tvErrorMessage.visibility = View.VISIBLE
                loading.visibility = View.GONE
            }

            override fun onResponse(call: Call<Patient>, response: Response<Patient>) {
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val tempPatient = response.body() as Patient
                            val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
                            viewPatientIntent.putExtra("patient", tempPatient)
                            startActivity(viewPatientIntent)
                        }
                        400 -> {
                            tvErrorMessage.text = getString(R.string.error_http_400)
                            tvErrorMessage.visibility = View.VISIBLE
                        }
                        404 -> {
                            tvErrorMessage.text = getString(R.string.error_http_404)
                            tvErrorMessage.visibility = View.VISIBLE
                        }
                        else -> {
                            tvErrorMessage.text = getString(R.string.error_http_500)
                            tvErrorMessage.visibility = View.VISIBLE
                        }
                    }
                    loading.visibility = View.GONE
                } else {
                    Log.d(TAG, response.code().toString())
                    Log.d(TAG, Gson().toJson(response.body()).toString())
                    tvErrorMessage.text = response.message()
                    tvErrorMessage.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                }
            }
        })
    }

    @AddTrace(name = "isFormValidAddPatientActivity", enabled = true /* optional */)
    private fun isFormValid(): Boolean {
        tvErrorMessage.visibility = View.GONE

        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val phone = etPhone.text.toString()
        val dob = etDOB.text.toString()
        val ward = etWard.text.toString()

        if (firstName.isBlank() || firstName.isEmpty() || firstName.length < 2) {
            tvErrorMessage.text = resources.getString(R.string.first_name_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (lastName.isBlank() || lastName.isEmpty() || lastName.length < 2) {
            tvErrorMessage.text = resources.getString(R.string.last_name_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (phone.isBlank() || phone.isEmpty() || phone.length < 6) {
            tvErrorMessage.text = resources.getString(R.string.phone_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (!DateValidator.isValid(dob)) {
            tvErrorMessage.text = resources.getString(R.string.valid_date_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (ward.isEmpty() || ward.isBlank()) {
            tvErrorMessage.text = resources.getString(R.string.ward_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onPause() {
        finish()
        super.onPause()
    }
}
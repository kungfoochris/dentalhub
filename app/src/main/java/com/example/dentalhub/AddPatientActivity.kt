package com.example.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.dbhelpers.DentalHubDBHelper
import com.example.dentalhub.interfaces.DjangoInterface
import com.example.dentalhub.models.Patient
import com.example.dentalhub.utils.AdapterHelper

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPatientActivity : AppCompatActivity(){

    private lateinit var btnAddPatient: Button
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerMaritalStatus: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etDOB: EditText
    private lateinit var etPhone: EditText
    private lateinit var etCity: EditText
    private lateinit var etState: EditText
    private lateinit var etCountry: EditText

    private lateinit var loading: ProgressBar
    private lateinit var tvErrorMessage: TextView

    private lateinit var context: Context
    private lateinit var dbHelper: DentalHubDBHelper
    private val TAG = "AddPatientActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        context = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DentalHubDBHelper(context)

        initUI()
    }

    private fun initUI() {
        loading = findViewById(R.id.loading)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etLastName = findViewById(R.id.etLastName)

        etCity = findViewById(R.id.etCity)
        etState = findViewById(R.id.etState)
        etCountry = findViewById(R.id.etCountry)

        etPhone = findViewById(R.id.etPhone)
        etDOB = findViewById(R.id.etDOB)

        btnAddPatient = findViewById(R.id.btnAddPatient)
        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerMaritalStatus = findViewById(R.id.spinnerMartialStatus)
        spinnerEducationLevel = findViewById(R.id.spinnerEducationLevel)
        spinnerGender.adapter = AdapterHelper.createAdapter(context, resources.getStringArray(R.array.gender_list).toList())
        spinnerMaritalStatus.adapter = AdapterHelper.createAdapter(context, resources.getStringArray(R.array.martial_status_list).toList())
        spinnerEducationLevel.adapter = AdapterHelper.createAdapter(context, resources.getStringArray(R.array.education_level_list).toList())

        btnAddPatient.setOnClickListener {
            if(isFormValid()){
                savePatient()
            }
        }
    }

    private fun savePatient() {
        Log.d(TAG, "savePatient()")
        loading.visibility = View.VISIBLE
        tvErrorMessage.visibility = View.GONE
        val patient = createPatient()
        if(DentalApp.isConnectedToWifi(this)){
            saveToServer(patient)
        }else{
            saveToLocalDB(patient)
        }
        loading.visibility = View.GONE
    }

    private fun createPatient() : Patient{
        Log.d(TAG, "createPatient()")
        val id=""
        val firstName = etFirstName.text.toString()
        val middleName = etMiddleName.text.toString()
        val lastName = etLastName.text.toString()
        val fullName = firstName+" "+middleName+" "+lastName
        val gender = spinnerGender.selectedItem.toString()
        val dob = etDOB.text.toString()
        val phone = etPhone.text.toString()
        val education = spinnerEducationLevel.selectedItem.toString()
        val city = etCity.text.toString()
        val state = etState.text.toString()
        val country = etCountry.text.toString()
        val latitude = ""
        val longitude = ""
        val date=""
        return Patient(id, firstName, middleName, lastName, fullName, gender, dob, phone, education, city, state, country, latitude, longitude, date)
    }

    private fun saveToLocalDB(patient: Patient) {
        Log.d(TAG, "saveToLocalDB")
        dbHelper.addPatient(patient)

        val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
        viewPatientIntent.putExtra("patient", patient)
        startActivity(viewPatientIntent)

    }

    private fun saveToServer(patient: Patient) {
        Log.d(TAG,"saveToServer()")
        val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN,"")
        val panelService = DjangoInterface.create(this)
        val call = panelService.addPatient("JWT $token", patient.id, patient.first_name, patient.last_name, patient.gender, patient.phone, patient.middle_name!!, patient.dob!!,  patient.education!!, patient.city!!, patient.state!!, patient.country!!, patient.latitude!!, patient.longitude!!)
        call.enqueue(object: Callback<Patient>{
            override fun onFailure(call: Call<Patient>, t: Throwable) {
                Log.d("onFailure", t.toString())
                tvErrorMessage.text = t.message.toString()
                tvErrorMessage.visibility = View.VISIBLE
                loading.visibility = View.GONE
            }

            override fun onResponse(call: Call<Patient>, response: Response<Patient>) {
                if(null != response.body()){
                    when(response.code()){
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
                }else{
                    Log.d(TAG, response.code().toString())
                    Log.d(TAG,response.body().toString())
                    tvErrorMessage.text = response.message()
                    tvErrorMessage.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                }
            }

        })

    }

    private fun isFormValid(): Boolean {
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
}
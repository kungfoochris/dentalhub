package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.utils.AdapterHelper
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.utils.DateValidator
import com.google.firebase.perf.metrics.AddTrace
import com.hornet.dateconverter.DateConverter
import io.objectbox.Box
import java.text.DecimalFormat

class AddPatientActivity : AppCompatActivity() {

    private lateinit var btnAddPatient: Button
    private lateinit var spinnerGender: Spinner
    private lateinit var spinnerEducationLevel: Spinner
    private lateinit var etFirstName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhone: EditText
    private lateinit var spinnerWard: Spinner
    private lateinit var spinnerMunicipality: Spinner
    private lateinit var spinnerDistrict: Spinner

    private lateinit var spinnerDobDay: Spinner
    private lateinit var spinnerDobMonth: Spinner
    private lateinit var spinnerDobYear: Spinner


    private lateinit var loading: ProgressBar
    private lateinit var tvErrorMessage: TextView

    private lateinit var context: Context
    private var patient: Patient? = null
    private val TAG = "AddPatientActivity"
    private var action = "new"

    private var allWards = mutableListOf<Ward>()
    private var allMunicipalities = mutableListOf<Municipality>()
    private var allDistricts = mutableListOf<District>()

    private lateinit var patientsBox: Box<Patient>
    private lateinit var districtsBox: Box<com.abhiyantrik.dentalhub.entities.District>
    private lateinit var municipalitiesBox: Box<Municipality>
    private lateinit var wardsBox: Box<Ward>

    @AddTrace(name = "onCreateAddPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)
        patient = intent.getParcelableExtra("patient")
        action = intent.getStringExtra("ACTION")
        context = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()
    }

    @AddTrace(name = "initUIAddPatientActivity", enabled = true /* optional */)
    private fun initUI() {
        districtsBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.District::class.java)
        municipalitiesBox = ObjectBox.boxStore.boxFor(Municipality::class.java)
        wardsBox = ObjectBox.boxStore.boxFor(Ward::class.java)

        loading = findViewById(R.id.loading)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        etFirstName = findViewById(R.id.etFirstName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etLastName = findViewById(R.id.etLastName)

        spinnerWard = findViewById(R.id.spinnerWard)
        spinnerMunicipality = findViewById(R.id.spinnerMunicipality)
        spinnerDistrict = findViewById(R.id.spinnerDistrict)

        spinnerDobDay = findViewById(R.id.spinnerDobDay)
        spinnerDobMonth = findViewById(R.id.spinnerDobMonth)
        spinnerDobYear = findViewById(R.id.spinnerDobYear)


        etPhone = findViewById(R.id.etPhone)

        btnAddPatient = findViewById(R.id.btnAddPatient)
        spinnerGender = findViewById(R.id.spinnerGender)
        spinnerEducationLevel = findViewById(R.id.spinnerEducationLevel)

        setupDistricts()
        spinnerGender.adapter =
            AdapterHelper.createAdapter(
                context,
                resources.getStringArray(R.array.gender_list).toList()
            )

        spinnerEducationLevel.adapter =
            AdapterHelper.createAdapter(
                context,
                resources.getStringArray(R.array.education_level_list).toList()
            )

        val nepaliDateToday = DateConverter().todayNepaliDate
        val currentYear = nepaliDateToday.year
        val startYear = currentYear - 120

        spinnerDobDay.adapter = AdapterHelper.createAdapterWithInts(context, (1..32).toList())
        spinnerDobMonth.adapter = AdapterHelper.createAdapterWithInts(context, (1..12).toList())
        spinnerDobYear.adapter = AdapterHelper.createAdapterWithInts(context, (startYear..currentYear).reversed().toList())

        updateUI()
        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)

        spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setupMunicipalities()
            }
        }
        spinnerMunicipality.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setupWards()
            }

        }
        btnAddPatient.setOnClickListener {
            if (isFormValid()) {
                savePatient()
            }
        }
    }

    private fun setupWards() {
        if (allMunicipalities.size > 0) {
            val dbMunicipality = allMunicipalities[spinnerMunicipality.selectedItemPosition]
            Log.d("Selected Municipality: ", spinnerMunicipality.selectedItem.toString())
            Log.d("Municipality Position: ", spinnerMunicipality.selectedItemPosition.toString())
            val dbWards =
                wardsBox.query().equal(Ward_.municipalityId, dbMunicipality.id).build().find()
            val wards = mutableListOf<String>()
            allWards = dbWards
            for (ward in dbWards) {
                wards.add(ward.ward.toString())
            }
            spinnerWard.adapter = AdapterHelper.createAdapter(context, wards.toList())
        } else {
            Toast.makeText(context, "Municipality not found.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupMunicipalities() {
        Log.d("Selected District", spinnerDistrict.selectedItem.toString())
        Log.d("District Position", spinnerDistrict.selectedItemPosition.toString())
        val dbDistrict = allDistricts[spinnerDistrict.selectedItemPosition]
        allMunicipalities =
            municipalitiesBox.query().equal(Municipality_.districtId, dbDistrict.id).build().find()
        val municipalitiesList = mutableListOf<String>()
        for (municipality in allMunicipalities) {
            municipalitiesList.add(municipality.name.capitalize())
        }
        spinnerMunicipality.adapter =
            AdapterHelper.createAdapter(context, municipalitiesList.toList())
        setupWards()
    }

    private fun setupDistricts() {
        allDistricts = districtsBox.query().build().find()
        val districtsList = mutableListOf<String>()
        for (district in allDistricts) {
            districtsList.add(district.name.capitalize())
        }
        spinnerDistrict.adapter = AdapterHelper.createAdapter(context, districtsList.toList())
        setupMunicipalities()
    }

    private fun updateUI() {
        if (patient != null) {
            title = patient!!.fullName()
            etFirstName.setText(patient!!.first_name)
            etMiddleName.setText(patient!!.middle_name)
            etLastName.setText(patient!!.last_name)
            etPhone.setText(patient!!.phone)
//            etStreetAddress.setText(patient!!.street_address)
//            etCity.setText(patient!!.city)
//            etState.setText(patient!!.state)
//            etCountry.setText(patient!!.country)
            spinnerGender.setSelection(resources.getStringArray(R.array.gender_list).indexOf(patient!!.gender))
            spinnerEducationLevel.setSelection(
                resources.getStringArray(R.array.education_level_list).indexOf(
                    patient!!.education
                )
            )
        }
    }

    @AddTrace(name = "savePatientAddPatientActivity", enabled = true /* optional */)
    private fun savePatient() {
        Log.d(TAG, "savePatient()")
        loading.visibility = View.VISIBLE
        tvErrorMessage.visibility = View.GONE
        val patient = createPatient()
        saveToLocalDB(patient)
        loading.visibility = View.GONE
    }

    @AddTrace(name = "createPatientAddPatientActivity", enabled = true /* optional */)
    private fun createPatient(): Patient {
        Log.d(TAG, "createPatient()")

        val dbDistrict = allDistricts[spinnerDistrict.selectedItemPosition]
        val dbMunicipality = allMunicipalities[spinnerMunicipality.selectedItemPosition]
        val dbWard = allWards[spinnerWard.selectedItemPosition]



        val id: Long = 0
        val firstName = etFirstName.text.toString()
        val middleName = etMiddleName.text.toString()
        val lastName = etLastName.text.toString()
        val gender = spinnerGender.selectedItem.toString()
        val dob = getFormattedDob()
        val phone = etPhone.text.toString()
        val education = spinnerEducationLevel.selectedItem.toString()
        val ward = dbWard.remote_id
        val municipality = dbMunicipality.remote_id
        val district = dbDistrict.remote_id
        val geography = DentalApp.geography_id
        val activity = DentalApp.activity_id
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
            patient!!.ward = ward
            patient!!.municipality = municipality
            patient!!.district = district
            patient!!.latitude = latitude
            patient!!.longitude = longitude
            patient!!.geography_id = DentalApp.geography_id
            patient!!.activityarea_id = DentalApp.activity_id
            patient!!.created_at = date
            patient!!.updated_at = date
            patient!!.updated = true
            return patient!!
        } else {
            val tempPatient = Patient();
            tempPatient.id = id
            tempPatient.remote_id = ""
            tempPatient.first_name = firstName
            tempPatient.middle_name = middleName
            tempPatient.last_name = lastName
            tempPatient.gender = gender
            tempPatient.dob = dob
            tempPatient.phone = phone
            tempPatient.education = education
            tempPatient.ward = ward
            tempPatient.municipality = municipality
            tempPatient.district = district
            tempPatient.latitude = latitude
            tempPatient.longitude = longitude
            tempPatient.geography_id = geography
            tempPatient.activityarea_id = activity
            tempPatient.created_at = date
            tempPatient.updated_at = date
            tempPatient.uploaded = false
            tempPatient.updated = false
            tempPatient.recall = null
            return tempPatient

        }
    }

    private fun getFormattedDob(): String {
        val dobYear = spinnerDobYear.selectedItem.toString()
        val dobMonth = DecimalFormat("00").format(spinnerDobMonth.selectedItem)
        val dobDay = DecimalFormat("00").format(spinnerDobDay.selectedItem)
        return "$dobYear-$dobMonth-$dobDay"
    }

    @AddTrace(name = "saveToLocalDBAddPatientActivity", enabled = true /* optional */)
    private fun saveToLocalDB(patient: Patient) {
        Log.d(TAG, "saveToLocalDB")
        patientsBox.put(patient)
        val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
        viewPatientIntent.putExtra("patient", patient)
        if (action.equals("new")) {
            startActivity(viewPatientIntent)
        } else {
            finish()
        }
    }

    @AddTrace(name = "isFormValidAddPatientActivity", enabled = true /* optional */)
    private fun isFormValid(): Boolean {
        tvErrorMessage.visibility = View.GONE

        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val phone = etPhone.text.toString()
        val dob = getFormattedDob()

        if(spinnerDistrict.selectedItem == null) {
            tvErrorMessage.text = "District is not selected."
            tvErrorMessage.visibility = View.VISIBLE
        }

        if(spinnerMunicipality.selectedItem == null) {
            tvErrorMessage.text = "Municipality is not selected."
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }

        if(spinnerWard.selectedItem == null) {
            tvErrorMessage.text = "Ward is not selected."
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }

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
        if (phone.isBlank() || phone.isEmpty()) {
            tvErrorMessage.text = resources.getString(R.string.phone_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (phone.length < 5) {
            tvErrorMessage.text = resources.getString(R.string.valid_phone_number_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
        if (!DateValidator.isValid(dob)) {
            tvErrorMessage.text = resources.getString(R.string.valid_date_is_required)
            tvErrorMessage.visibility = View.VISIBLE
            return false
        }
//        val dbMunicipality = allMunicipalities[spinnerMunicipality.selectedItemPosition]
//        val dbWard = allWards[spinnerWard.selectedItemPosition]
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
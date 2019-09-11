package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.abhiyantrik.dentalhub.entities.*
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.District
import com.abhiyantrik.dentalhub.models.Patient as PatientModel
import com.abhiyantrik.dentalhub.models.Profile
import com.abhiyantrik.dentalhub.utils.DateHelper
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetupActivity : AppCompatActivity() {

    private val TAG = "SetupActivity"
    private lateinit var tvMessage: TextView

    private lateinit var districtsBox: Box<com.abhiyantrik.dentalhub.entities.District>
    private lateinit var municipalitiesBox: Box<Municipality>
    private lateinit var patientsBox: Box<com.abhiyantrik.dentalhub.entities.Patient>
    private lateinit var wardsBox: Box<Ward>
    var allDistricts = listOf<District>()

    private lateinit var context: Context
    var profileLoadComplete = false
    var dataLoadComplete = false
    var patientDataLoadComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        context = this
        initUI()
        loadProfile()

    }

    private fun loadProfile() {
        tvMessage.text = tvMessage.text.toString() + "Loading profile...\n"
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.fetchProfile("JWT $token")
        call.enqueue(object : Callback<Profile> {
            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                if (BuildConfig.DEBUG) {
                    tvMessage.text = tvMessage.text.toString() + t.message.toString()
                } else {
                    tvMessage.text = tvMessage.text.toString() + "Failed to load profile\n"
                }
            }

            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                Log.d("SetupActivity", response.code().toString())
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val p = response.body() as Profile
                            DentalApp.fullName = p.fullName()
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_PROFILE_FULL_NAME,
                                p.fullName()
                            )
                            DentalApp.saveToPreference(context, Constants.PREF_PROFILE_ID, p.id)
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_PROFILE_IMAGE,
                                p.image
                            )
                            profileLoadComplete = true
                            tvMessage.text = tvMessage.text.toString() + "Loading profile complete\n"
                            loadData()
                        }
                    }
                } else {
                    Log.d("SetupActivity", "response failed")
                }
            }

        })
    }

    private fun loadData() {

        Log.d(TAG, "listAddressess()")
        tvMessage.text = tvMessage.text.toString() + "Loading addresses...\n"
        val panelService = DjangoInterface.create(this)
        val call = panelService.listAddresses()
        call.enqueue(object : Callback<List<District>> {
            override fun onFailure(call: Call<List<District>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                tvMessage.text = tvMessage.text.toString() + "Failed to load adresses\n"
                Log.d(TAG, t.toString())
            }

            override fun onResponse(
                call: Call<List<District>>,
                response: Response<List<District>>
            ) {
                Log.d(TAG, "onResponse()")
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            allDistricts = response.body() as List<District>

                            for (district in allDistricts) {
                                if (districtsBox.query().equal(
                                        District_.name,
                                        district.name
                                    ).build().count() == 0.toLong()
                                ) {
                                    val newDistrict = com.abhiyantrik.dentalhub.entities.District()
                                    newDistrict.remote_id = district.id
                                    newDistrict.name = district.name
                                    districtsBox.put(newDistrict)
                                }


                                for (municipality in district.municipalities) {
                                    val dbDistrict =
                                        districtsBox.query().orderDesc(District_.id).build()
                                            .findFirst()
                                    if (municipalitiesBox.query().equal(
                                            Municipality_.name,
                                            municipality.name
                                        ).build().count() == 0.toLong()
                                    ) {
                                        val newMunicipality = Municipality()
                                        newMunicipality.remote_id = municipality.id
                                        newMunicipality.name = municipality.name
                                        newMunicipality.district?.target = dbDistrict
                                        municipalitiesBox.put(newMunicipality)
                                        for (ward in municipality.wards) {
                                            val dbMunicipality = municipalitiesBox.query()
                                                .orderDesc(Municipality_.id).build().findFirst()
                                            val newWard = Ward()
                                            newWard.remote_id = ward.id
                                            newWard.ward = ward.ward
                                            newWard.name =
                                                dbMunicipality!!.name + "-" + ward.ward.toString() + ", " + dbDistrict!!.name
                                            newWard.municipality?.target = dbMunicipality
                                            wardsBox.put(newWard)
                                        }
                                    }
                                }
                            }
                            tvMessage.text =
                                tvMessage.text.toString() + "Loading address complete\n"
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_SETUP_COMPLETE,
                                "true"
                            )
                            dataLoadComplete = true
                            loadPatientData()
                        }
                    }
                } else {
                    Log.d(TAG, response.code().toString())
                }
            }

        })

    }

    private fun loadPatientData() {
        tvMessage.text = tvMessage.text.toString() + "Loading patients...\n"
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.getPatients("JWT $token")
        call.enqueue(object: Callback<List<PatientModel>>{
            override fun onFailure(call: Call<List<PatientModel>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                tvMessage.text = tvMessage.text.toString() + "Failed to load patients\n"
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<List<PatientModel>>, response: Response<List<PatientModel>>) {
                if(null!= response.body()){
                    Log.d("SetupActivity", response.code().toString())
                    when(response.code()){
                        200 -> {
                            patientDataLoadComplete = true
                            val allPatients = response.body() as List<PatientModel>
                            for (patient in allPatients){
                                val existingPatient =patientsBox.query().equal(Patient_.remote_id, patient.id.toString()).build().findFirst()
                                if(existingPatient != null){
                                    Log.d("SetupActivity", existingPatient.fullName()+" already exists.")
                                    tvMessage.text = tvMessage.text.toString() + existingPatient.fullName()+" already exists.\n"
                                }else{
                                    val patientEntity = Patient()
                                    patientEntity.remote_id = patient.id.toString()
                                    patientEntity.first_name = patient.first_name
                                    patientEntity.middle_name = patient.middle_name
                                    patientEntity.last_name = patient.last_name
                                    patientEntity.gender = patient.gender
                                    patientEntity.dob = patient.dob
                                    patientEntity.phone = patient.phone
                                    patientEntity.education = patient.education
                                    patientEntity.ward = patient.ward
                                    patientEntity.municipality = patient.municipality
                                    patientEntity.district = patient.district
                                    patientEntity.latitude = patient.latitude
                                    patientEntity.longitude = patient.longitude
                                    patientEntity.geography_id = patient.geography
                                    patientEntity.activityarea_id = patient.activity_area
                                    patientEntity.uploaded = true
                                    patientEntity.updated = false
                                    patientEntity.recall = null
                                    patientEntity.author = patient.author

                                    if(patient.created_at.isBlank()){
                                        patientEntity.created_at = DateHelper.getCurrentNepaliDate()
                                    }else{
                                        patientEntity.created_at = patient.created_at
                                    }
                                    if(patient.updated_at.isBlank()){
                                        patientEntity.updated_at = DateHelper.getCurrentNepaliDate()
                                    }else{
                                        patientEntity.updated_at = patient.updated_at
                                    }
                                    patientEntity.updated_by = patient.updated_by

                                    patientsBox.put(patientEntity)
                                    tvMessage.text = tvMessage.text.toString() + patient.fullName()+" downloaded.\n"
                                }

                            }
                            tvMessage.text = tvMessage.text.toString() + "Loading patients complete\n"
                            if(patientDataLoadComplete && profileLoadComplete && dataLoadComplete){
                                startActivity(Intent(context, LocationSelectorActivity::class.java))
                                finish()
                            }
                        }
                    }
                }else{
                    Log.d(TAG, response.code().toString())
                }
            }

        })

    }

    private fun initUI() {
        tvMessage = findViewById(R.id.tvMessage)

        districtsBox =
            ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.District::class.java)
        municipalitiesBox = ObjectBox.boxStore.boxFor(Municipality::class.java)
        wardsBox = ObjectBox.boxStore.boxFor(Ward::class.java)
        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)

    }
}

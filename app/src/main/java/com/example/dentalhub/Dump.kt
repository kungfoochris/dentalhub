package com.example.dentalhub

import android.content.Intent
import android.util.Log
import android.view.View
import com.example.dentalhub.entities.Patient
import com.example.dentalhub.interfaces.DjangoInterface
import com.google.firebase.perf.metrics.AddTrace
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


//@AddTrace(name = "saveToServerAddPatientActivity", enabled = true /* optional */)
//private fun saveToServer(patient: Patient) {
//    Log.d(TAG, "saveToServer()")
//    Log.d(TAG, patient.toString())
//    val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
//    val panelService = DjangoInterface.create(this)
//    val call = panelService.addPatient(
//        "JWT $token",
//        patient.id,
//        patient.first_name,
//        patient.last_name,
//        patient.gender,
//        patient.phone,
//        patient.middle_name,
//        patient.dob,
//        patient.education,
//        patient.street_address,
//        patient.ward,
//        patient.city,
//        patient.state,
//        patient.country,
//        patient.latitude,
//        patient.longitude
//    )
//    call.enqueue(object : Callback<Patient> {
//        override fun onFailure(call: Call<Patient>, t: Throwable) {
//            Log.d("onFailure", t.toString())
//            tvErrorMessage.text = t.message.toString()
//            tvErrorMessage.visibility = View.VISIBLE
//            loading.visibility = View.GONE
//        }
//
//        override fun onResponse(call: Call<Patient>, response: Response<Patient>) {
//            if (null != response.body()) {
//                when (response.code()) {
//                    200 -> {
//                        val tempPatient = response.body() as Patient
//                        val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
//                        viewPatientIntent.putExtra("patient", tempPatient)
//                        startActivity(viewPatientIntent)
//                    }
//                    400 -> {
//                        tvErrorMessage.text = getString(R.string.error_http_400)
//                        tvErrorMessage.visibility = View.VISIBLE
//                    }
//                    404 -> {
//                        tvErrorMessage.text = getString(R.string.error_http_404)
//                        tvErrorMessage.visibility = View.VISIBLE
//                    }
//                    else -> {
//                        tvErrorMessage.text = getString(R.string.error_http_500)
//                        tvErrorMessage.visibility = View.VISIBLE
//                    }
//                }
//                loading.visibility = View.GONE
//            } else {
//                Log.d(TAG, response.code().toString())
//                Log.d(TAG, Gson().toJson(response.body()).toString())
//                tvErrorMessage.text = response.message()
//                tvErrorMessage.visibility = View.VISIBLE
//                loading.visibility = View.GONE
//            }
//        }
//    })
//}


//@AddTrace(name = "listPatientsFromServerMainActivity", enabled = true /* optional */)
//private fun listPatientsFromServer() {
//    Log.d(TAG, "listPatientsFromServer")
//    val token = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
//    val panelService = DjangoInterface.create(this)
//    val call = panelService.listPatients("JWT $token")
//    call.enqueue(object : Callback<List<Patient>> {
//        override fun onFailure(call: Call<List<Patient>>, t: Throwable) {
//            Log.d(TAG, "onFailure()")
//            Log.d("onFailure", t.toString())
//        }
//
//        override fun onResponse(call: Call<List<Patient>>, response: Response<List<Patient>>) {
//            Log.d(TAG, "onResponse()")
//            if (null != response.body()) {
//                when (response.code()) {
//                    200 -> {
//                        allPatients = response.body() as List<Patient>
//                        setupAdapter()
//                    }
//                }
//            }
//        }
//
//    })
//
//}
//



//         fun checkPlayServices(context: Context): Boolean {
//            var apiAvailability = GoogleApiAvailability.getInstance();
//            val resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
//
//            if (resultCode != ConnectionResult.SUCCESS) {
//                if (apiAvailability.isUserResolvableError(resultCode)) {
//                    apiAvailability.getErrorDialog(context as Activity?, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
//                } else {
//                    false
//                }
//
//                return false
//            }
//
//            return true
//        }
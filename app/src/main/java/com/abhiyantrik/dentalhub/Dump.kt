package com.abhiyantrik.dentalhub


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
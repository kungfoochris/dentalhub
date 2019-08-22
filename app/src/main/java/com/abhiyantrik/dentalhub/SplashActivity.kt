package com.abhiyantrik.dentalhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.abhiyantrik.dentalhub.services.BootstrapService
import com.google.firebase.perf.metrics.AddTrace

class SplashActivity : Activity() {
    private lateinit var context: Context

    @AddTrace(name = "onCreateTraceSplashActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        context = this

        startService(Intent(this, BootstrapService::class.java))

        Handler().postDelayed({
            val token: String = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN, "")
            val email: String = DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL, "")
            val password: String = DentalApp.readFromPreference(context, Constants.PREF_AUTH_PASSWORD, "")
            if (token.isEmpty() || email.isEmpty() || password.isEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                val setupComplete = DentalApp.readFromPreference(context, Constants.PREF_SETUP_COMPLETE,"false")
                if(setupComplete.equals("true")) {
                    val selectedLocation_id = DentalApp.readFromPreference(context, Constants.PREF_SELECTED_LOCATION_ID, "")
                    val selectedLocation_name = DentalApp.readFromPreference(context, Constants.PREF_SELECTED_LOCATION_NAME, "")
                    val selectedActivity_name = DentalApp.readFromPreference(context, Constants.PREF_ACTIVITY_NAME, "")
                    val selectedActivity_id = DentalApp.readFromPreference(context, Constants.PREF_ACTIVITY_ID, "")
                    val remarks = DentalApp.readFromPreference(context, Constants.PREF_ACTIVITY_REMARKS, "")
//                    if(DentalApp.geography.isEmpty() || DentalApp.activity.isEmpty()){
//                        startActivity(Intent(this, LocationSelectorActivity::class.java))
//                    }
                    if(selectedLocation_id.isNullOrEmpty() || selectedActivity_id.isNullOrEmpty()){
                        startActivity(Intent(this, LocationSelectorActivity::class.java))
                    } else {
                        DentalApp.geography_id = selectedLocation_id
                        DentalApp.geography_name = selectedLocation_name
                        DentalApp.activity_id = selectedActivity_id
                        DentalApp.activity_name = selectedActivity_name
                        DentalApp.activityRemarks = remarks
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }else{
                    startActivity(Intent(this, SetupActivity::class.java))
                }

            }

        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }


}

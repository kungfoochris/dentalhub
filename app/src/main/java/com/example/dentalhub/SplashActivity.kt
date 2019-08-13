package com.example.dentalhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.example.dentalhub.services.BootstrapService
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
                DentalApp.geography = DentalApp.readIntFromPreference(context, Constants.PREF_SELECTED_LOCATION).toString()
                DentalApp.activity = DentalApp.readFromPreference(context, Constants.PREF_ACTIVITY_NAME, "")
                DentalApp.activityRemarks = DentalApp.readFromPreference(context, Constants.PREF_ACTIVITY_REMARKS, "")
                if(DentalApp.geography.isEmpty() || DentalApp.activity.isEmpty() || DentalApp.activityRemarks.isEmpty()){
                    startActivity(Intent(this, LocationSelectorActivity::class.java))
                }else{
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }

        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }


}

package com.example.dentalhub

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
                startActivity(Intent(this, SelectorActivity::class.java))
            }

        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }


}

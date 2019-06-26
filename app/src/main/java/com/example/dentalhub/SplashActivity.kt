package com.example.dentalhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class SplashActivity : Activity() {
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        context = this

        Handler().postDelayed({
            val token: String = DentalApp.readFromPreference(context, Constants.PREF_AUTH_TOKEN,"")
            val email: String = DentalApp.readFromPreference(context, Constants.PREF_AUTH_EMAIL, "")
            val password: String = DentalApp.readFromPreference(context, Constants.PREF_AUTH_PASSWORD, "")
            if(token.isEmpty() || email.isEmpty() || password.isEmpty()){
                startActivity(Intent(this,LoginActivity::class.java))
            }else{
                startActivity(Intent(this, MainActivity::class.java))
            }

        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

package com.example.dentalhub

import android.content.Context
import android.net.ConnectivityManager
import androidx.multidex.MultiDexApplication
import com.example.dentalhub.models.Location

class DentalApp : MultiDexApplication(){
    companion object Factory {
        private const val PREF_FILE_NAME = "dentalhub"
        var location: Location = Location("0", "0")
        fun saveToPreference(context: Context, preferenceName: String, preferenceValue: String) {
            val sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(preferenceName, preferenceValue)
            editor.apply()
        }

        fun removeFromPreference(context: Context, preferenceName: String) {
            val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.remove(preferenceName)
            editor.apply()

        }
        fun readFromPreference(context: Context, preferenceName: String, defaultValue: String): String{
            val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            return prefs.getString(preferenceName, defaultValue).toString()
        }
        fun hasAuthDetails(context: Context): Boolean{
            val email = readFromPreference(context, Constants.PREF_AUTH_EMAIL,"")
            val password = readFromPreference(context, Constants.PREF_AUTH_PASSWORD,"")
            val socialAuth = readFromPreference(context, Constants.PREF_AUTH_SOCIAL, "false")
            var status = false
            if(socialAuth == "true"){
                status = true
            }
            if(email.isNotEmpty() && password.isNotEmpty()){
                status = true
            }
            return status
        }

        fun clearAuthDetails(context: Context){
            saveToPreference(context, Constants.PREF_AUTH_TOKEN, "")
            saveToPreference(context, Constants.PREF_AUTH_EMAIL, "")
            saveToPreference(context, Constants.PREF_AUTH_PASSWORD, "")
            saveToPreference(context, Constants.PREF_AUTH_SOCIAL, "false")
        }

        fun isConnectedToWifi(context: Context): Boolean{
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return mWifi.isConnected

        }
    }
}
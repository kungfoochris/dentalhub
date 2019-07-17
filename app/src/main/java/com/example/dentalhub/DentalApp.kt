package com.example.dentalhub

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.multidex.MultiDexApplication
import com.example.dentalhub.models.Location
import com.example.dentalhub.utils.NotificationHelper


class DentalApp : MultiDexApplication() {


    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)

        context = applicationContext

        defaultChannelId = applicationContext.packageName + applicationContext.getString(R.string.app_name)
        syncChannelId = applicationContext.packageName + applicationContext.getString(R.string.app_name) + "-sync"

        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel."
        )

        NotificationHelper.createNotificationChannel(
            this,
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name) + "-sync", "Notification channel for sync service."
        )

    }


    companion object Factory {
        private const val PREF_FILE_NAME = "dentalhub"
        var location: Location = Location("0", "0")

        var geography: String = ""
        var activity: String = ""
        var defaultChannelId: String = ""
        var syncChannelId: String = ""
        lateinit var context: Context

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

        fun readFromPreference(context: Context, preferenceName: String, defaultValue: String): String {
            val prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            return prefs.getString(preferenceName, defaultValue).toString()
        }

        fun hasAuthDetails(context: Context): Boolean {
            val email = readFromPreference(context, Constants.PREF_AUTH_EMAIL, "")
            val password = readFromPreference(context, Constants.PREF_AUTH_PASSWORD, "")
            val socialAuth = readFromPreference(context, Constants.PREF_AUTH_SOCIAL, "false")
            var status = false
            if (socialAuth == "true") {
                status = true
            }
            if (email.isNotEmpty() && password.isNotEmpty()) {
                status = true
            }
            return status
        }

        fun clearAuthDetails(context: Context) {
            saveToPreference(context, Constants.PREF_AUTH_TOKEN, "")
            saveToPreference(context, Constants.PREF_AUTH_EMAIL, "")
            saveToPreference(context, Constants.PREF_AUTH_PASSWORD, "")
            saveToPreference(context, Constants.PREF_AUTH_SOCIAL, "false")
        }

        fun isConnectedToWifi(context: Context): Boolean {
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return mWifi.isConnected

        }

        fun displayNotification(id: Int, title: String, desc: String, longDesc: String) {
            val notificationBuilder = NotificationCompat.Builder(context, syncChannelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(desc)
                .setTicker(context.getString(R.string.sync_ticker))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(longDesc)
                )
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManager = NotificationManagerCompat.from(context)

            notificationManager.notify(id, notificationBuilder.build())
        }

        fun cancelNotification(id: Int) {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(id)
        }


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

    }
}
package com.abhiyantrik.dentalhub.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Activity
import com.abhiyantrik.dentalhub.entities.Activity_
import com.abhiyantrik.dentalhub.entities.Geography
import com.abhiyantrik.dentalhub.entities.Geography_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BootstrapService : Service() {
    private lateinit var activitiesBox: Box<Activity>
    private lateinit var geographiesBox: Box<Geography>
    val TAG = "BootstrapService"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        activitiesBox = ObjectBox.boxStore.boxFor(Activity::class.java)
        geographiesBox = ObjectBox.boxStore.boxFor(Geography::class.java)

        //listGeographies()
        listActivities()
        return super.onStartCommand(intent, flags, startId)
    }

//    private fun listGeographies() {
//        Log.d(TAG, "listGeographies()")
//        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
//        val panelService = DjangoInterface.create(this)
//        val call = panelService.listGeographies("JWT $token")
//        call.enqueue(object : Callback<List<Geography>> {
//            override fun onFailure(call: Call<List<Geography>>, t: Throwable) {
//                Log.d(TAG, "onFailure()")
//                Log.d(TAG, t.toString())
//            }
//
//            override fun onResponse(call: Call<List<Geography>>, response: Response<List<Geography>>) {
//                Log.d(TAG, "onResponse()")
//                if (null != response.body()) {
//                    when (response.code()) {
//                        200 -> {
//                            val allGeographies: List<Geography> = response.body() as List<Geography>
//                            for (geography in allGeographies) {
//                                val a =
//                                    geographiesBox.query().equal(Geography_.street_address, geography.street_address)
//                                        .equal(Geography_.city, geography.city).build().findFirst()
//                                if (a == null) {
//                                    geography.remote_id = geography.id
//                                    geography.id = 0
//                                    geographiesBox.put(geography)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//        })
//
//    }

    private fun listActivities() {
        Log.d(TAG, "listActivities()")
        val panelService = DjangoInterface.create(this)
        val call = panelService.listActivities()
        call.enqueue(object : Callback<List<Activity>> {
            override fun onFailure(call: Call<List<Activity>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<List<Activity>>, response: Response<List<Activity>>) {
                Log.d(TAG, "onResponse()")
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val allActivities: List<Activity> = response.body() as List<Activity>
                            for (activity in allActivities) {
                                val a = activitiesBox.query().equal(Activity_.name, activity.name).build().findFirst()
                                if (a == null) {
                                    activity.remote_id = activity.id
                                    activity.id = 0
                                    activitiesBox.put(activity)
                                }

                            }
                        }
                    }
                }
            }

        })
    }
}
package com.example.dentalhub.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.dentalhub.DentalApp
import com.example.dentalhub.utils.GpsUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import java.util.*


class LocationTrackerService: Service(){
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    var isGPS: Boolean = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(10 * 1000) // 10 seconds
        locationRequest.setFastestInterval(5 * 1000) // 5 seconds

//        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener{
//            override fun gpsStatus(isGPSEnable: Boolean) {
//                // turn on GPS
//                isGPS = isGPSEnable
//            }
//        })
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        DentalApp.location.latitude = location.latitude.toString()
                        DentalApp.location.longitude = location.longitude.toString()
                        Log.d("Location", DentalApp.location.toString())
                    }
                }
            }
        }
        val handler = Handler()
        val delay = 15*1000 //milliseconds

        handler.postDelayed(object : Runnable {
            override fun run() {
                Log.d("Location", DentalApp.location.toString())
                mFusedLocationClient.lastLocation.addOnSuccessListener {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                }

                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())


        return super.onStartCommand(intent, flags, startId)
    }
}
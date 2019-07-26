package com.example.dentalhub

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.entities.Activity
import com.example.dentalhub.entities.Geography
import com.example.dentalhub.utils.AdapterHelper
import io.objectbox.Box
import io.objectbox.query.Query

class SelectorActivity : AppCompatActivity() {

    // lists for permissions
    private var permissionsToRequest: java.util.ArrayList<String>? = null
    private val permissionsRejected = java.util.ArrayList<String>()
    private val permissions = java.util.ArrayList<String>()


    private lateinit var spinnerLocation: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var btnGo: Button
    private lateinit var btnLogout: Button

    private lateinit var activitiesBox: Box<Activity>
    private lateinit var geographiesBox: Box<Geography>
    private lateinit var activitiesQuery: Query<Activity>
    private lateinit var geographiesQuery: Query<Geography>

    private lateinit var context: Context
    var allGeographies = mutableListOf<Geography>()
    var allActivities = mutableListOf<Activity>()

    private var geographies = mutableListOf<String>()
    private val TAG = "selectorActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this

        // we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionsToRequest = permissionsToRequest(permissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionsToRequest!!.size > 0) {
            requestPermissions(permissionsToRequest!!.toTypedArray(), ALL_PERMISSIONS_RESULT)
        }

        initUI()
    }

    private fun initUI() {
        activitiesBox = ObjectBox.boxStore.boxFor(Activity::class.java)
        geographiesBox = ObjectBox.boxStore.boxFor(Geography::class.java)

        activitiesQuery = activitiesBox.query().build()
        geographiesQuery = geographiesBox.query().build()

        spinnerLocation = findViewById(R.id.spinnerLocation)
        spinnerActivity = findViewById(R.id.spinnerActivity)
        btnGo = findViewById(R.id.btnGo)
        btnLogout = findViewById(R.id.btnLogout)

        setupActivities()
        setupGeographies()

        btnGo.setOnClickListener {

            if (geographies.size > 0) {

                DentalApp.geography = getGeographyId(spinnerLocation.selectedItem.toString())
                DentalApp.activity = getActivityId(spinnerActivity.selectedItem.toString())

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(context, "You do not have permission to login to any location", Toast.LENGTH_LONG).show()
            }
        }
        btnLogout.setOnClickListener {
            DentalApp.clearAuthDetails(context)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getActivityId(activityName: String): String {
        for (activity in allActivities) {
            if (activity.name.equals(activityName)) {
                return activity.remote_id.toString()
            }
        }
        return ""
    }

    private fun getGeographyId(address: String): String {
        for (geography in allGeographies) {
            if (geography.address().equals(address)) {
                return geography.remote_id.toString()
            }
        }
        return ""
    }

    private fun setupActivities() {
        allActivities = activitiesQuery.find()
        val activities = mutableListOf<String>()
        for (activity in allActivities) {
            activities.add(activity.name)
        }

        spinnerActivity.adapter = AdapterHelper.createAdapter(context, activities)
    }

    private fun setupGeographies() {
        allGeographies = geographiesQuery.find()
        geographies = mutableListOf<String>()
        for (geography in allGeographies) {
            geographies.add(geography.address())
        }
        if (geographies.size > 0) {
            spinnerLocation.adapter = AdapterHelper.createAdapter(context, geographies)
        } else {
            Toast.makeText(context, "You do not have permission to login to any location", Toast.LENGTH_LONG).show()
        }

    }

    private fun permissionsToRequest(wantedPermissions: java.util.ArrayList<String>): java.util.ArrayList<String> {
        val result = java.util.ArrayList<String>()

        for (perm in wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }

        return result
    }

    private fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else true

    }


    companion object {
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private const val UPDATE_INTERVAL: Long = 5000
        private const val FASTEST_INTERVAL: Long = 5000 // = 5 seconds
        // integer for permissions results request
        private const val ALL_PERMISSIONS_RESULT = 1011
    }
}

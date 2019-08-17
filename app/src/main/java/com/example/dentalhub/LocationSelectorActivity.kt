package com.example.dentalhub

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.adapters.GeographyAdapter
import com.example.dentalhub.entities.*
import com.example.dentalhub.models.Geography
import com.example.dentalhub.models.District as DistrictModel
import com.example.dentalhub.models.Municipality as MunicipalityModel
import com.example.dentalhub.models.Ward as WardModel
import com.example.dentalhub.utils.RecyclerViewItemSeparator
import io.objectbox.Box


class LocationSelectorActivity : AppCompatActivity() {

    // lists for permissions
    private var permissionsToRequest: java.util.ArrayList<String>? = null
    private val permissionsRejected = java.util.ArrayList<String>()
    private val permissions = java.util.ArrayList<String>()


    private lateinit var recyclerView: RecyclerView
    private lateinit var btnLogout: Button

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var context: Context
    var allGeographies = mutableListOf<Geography>()

    private lateinit var geographyAdapter: GeographyAdapter
    private val TAG = "selectorActivity"


    private lateinit var districtsBox: Box<District>
    private lateinit var municipalitiesBox: Box<Municipality>
    private lateinit var wardsBox: Box<Ward>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_selector)
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

        recyclerView = findViewById(R.id.recyclerView)
        btnLogout = findViewById(R.id.btnLogout)

        wardsBox= ObjectBox.boxStore.boxFor(Ward::class.java)

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(20)
        recyclerView.addItemDecoration(divider)

        listAddressess()

        btnLogout.setOnClickListener {
            DentalApp.clearAuthDetails(context)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun listAddressess() {
        val allWards = wardsBox.query().build().find()
        for(ward in allWards){
            val geography = Geography(ward.id.toInt(),ward.name.split(' ').joinToString { it.capitalize()  })
            allGeographies.add(geography)
            setupAdapter()
        }
    }


    private fun setupAdapter() {

        geographyAdapter = GeographyAdapter(context, allGeographies, object : GeographyAdapter.GeographyClickListener {
            override fun onGeographyClick(geography: Geography) {
                DentalApp.saveIntToPreference(context, Constants.PREF_SELECTED_LOCATION, geography.id)
                startActivity(Intent(context, ActivitySelectorActivity::class.java))
                finish()
            }
        })
        recyclerView.adapter = geographyAdapter
        geographyAdapter.notifyDataSetChanged()
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

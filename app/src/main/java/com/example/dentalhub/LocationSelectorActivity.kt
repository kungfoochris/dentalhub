package com.example.dentalhub

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dentalhub.adapters.GeographyAdapter
import com.example.dentalhub.interfaces.DjangoInterface
import com.example.dentalhub.models.Geography
import com.example.dentalhub.utils.RecyclerViewItemSeparator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    var allGeographies = listOf<Geography>()
    private var geographies = mutableListOf<String>()
    private lateinit var geographyAdapter: GeographyAdapter
    private val TAG = "selectorActivity"

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

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(20)
        recyclerView.addItemDecoration(divider)

        listGeographies()

        btnLogout.setOnClickListener {
            DentalApp.clearAuthDetails(context)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }



    private fun listGeographies() {
        Log.d(TAG, "listGeographies()")
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(this)
        val call = panelService.listGeographies("JWT $token")
        call.enqueue(object : Callback<List<Geography>> {
            override fun onFailure(call: Call<List<Geography>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<List<Geography>>, response: Response<List<Geography>>) {
                Log.d(TAG, "onResponse()")
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            allGeographies = response.body() as List<Geography>
                            setupAdapter()
                        }
                    }
                }
            }

        })

    }

    private fun setupAdapter() {
        geographyAdapter = GeographyAdapter(context, allGeographies, object : GeographyAdapter.GeographyClickListener {
            override fun onGeographyClick(geography: Geography) {
                DentalApp.saveIntToPreference(context, "SELECTED_LOCATION", geography.id)
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

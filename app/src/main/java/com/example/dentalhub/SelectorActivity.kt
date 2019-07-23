package com.example.dentalhub

import android.content.Context
import android.content.Intent
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
    private lateinit var spinnerLocation: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var btnGo: Button
    private lateinit var btnLogout: Button

    private lateinit var activitiesBox: Box<Activity>
    private lateinit var geographiesBox: Box<Geography>
    private lateinit var activitiesQuery: Query<Activity>
    private lateinit var geographiesQuery: Query<Geography>

    private lateinit var context: Context

    private var geographies = mutableListOf<String>()
    private val TAG = "selectorActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        context = this
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

                DentalApp.geography = spinnerLocation.selectedItem.toString()
                DentalApp.activity = spinnerActivity.selectedItem.toString()

                startActivity(Intent(this, MainActivity::class.java))
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

    private fun setupActivities() {
        val allActivities = activitiesQuery.find()
        val activities = mutableListOf<String>()
        for (activity in allActivities) {
            activities.add(activity.name)
        }

        spinnerActivity.adapter = AdapterHelper.createAdapter(context, activities)
    }

    private fun setupGeographies() {
        val allGeographies = geographiesQuery.find()
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


    override fun onPause() {
        super.onPause()
        finish()
    }
}

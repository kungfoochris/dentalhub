package com.example.dentalhub

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import com.example.dentalhub.entities.Activity
import com.example.dentalhub.entities.Geography
import com.example.dentalhub.utils.AdapterHelper
import io.objectbox.Box
import io.objectbox.query.Query

class SelectorActivity : AppCompatActivity() {
    private lateinit var spinnerLocation: Spinner
    private lateinit var spinnerActivity: Spinner
    private lateinit var btnGo: Button

    private lateinit var activitiesBox: Box<Activity>
    private lateinit var geographiesBox: Box<Geography>
    private lateinit var activitiesQuery: Query<Activity>
    private lateinit var geographiesQuery: Query<Geography>

    private lateinit var context: Context

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

        setupActivities()
        setupGeograhies()

        btnGo.setOnClickListener {
            DentalApp.geography = spinnerLocation.selectedItem.toString()
            DentalApp.activity = spinnerActivity.selectedItem.toString()

            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun setupActivities() {
        val allActivities = activitiesQuery.find()
        val activities = mutableListOf<String>()
        for(activity in allActivities){
            activities.add(activity.name)
        }

        spinnerActivity.adapter = AdapterHelper.createAdapter(context, activities)
    }

    private fun setupGeograhies() {
        val allGeographies = geographiesQuery.find()
        val geographies = mutableListOf<String>()
        for(geography in allGeographies){
            geographies.add(geography.address())
        }
        spinnerLocation.adapter = AdapterHelper.createAdapter(context, geographies)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

package com.example.dentalhub

import android.content.Context
import android.os.Bundle
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.dentalhub.adapters.FormPageAdapter
import com.example.dentalhub.entities.Patient
import com.example.dentalhub.utils.AdapterHelper
import com.google.android.material.tabs.TabLayout
import com.google.firebase.perf.metrics.AddTrace

class AddEncounterActivity : AppCompatActivity(), TreatmentFragmentCommunicator{

    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var spinnerReason: Spinner
    private lateinit var patient: Patient

    private lateinit var context: Context

    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_encounter)

        context = this

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        patient = intent.getParcelableExtra("patient")
        title = patient.fullName()
        initUI()
    }

    @AddTrace(name = "initUITrace", enabled = true /* optional */)
    private fun initUI() {
        spinnerReason = findViewById(R.id.spinnerReason)
        pager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tabLayout)

        spinnerReason.adapter = AdapterHelper.createAdapter(context, resources.getStringArray(R.array.encounter_reason).toList())
        val fragmentAdapter = FormPageAdapter(supportFragmentManager)
        pager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(pager)

    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun goBack() {
        if(pager.currentItem == 0){
            pager.currentItem = 3
        }else{
            pager.currentItem -= 1
        }

    }

    override fun goForward() {
        if(pager.currentItem == 3){
            pager.currentItem = 0
        }else {
            pager.currentItem += 1
        }
    }
}

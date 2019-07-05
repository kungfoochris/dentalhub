package com.example.dentalhub

import android.content.Context
import android.os.Bundle
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.dentalhub.adapters.FormPageAdapter
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.entities.Encounter_
import com.example.dentalhub.entities.Patient
import com.example.dentalhub.utils.AdapterHelper
import com.google.android.material.tabs.TabLayout
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box

class AddEncounterActivity : AppCompatActivity(), TreatmentFragmentCommunicator{

    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var patient: Patient

    private lateinit var encounterBox: Box<Encounter>

    private lateinit var context: Context

    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_encounter)
        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        context = this

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        patient = intent.getParcelableExtra("patient")
        title = patient.fullName()
        val encounter = encounterBox.query().orderDesc(Encounter_.id).build().findFirst()

        initUI()
    }

    @AddTrace(name = "initUITrace", enabled = true /* optional */)
    private fun initUI() {

        pager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tabLayout)


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

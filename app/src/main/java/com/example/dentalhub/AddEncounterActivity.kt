package com.example.dentalhub

import android.content.Context
import android.os.Bundle
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.dentalhub.adapters.FormPageAdapter
import com.example.dentalhub.entities.*
import com.example.dentalhub.utils.AdapterHelper
import com.google.android.material.tabs.TabLayout
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box

class AddEncounterActivity : AppCompatActivity(), TreatmentFragmentCommunicator{

    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var patient: Patient

    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>

    private lateinit var context: Context

    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_encounter)

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        historyBox = ObjectBox.boxStore.boxFor(History::class.java)
        screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
        treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)

        context = this

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        patient = intent.getParcelableExtra("patient")
        title = patient.fullName()

        val encounter = encounterBox.query().orderDesc(Encounter_.id).build().findFirst()

        val history = History()
        history.encounter?.target = encounter
        historyBox.put(history)

        val screening = Screening()
        screening.encounter?.target = encounter
        screeningBox.put(screening)

        val treatment = Treatment()
        treatment.encounter?.target = encounter
        treatmentBox.put(treatment)

        val referral  = Referral()
        treatment.encounter?.target = encounter
        referralBox.put(referral)

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

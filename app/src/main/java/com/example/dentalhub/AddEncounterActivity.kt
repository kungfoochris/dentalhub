package com.example.dentalhub

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.dentalhub.adapters.FormPageAdapter
import com.example.dentalhub.entities.*
import com.example.dentalhub.fragments.interfaces.HistoryFormCommunicator
import com.example.dentalhub.fragments.interfaces.ReferralFormCommunicator
import com.example.dentalhub.fragments.interfaces.ScreeningFormCommunicator
import com.example.dentalhub.fragments.interfaces.TreatmentFormCommunicator
import com.google.android.material.tabs.TabLayout
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box

class AddEncounterActivity : AppCompatActivity(), TreatmentFragmentCommunicator, HistoryFormCommunicator, ScreeningFormCommunicator, TreatmentFormCommunicator, ReferralFormCommunicator {



    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var patient: Patient

    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>

    private lateinit var context: Context

    val history = History()
    val screening = Screening()
    val treatment = Treatment()
    val referral  = Referral()

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



        history.blood_disorder = false
        history.diabetes = false
        history.liver_problem = false
        history.rheumatic_fever = false
        history.seizuers_or_epilepsy = false
        history.hepatitis_b_or_c = false
        history.hiv = false
        history.other = ""
        history.no_underlying_medical_record = false
        history.not_taking_any_medications = false
        history.allergies = ""

        history.encounter?.target = encounter
        historyBox.put(history)



        screening.carries_risk = "low"
        screening.decayed_pimary_teeth = 0
        screening.decayed_permanent_teeth = 0
        screening.cavity_permanent_tooth = false
        screening.cavity_permanent_anterior = false
        screening.active_infection = false
        screening.need_art_filling = false
        screening.need_sealant = false
        screening.need_sdf = false
        screening.need_extraction = false

        screening.encounter?.target = encounter
        screeningBox.put(screening)


        treatment.encounter?.target = encounter

        treatment.fv_applied = false
        treatment.treatment_plan_complete = false
        treatment.notes = ""

        treatmentBox.put(treatment)


        treatment.encounter?.target = encounter

        referral.no_referral = false
        referral.health_post = false
        referral.hygienist = false
        referral.dentist = false
        referral.general_physician = false
        referral.other = false

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
    override fun updateHistory(bloodDisorders: Boolean,diabetes: Boolean,liverProblem: Boolean,
                               rheumaticFever: Boolean, seizuersOrEpilepsy: Boolean, hepatitisBOrC: Boolean,
                               hiv: Boolean, other: String, noUnderlyingMedicalRecord:Boolean,medications: String,
                               notTakingAnyMedications: Boolean, allergies: String) {
        history.blood_disorder = bloodDisorders
        history.diabetes = diabetes
        history.liver_problem = liverProblem
        history.rheumatic_fever = rheumaticFever
        history.seizuers_or_epilepsy = seizuersOrEpilepsy
        history.hepatitis_b_or_c = hepatitisBOrC
        history.hiv = hiv
        history.other = other
        history.medications = medications
        history.no_underlying_medical_record = noUnderlyingMedicalRecord
        history.not_taking_any_medications = notTakingAnyMedications
        history.allergies = allergies
        historyBox.put(history)
    }
    override fun updateScreening(
        carriesRisk: String,
        decayedPrimaryTeeth: String,
        decayedPermanentTeeth: String,
        cavityPermanentTooth: Boolean,
        cavityPermanentAnterior: Boolean,
        activeInfection: Boolean,
        needARTFilling: Boolean,
        needSealant: Boolean,
        needSDF: Boolean,
        needExtraction: Boolean
    ) {
        screening.carries_risk = carriesRisk
        try{
            screening.decayed_pimary_teeth = decayedPrimaryTeeth.toInt()
        }catch (e: NumberFormatException){
            screening.decayed_pimary_teeth = 0
        }
        try {
            screening.decayed_permanent_teeth = decayedPermanentTeeth.toInt()
        }catch(e: java.lang.NumberFormatException){

            screening.decayed_permanent_teeth = 0
        }
        screening.cavity_permanent_tooth = cavityPermanentTooth
        screening.cavity_permanent_anterior = cavityPermanentAnterior
        screening.active_infection = activeInfection
        screening.need_art_filling = needARTFilling
        screening.need_sealant = needSealant
        screening.need_sdf = needSDF
        screening.need_extraction = needExtraction

        screeningBox.put(screening)
    }

    override fun updateTreatment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateReferral(
        noReferral: Boolean,
        healthPost: Boolean,
        hygienist: Boolean,
        dentist: Boolean,
        generalPhysician: Boolean,
        other: Boolean
    ) {
        referral.no_referral = noReferral
        referral.health_post = healthPost
        referral.hygienist = hygienist
        referral.dentist = dentist
        referral.general_physician = generalPhysician
        referral.other = other

        referralBox.put(referral)
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

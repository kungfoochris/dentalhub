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

    var history = History()
    var screening = Screening()
    var treatment = Treatment()
    var referral  = Referral()
    var encounter = Encounter()

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

        encounter = encounterBox.query().orderDesc(Encounter_.id).build().findFirst()!!

        history.encounter?.target = encounter
        historyBox.put(history)


        screening.encounter?.target = encounter
        screeningBox.put(screening)

        treatment.encounter?.target = encounter
        treatmentBox.put(treatment)


        referral.encounter?.target = encounter
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

        history = historyBox.query().equal(History_.encounterId, encounter.id).orderDesc(History_.id).build().findFirst()!!

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

        screening = screeningBox.query().equal(Screening_.encounterId, encounter.id).orderDesc(Screening_.id).build().findFirst()!!

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
    override fun updateTreatment(
        notes: String,
        fvApplied: Boolean,
        treatmentPlanComplete: Boolean,
        teeth: Array<String>
    ) {
        treatment = treatmentBox.query().equal(Treatment_.encounterId, encounter.id).orderDesc(Treatment_.id).build().findFirst()!!

        treatment.fv_applied = fvApplied
        treatment.notes = notes
        treatment.treatment_plan_complete = treatmentPlanComplete

        treatment.tooth18 = teeth[0]
        treatment.tooth17 = teeth[1]
        treatment.tooth16 = teeth[2]
        treatment.tooth15 = teeth[3]
        treatment.tooth14 = teeth[4]
        treatment.tooth13 = teeth[5]
        treatment.tooth12 = teeth[6]
        treatment.tooth11 = teeth[7]

        treatment.tooth21 = teeth[8]
        treatment.tooth22 = teeth[9]
        treatment.tooth23 = teeth[10]
        treatment.tooth24 = teeth[11]
        treatment.tooth25 = teeth[12]
        treatment.tooth26 = teeth[13]
        treatment.tooth27 = teeth[14]
        treatment.tooth28 = teeth[15]

        treatment.tooth48 = teeth[16]
        treatment.tooth47 = teeth[17]
        treatment.tooth46 = teeth[18]
        treatment.tooth45 = teeth[19]
        treatment.tooth44 = teeth[20]
        treatment.tooth43 = teeth[21]
        treatment.tooth42 = teeth[22]
        treatment.tooth41 = teeth[23]

        treatment.tooth31 = teeth[24]
        treatment.tooth32 = teeth[25]
        treatment.tooth33 = teeth[26]
        treatment.tooth34 = teeth[27]
        treatment.tooth35 = teeth[28]
        treatment.tooth36 = teeth[29]
        treatment.tooth37 = teeth[30]
        treatment.tooth38 = teeth[31]

        treatmentBox.put(treatment)
    }

    override fun updateReferral(
        noReferral: Boolean,
        healthPost: Boolean,
        hygienist: Boolean,
        dentist: Boolean,
        generalPhysician: Boolean,
        other: Boolean
    ) {
        referral = referralBox.query().equal(Referral_.encounterId, encounter.id).orderDesc(Referral_.id).build().findFirst()!!

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
            onBackPressed()
        }else {
            pager.currentItem += 1
        }
    }
}

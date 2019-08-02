package com.example.dentalhub

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
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


class AddEncounterActivity : AppCompatActivity(), TreatmentFragmentCommunicator, HistoryFormCommunicator,
    ScreeningFormCommunicator, TreatmentFormCommunicator, ReferralFormCommunicator {


    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var patient: Patient

    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>
    private lateinit var recallBox: Box<Recall>

    private lateinit var context: Context

    private var history = History()
    private var screening = Screening()
    private var treatment = Treatment()
    private var referral = Referral()
    private var recall = Recall()
    private var encounter = Encounter()
    var encounterId: Long = 0

    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_encounter)

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        historyBox = ObjectBox.boxStore.boxFor(History::class.java)
        screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
        treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)
        recallBox = ObjectBox.boxStore.boxFor(Recall::class.java)

        context = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        patient = intent.getParcelableExtra("patient")
        title = patient.fullName()

        encounterId = intent.getLongExtra("ENCOUNTER_ID", "0".toLong())
        Log.d("encounterId", encounterId.toString())

        if (encounterId == "0".toLong()) {
            encounter = encounterBox.query().orderDesc(Encounter_.id).build().findFirst()!!

            history.encounter?.target = encounter
            historyBox.put(history)


            screening.encounter?.target = encounter
            screeningBox.put(screening)

            treatment.encounter?.target = encounter
            treatmentBox.put(treatment)


            referral.encounter?.target = encounter
            referralBox.put(referral)

            recall.encounter?.target = encounter
            recallBox.put(recall)

        } else {
            encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!

            history =
                historyBox.query().equal(
                    History_.encounterId,
                    encounter.id
                ).orderDesc(History_.id).build().findFirst()!!

            screening = screeningBox.query().equal(
                Screening_.encounterId,
                encounter.id
            ).orderDesc(Screening_.id).build().findFirst()!!

            treatment = treatmentBox.query().equal(
                Treatment_.encounterId,
                encounter.id
            ).orderDesc(Treatment_.id).build().findFirst()!!

            referral =
                referralBox.query().equal(
                    Referral_.encounterId,
                    encounter.id
                ).orderDesc(Referral_.id).build().findFirst()!!

            recall = recallBox.query().equal(Recall_.id, encounter.id).orderDesc(Recall_.id).build().findFirst()!!

        }

        initUI()


    }


    @AddTrace(name = "initUITrace", enabled = true /* optional */)
    private fun initUI() {

        pager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tabLayout)

        //pager.setOnTouchListener { _: View, _ -> true }
        pager.beginFakeDrag()

        val fragmentAdapter = FormPageAdapter(supportFragmentManager)
        pager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(pager)

    }

    override fun updateHistory(
        bloodDisorders: Boolean, diabetes: Boolean, liverProblem: Boolean,
        rheumaticFever: Boolean, seizuresOrEpilepsy: Boolean, hepatitisBOrC: Boolean,
        hiv: Boolean, other: String, noUnderlyingMedicalCondition: Boolean, medications: String,
        notTakingAnyMedications: Boolean, noAllergies: Boolean, allergies: String
    ) {

        history =
            historyBox.query().equal(History_.encounterId, encounter.id).orderDesc(History_.id).build().findFirst()!!

        history.blood_disorder = bloodDisorders
        history.diabetes = diabetes
        history.liver_problem = liverProblem
        history.rheumatic_fever = rheumaticFever
        history.seizuers_or_epilepsy = seizuresOrEpilepsy
        history.hepatitis_b_or_c = hepatitisBOrC
        history.hiv = hiv
        history.other = other
        history.medications = medications
        history.no_underlying_medical_condition = noUnderlyingMedicalCondition
        history.not_taking_any_medications = notTakingAnyMedications
        history.no_allergies = noAllergies
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

        screening = screeningBox.query().equal(
            Screening_.encounterId,
            encounter.id
        ).orderDesc(Screening_.id).build().findFirst()!!

        screening.carries_risk = carriesRisk
        try {
            screening.decayed_pimary_teeth = decayedPrimaryTeeth.toInt()
        } catch (e: NumberFormatException) {
            screening.decayed_pimary_teeth = 0
        }
        try {
            screening.decayed_permanent_teeth = decayedPermanentTeeth.toInt()
        } catch (e: java.lang.NumberFormatException) {

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
        treatment = treatmentBox.query().equal(
            Treatment_.encounterId,
            encounter.id
        ).orderDesc(Treatment_.id).build().findFirst()!!

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


        //primary teeth
        treatment.tooth51 = teeth[32]
        treatment.tooth52 = teeth[33]
        treatment.tooth53 = teeth[34]
        treatment.tooth54 = teeth[35]
        treatment.tooth55 = teeth[36]

        treatment.tooth61 = teeth[37]
        treatment.tooth62 = teeth[38]
        treatment.tooth63 = teeth[39]
        treatment.tooth64 = teeth[40]
        treatment.tooth65 = teeth[41]

        treatment.tooth81 = teeth[42]
        treatment.tooth82 = teeth[43]
        treatment.tooth83 = teeth[44]
        treatment.tooth84 = teeth[45]
        treatment.tooth85 = teeth[46]

        treatment.tooth71 = teeth[47]
        treatment.tooth72 = teeth[48]
        treatment.tooth73 = teeth[49]
        treatment.tooth74 = teeth[50]
        treatment.tooth75 = teeth[51]

        treatmentBox.put(treatment)
    }

    override fun updateReferral(
        noReferral: Boolean,
        healthPost: Boolean,
        hygienist: Boolean,
        dentist: Boolean,
        generalPhysician: Boolean,
        other: Boolean,
        otherDetails: String
    ) {
        referral =
            referralBox.query().equal(Referral_.encounterId, encounter.id).orderDesc(Referral_.id).build().findFirst()!!

        referral.no_referral = noReferral
        referral.health_post = healthPost
        referral.hygienist = hygienist
        referral.dentist = dentist
        referral.general_physician = generalPhysician
        referral.other = other
        referral.other_details = otherDetails

        referralBox.put(referral)
    }

    override fun updateRecall(
        recallDate: String,
        recallTime: String,
        selectedGeography: String,
        selectedActivity: String
    ) {
        recall =
            recallBox.query().equal(Recall_.encounterId, encounter.id).orderDesc(Recall_.id).build().findFirst()!!

        recall.date = recallDate
        recall.time = recallTime
        recall.geography = selectedGeography
        recall.activity = selectedActivity

        recallBox.put(recall)
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
        if (pager.currentItem == 0) {
            pager.currentItem = 3
        } else {
            pager.currentItem -= 1
        }

    }

    override fun goForward() {
        if (pager.currentItem == 3) {
            pager.currentItem = 0
            onBackPressed()
        } else {
            pager.currentItem += 1
        }
    }
}

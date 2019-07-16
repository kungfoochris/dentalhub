package com.example.dentalhub

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.entities.*
import io.objectbox.Box

class ViewEncounterActivity : AppCompatActivity() {

    private var history = History()
    private var screening = Screening()
    private var treatment = Treatment()
    private var referral = Referral()
    private var encounter = Encounter()

    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>

    // history
    private lateinit var tvBloodDisorderOrBleedingProblem: TextView
    private lateinit var tvDiabetes: TextView
    private lateinit var tvLiverProblem: TextView
    private lateinit var tvRheumaticFever: TextView
    private lateinit var tvSeizuresOrEpilepsy: TextView
    private lateinit var tvHepatitisBOrC: TextView
    private lateinit var tvHIV: TextView
    private lateinit var tvOther: TextView
    private lateinit var tvNoUnderlyingMedicalRecord: TextView
    private lateinit var tvNotTakingAnyMedications: TextView
    private lateinit var tvAllergies: TextView

    // screening
    private lateinit var tvCarriesRisk: TextView
    private lateinit var tvDecayedPrimaryTeeth: TextView
    private lateinit var tvDecayedPermanentTeeth: TextView
    private lateinit var tvCavityPermanentTooth: TextView
    private lateinit var tvCavityPermanentAnterior: TextView
    private lateinit var tvActiveInfection: TextView
    private lateinit var tvNeedARTFilling: TextView
    private lateinit var tvNeedSealant: TextView
    private lateinit var tvNeedSDF: TextView
    private lateinit var tvNeedExtraction: TextView

    // treatment
    private lateinit var tvFVApplied: TextView
    private lateinit var tvTreatmentPlanComplete: TextView
    private lateinit var tvNotes: TextView

    // referral
    private lateinit var tvNoReferral: TextView
    private lateinit var tvHealthPost: TextView
    private lateinit var tvHygienist: TextView
    private lateinit var tvDentist: TextView
    private lateinit var tvGeneralPhysician: TextView
    private lateinit var tvOtherDetails: TextView

    var encounterId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_encounter)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        encounterId = intent.getLongExtra("ENCOUNTER_ID", 0)

        encounterBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
        historyBox = ObjectBox.boxStore.boxFor(History::class.java)
        screeningBox = ObjectBox.boxStore.boxFor(Screening::class.java)
        treatmentBox = ObjectBox.boxStore.boxFor(Treatment::class.java)
        referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)

        encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!

        history = historyBox.query().equal(History_.encounterId, encounter.id).build().findFirst()!!
        screening = screeningBox.query().equal(Screening_.encounterId, encounter.id).build().findFirst()!!
        treatment = treatmentBox.query().equal(Treatment_.encounterId, encounter.id).build().findFirst()!!
        referral = referralBox.query().equal(Referral_.encounterId, encounter.id).build().findFirst()!!


        initUI()
    }

    private fun initUI() {
        // history
        tvBloodDisorderOrBleedingProblem = findViewById(R.id.tvBloodDisorderOrBleedingProblem)
        tvDiabetes = findViewById(R.id.tvDiabetes)
        tvLiverProblem = findViewById(R.id.tvLiverProblem)
        tvRheumaticFever = findViewById(R.id.tvRheumaticFever)
        tvSeizuresOrEpilepsy = findViewById(R.id.tvSeizuresOrEpilepsy)
        tvHepatitisBOrC = findViewById(R.id.tvHepatitisBOrC)
        tvHIV = findViewById(R.id.tvHIV)
        tvOther = findViewById(R.id.tvOther)
        tvNoUnderlyingMedicalRecord = findViewById(R.id.tvNoUnderlyingMedicalRecord)
        tvNotTakingAnyMedications = findViewById(R.id.tvNotTakingAnyMedications)
        tvAllergies = findViewById(R.id.tvAllergies)

        tvBloodDisorderOrBleedingProblem.text = history.blood_disorder.toString()
        tvDiabetes.text = history.diabetes.toString()
        tvLiverProblem.text = history.liver_problem.toString()
        tvRheumaticFever.text = history.rheumatic_fever.toString()
        tvSeizuresOrEpilepsy.text = history.seizuers_or_epilepsy.toString()
        tvHepatitisBOrC.text = history.hepatitis_b_or_c.toString()
        tvHIV.text = history.hiv.toString()
        tvOther.text = history.other
        tvNoUnderlyingMedicalRecord.text = history.no_underlying_medical_record.toString()
        tvNotTakingAnyMedications.text = history.not_taking_any_medications.toString()
        tvAllergies.text = history.allergies

        // screening
        tvCarriesRisk = findViewById(R.id.tvCarriesRisk)
        tvDecayedPrimaryTeeth = findViewById(R.id.tvDecayedPrimaryTeeth)
        tvDecayedPermanentTeeth = findViewById(R.id.tvDecayedPermanentTeeth)
        tvCavityPermanentTooth = findViewById(R.id.tvCavityPermanentTooth)
        tvCavityPermanentAnterior = findViewById(R.id.tvCavityPermanentAnterior)
        tvActiveInfection = findViewById(R.id.tvActiveInfection)
        tvNeedARTFilling = findViewById(R.id.tvNeedARTFilling)
        tvNeedSealant = findViewById(R.id.tvNeedSealant)
        tvNeedSDF = findViewById(R.id.tvNeedSDF)
        tvNeedExtraction = findViewById(R.id.tvNeedExtraction)

        tvCarriesRisk.text = screening.carries_risk
        tvDecayedPrimaryTeeth.text = screening.decayed_pimary_teeth.toString()
        tvDecayedPermanentTeeth.text = screening.decayed_permanent_teeth.toString()
        tvCavityPermanentTooth.text = screening.cavity_permanent_tooth.toString()
        tvCavityPermanentAnterior.text = screening.cavity_permanent_anterior.toString()
        tvActiveInfection.text = screening.active_infection.toString()
        tvNeedARTFilling.text = screening.need_art_filling.toString()
        tvNeedSealant.text = screening.need_sealant.toString()
        tvNeedSDF.text = screening.need_sdf.toString()
        tvNeedExtraction.text = screening.need_extraction.toString()

        // treatment
        tvFVApplied = findViewById(R.id.tvFVApplied)
        tvNotes = findViewById(R.id.tvNotes)
        tvTreatmentPlanComplete = findViewById(R.id.tvTreatmentPlanComplete)

        tvFVApplied.text = treatment.fv_applied.toString()
        tvTreatmentPlanComplete.text = treatment.treatment_plan_complete.toString()
        tvNotes.text = treatment.notes

        // referral
        tvNoReferral = findViewById(R.id.tvNoReferral)
        tvHealthPost = findViewById(R.id.tvHealthPost)
        tvHygienist = findViewById(R.id.tvHygienist)
        tvDentist = findViewById(R.id.tvDentist)
        tvGeneralPhysician = findViewById(R.id.tvGeneralPhysician)
        tvOtherDetails = findViewById(R.id.tvOtherDetails)

        tvNoReferral.text = referral.no_referral.toString()
        tvHealthPost.text = referral.health_post.toString()
        tvHygienist.text = referral.hygienist.toString()
        tvDentist.text = referral.dentist.toString()
        tvGeneralPhysician.text = referral.general_physician.toString()
        tvOtherDetails.text = referral.other_details

        // schedule

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}
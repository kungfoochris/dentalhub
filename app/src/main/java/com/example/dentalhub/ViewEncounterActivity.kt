package com.example.dentalhub

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.entities.*
import io.objectbox.Box

class ViewEncounterActivity : AppCompatActivity() {

    private var history = History()
    private var screening = Screening()
    private var treatment = Treatment()
    private var referral = Referral()
    private var encounter = Encounter()
    private var recall = Recall()

    private lateinit var encounterBox: Box<Encounter>
    private lateinit var historyBox: Box<History>
    private lateinit var screeningBox: Box<Screening>
    private lateinit var treatmentBox: Box<Treatment>
    private lateinit var referralBox: Box<Referral>
    private lateinit var recallBox: Box<Recall>

    // history

    // History Titles TextView
    private lateinit var tvBloodDisorderOrBleedingProblemTitle: TextView
    private lateinit var tvDiabetesTitle: TextView
    private lateinit var tvLiverProblemTitle: TextView
    private lateinit var tvRheumaticFeverTitle: TextView
    private lateinit var tvSeizuresOrEpilepsyTitle: TextView
    private lateinit var tvHepatitisBOrCTitle: TextView
    private lateinit var tvHIVTitle: TextView
    private lateinit var tvOtherTitle: TextView
    private lateinit var tvNoUnderlyingMedicalConditionTitle: TextView
    private lateinit var tvNotTakingAnyMedicationsTitle: TextView
    private lateinit var tvAllergiesTitle: TextView
    // Didn't saw the Medications information -----------

    // History Data TextView
    private lateinit var tvBloodDisorderOrBleedingProblem: TextView
    private lateinit var tvDiabetes: TextView
    private lateinit var tvLiverProblem: TextView
    private lateinit var tvRheumaticFever: TextView
    private lateinit var tvSeizuresOrEpilepsy: TextView
    private lateinit var tvHepatitisBOrC: TextView
    private lateinit var tvHIV: TextView
    private lateinit var tvOther: TextView
    private lateinit var tvNoUnderlyingMedicalCondition: TextView
    private lateinit var tvNotTakingAnyMedications: TextView
    private lateinit var tvAllergies: TextView

    // screening

    // Screening Titles TextView
    private lateinit var tvCarriesRiskTitle: TextView
    private lateinit var tvDecayedPrimaryTeethTitle: TextView
    private lateinit var tvDecayedPermanentTeethTitle: TextView
    private lateinit var tvCavityPermanentToothTitle: TextView
    private lateinit var tvCavityPermanentAnteriorTitle: TextView
    private lateinit var tvActiveInfectionTitle: TextView
    private lateinit var tvNeedARTFillingTitle: TextView
    private lateinit var tvNeedSealantTitle: TextView
    private lateinit var tvNeedSDFTitle: TextView
    private lateinit var tvNeedExtractionTitle: TextView

    // Screening Data TextView
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

    // recall
    private lateinit var tvRecallDate: TextView
    private lateinit var tvRecallTime: TextView
    private lateinit var tvRecallGeography: TextView
    private lateinit var tvRecallActivity: TextView

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
        recallBox = ObjectBox.boxStore.boxFor(Recall::class.java)

        encounter = encounterBox.query().equal(Encounter_.id, encounterId).build().findFirst()!!

        history = historyBox.query().equal(History_.encounterId, encounter.id).build().findFirst()!!
        screening = screeningBox.query().equal(Screening_.encounterId, encounter.id).build().findFirst()!!
        treatment = treatmentBox.query().equal(Treatment_.encounterId, encounter.id).build().findFirst()!!
        referral = referralBox.query().equal(Referral_.encounterId, encounter.id).build().findFirst()!!
        recall = recallBox.query().equal(Recall_.encounterId, encounter.id).build().findFirst()!!


        initUI()
    }

    private fun initUI() {
        // history

        // History Title TextView
        tvBloodDisorderOrBleedingProblemTitle = findViewById(R.id.tvBloodDisorderOrBleedingProblemTitle)
        tvDiabetesTitle = findViewById(R.id.tvDiabetesTitle)
        tvLiverProblemTitle = findViewById(R.id.tvLiverProblemTitle)
        tvRheumaticFeverTitle = findViewById(R.id.tvRheumaticFeverTitle)
        tvSeizuresOrEpilepsyTitle = findViewById(R.id.tvSeizuresOrEpilepsyTitle)
        tvHepatitisBOrCTitle = findViewById(R.id.tvHepatitisBOrCTitle)
        tvHIVTitle = findViewById(R.id.tvHIVTitle)
        tvOtherTitle = findViewById(R.id.tvOtherTitle)
        tvNoUnderlyingMedicalConditionTitle = findViewById(R.id.tvNoUnderlyingMedicalConditionTitle)
        tvNotTakingAnyMedicationsTitle = findViewById(R.id.tvNotTakingAnyMedicationsTitle)
        tvAllergiesTitle = findViewById(R.id.tvAllergiesTitle)

        // History Data TextView
        tvBloodDisorderOrBleedingProblem = findViewById(R.id.tvBloodDisorderOrBleedingProblem)
        tvDiabetes = findViewById(R.id.tvDiabetes)
        tvLiverProblem = findViewById(R.id.tvLiverProblem)
        tvRheumaticFever = findViewById(R.id.tvRheumaticFever)
        tvSeizuresOrEpilepsy = findViewById(R.id.tvSeizuresOrEpilepsy)
        tvHepatitisBOrC = findViewById(R.id.tvHepatitisBOrC)
        tvHIV = findViewById(R.id.tvHIV)
        tvOther = findViewById(R.id.tvOther)
        tvNoUnderlyingMedicalCondition = findViewById(R.id.tvNoUnderlyingMedicalCondition)
        tvNotTakingAnyMedications = findViewById(R.id.tvNotTakingAnyMedications)
        tvAllergies = findViewById(R.id.tvAllergies)

        hideTextView(history.blood_disorder, tvBloodDisorderOrBleedingProblemTitle, tvBloodDisorderOrBleedingProblem)
        hideTextView(history.diabetes, tvDiabetesTitle, tvDiabetes)
        hideTextView(history.liver_problem, tvLiverProblemTitle, tvLiverProblem)
        hideTextView(history.rheumatic_fever, tvRheumaticFeverTitle, tvRheumaticFever)
        hideTextView(history.seizuers_or_epilepsy, tvSeizuresOrEpilepsyTitle, tvSeizuresOrEpilepsy)
        hideTextView(history.hepatitis_b_or_c, tvHepatitisBOrCTitle, tvHepatitisBOrC)
        hideTextView(history.hiv, tvHIVTitle, tvHIV)
        // left to history.other
        hideTextView(history.no_underlying_medical_condition, tvNoUnderlyingMedicalConditionTitle, tvNoUnderlyingMedicalCondition)
        // left to history.medicine Edit Text
        // left to history.not_taking_any_medications Boolean
        // left to alleragies Boolean
        // left to allerigies Edit Text


//        if (!history.other.isNullOrBlank()) {
//            tvOther.text = history.other
//        } else {
//            goneTextView(tvOtherTitle)
//            goneTextView(tvOther)
//        }

        // screening

        // Screening Title TextView
        tvCarriesRiskTitle = findViewById(R.id.tvCarriesRiskTitle)
        tvDecayedPrimaryTeethTitle = findViewById(R.id.tvDecayedPrimaryTeethTitle)
        tvDecayedPermanentTeethTitle = findViewById(R.id.tvDecayedPermanentTeethTitle)
        tvCavityPermanentToothTitle = findViewById(R.id.tvCavityPermanentToothTitle)
        tvCavityPermanentAnteriorTitle = findViewById(R.id.tvCavityPermanentAnteriorTitle)
        tvActiveInfectionTitle = findViewById(R.id.tvActiveInfectionTitle)
        tvNeedARTFillingTitle = findViewById(R.id.tvNeedARTFillingTitle)
        tvNeedSealantTitle = findViewById(R.id.tvNeedSealantTitle)
        tvNeedSDFTitle = findViewById(R.id.tvNeedSDFTitle)
        tvNeedExtractionTitle = findViewById(R.id.tvNeedExtractionTitle)

        // Screening Data TextView
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

//        tvCarriesRisk.text = screening.carries_risk
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

        tvRecallDate = findViewById(R.id.tvRecallDate)
        tvRecallTime = findViewById(R.id.tvRecallTime)
        tvRecallActivity = findViewById(R.id.tvRecallActivity)
        tvRecallGeography = findViewById(R.id.tvRecallGeography)

        tvRecallDate.text = recall.date
        tvRecallTime.text = recall.time
        tvRecallActivity.text = recall.activity
        tvRecallGeography.text = recall.geography

    }

    private fun hideTextView(disease : Boolean, viewTitle : View, view : TextView) {
        if (disease) {
            view.text = disease.toString()

        } else {
            viewTitle.visibility = View.GONE
            view.visibility = View.GONE
        }
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
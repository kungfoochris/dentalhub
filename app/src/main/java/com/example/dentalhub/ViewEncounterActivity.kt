package com.example.dentalhub

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.entities.*
import io.objectbox.Box
import kotlinx.android.synthetic.main.fragment_history.*

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
    private lateinit var tvNoOfDecayedPrimaryTeethTitle: TextView
    private lateinit var tvNoOfDecayedPermanentTeethTitle: TextView
    private lateinit var tvCavityPermanentToothTitle: TextView
    private lateinit var tvCavityPermanentAnteriorTitle: TextView
    private lateinit var tvActiveInfectionTitle: TextView
    private lateinit var tvNeedARTFillingTitle: TextView
    private lateinit var tvNeedSealantTitle: TextView
    private lateinit var tvNeedSDFTitle: TextView
    private lateinit var tvNeedExtractionTitle: TextView

    // Screening Data TextView
    private lateinit var tvCarriesRisk: TextView
    private lateinit var tvNoOfDecayedPrimaryTeeth: TextView
    private lateinit var tvNoOfDecayedPermanentTeeth: TextView
    private lateinit var tvCavityPermanentTooth: TextView
    private lateinit var tvCavityPermanentAnterior: TextView
    private lateinit var tvActiveInfection: TextView
    private lateinit var tvNeedARTFilling: TextView
    private lateinit var tvNeedSealant: TextView
    private lateinit var tvNeedSDF: TextView
    private lateinit var tvNeedExtraction: TextView

    // treatment

    // Treatment Title TextView
    private lateinit var tvFVAppliedTitle: TextView
    private lateinit var tvTreatmentPlanCompleteTitle: TextView
    private lateinit var tvNotesTitle: TextView

    // Treatment Data TextView
    private lateinit var tvFVApplied: TextView
    private lateinit var tvTreatmentPlanComplete: TextView
    private lateinit var tvNotes: TextView

    // referral

    // Referral Title TextView
    private lateinit var tvNoReferralTitle: TextView
    private lateinit var tvHealthPostTitle: TextView
    private lateinit var tvHygienistTitle: TextView
    private lateinit var tvDentistTitle: TextView
    private lateinit var tvGeneralPhysicianTitle: TextView
    private lateinit var tvOtherDetailsTitle: TextView

    // Referral Data TextView
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

        hideBoolean(history.blood_disorder, tvBloodDisorderOrBleedingProblemTitle, tvBloodDisorderOrBleedingProblem)
        hideBoolean(history.diabetes, tvDiabetesTitle, tvDiabetes)
        hideBoolean(history.liver_problem, tvLiverProblemTitle, tvLiverProblem)
        hideBoolean(history.rheumatic_fever, tvRheumaticFeverTitle, tvRheumaticFever)
        hideBoolean(history.seizuers_or_epilepsy, tvSeizuresOrEpilepsyTitle, tvSeizuresOrEpilepsy)
        hideBoolean(history.hepatitis_b_or_c, tvHepatitisBOrCTitle, tvHepatitisBOrC)
        hideBoolean(history.hiv, tvHIVTitle, tvHIV)
        hideString(history.other, tvOtherTitle, tvOther)
        hideBoolean(history.no_underlying_medical_condition, tvNoUnderlyingMedicalConditionTitle, tvNoUnderlyingMedicalCondition)

        // left to history.medicine Edit Text
        hideBoolean(history.not_taking_any_medications, tvNotTakingAnyMedicationsTitle, tvNotTakingAnyMedications)
        // left to alleragies Boolean
        // left to allerigies Edit Text

        // screening

        // Screening Title TextView
        tvCarriesRiskTitle = findViewById(R.id.tvCarriesRiskTitle)
        tvNoOfDecayedPrimaryTeethTitle = findViewById(R.id.tvNoOfDecayedPrimaryTeethTitle)
        tvNoOfDecayedPermanentTeethTitle = findViewById(R.id.tvNoOfDecayedPermanentTeethTitle)
        tvCavityPermanentToothTitle = findViewById(R.id.tvCavityPermanentToothTitle)
        tvCavityPermanentAnteriorTitle = findViewById(R.id.tvCavityPermanentAnteriorTitle)
        tvActiveInfectionTitle = findViewById(R.id.tvActiveInfectionTitle)
        tvNeedARTFillingTitle = findViewById(R.id.tvNeedARTFillingTitle)
        tvNeedSealantTitle = findViewById(R.id.tvNeedSealantTitle)
        tvNeedSDFTitle = findViewById(R.id.tvNeedSDFTitle)
        tvNeedExtractionTitle = findViewById(R.id.tvNeedExtractionTitle)

        // Screening Data TextView
        tvCarriesRisk = findViewById(R.id.tvCarriesRisk)
        tvNoOfDecayedPrimaryTeeth = findViewById(R.id.tvNoOfDecayedPrimaryTeeth)
        tvNoOfDecayedPermanentTeeth = findViewById(R.id.tvNoOfDecayedPermanentTeeth)
        tvCavityPermanentTooth = findViewById(R.id.tvCavityPermanentTooth)
        tvCavityPermanentAnterior = findViewById(R.id.tvCavityPermanentAnterior)
        tvActiveInfection = findViewById(R.id.tvActiveInfection)
        tvNeedARTFilling = findViewById(R.id.tvNeedARTFilling)
        tvNeedSealant = findViewById(R.id.tvNeedSealant)
        tvNeedSDF = findViewById(R.id.tvNeedSDF)
        tvNeedExtraction = findViewById(R.id.tvNeedExtraction)

        // to hide if screening items are unchecked while adding encounter
        hideString(screening.carries_risk, tvCarriesRiskTitle, tvCarriesRisk)
        hideInt(screening.decayed_pimary_teeth, tvNoOfDecayedPrimaryTeethTitle, tvNoOfDecayedPrimaryTeeth)
        hideInt(screening.decayed_permanent_teeth, tvNoOfDecayedPermanentTeethTitle, tvNoOfDecayedPermanentTeeth)
        hideBoolean(screening.cavity_permanent_tooth, tvCavityPermanentToothTitle, tvCavityPermanentTooth)
        hideBoolean(screening.cavity_permanent_anterior, tvCavityPermanentAnteriorTitle, tvCavityPermanentAnterior)
        hideBoolean(screening.active_infection, tvActiveInfectionTitle, tvActiveInfection)
        hideBoolean(screening.need_art_filling, tvNeedARTFillingTitle, tvNeedARTFilling)
        hideBoolean(screening.need_sealant, tvNeedSealantTitle, tvNeedSealant)
        hideBoolean(screening.need_sdf, tvNeedSDFTitle, tvNeedSDF)
        hideBoolean(screening.need_extraction, tvNeedExtractionTitle, tvNeedExtraction)

        // treatment

        // Treatment Title TextView
        tvFVAppliedTitle = findViewById(R.id.tvFVAppliedTitle)
        tvNotesTitle = findViewById(R.id.tvNotesTitle)
        tvTreatmentPlanCompleteTitle = findViewById(R.id.tvTreatmentPlanCompleteTitle)

        // Treatment Data TextView
        tvFVApplied = findViewById(R.id.tvFVApplied)
        tvNotes = findViewById(R.id.tvNotes)
        tvTreatmentPlanComplete = findViewById(R.id.tvTreatmentPlanComplete)

        hideBoolean(treatment.fv_applied, tvFVAppliedTitle, tvFVApplied)
        hideBoolean(treatment.treatment_plan_complete, tvTreatmentPlanCompleteTitle, tvTreatmentPlanComplete)
        // notes is in string value
//        tvNotes.text = treatment.notes

        // referral

        // Referral Title TextView
        tvNoReferralTitle = findViewById(R.id.tvNoReferralTitle)
        tvHealthPostTitle = findViewById(R.id.tvHealthPostTitle)
        tvHygienistTitle = findViewById(R.id.tvHygienistTitle)
        tvDentistTitle = findViewById(R.id.tvDentistTitle)
        tvGeneralPhysicianTitle = findViewById(R.id.tvGeneralPhysicianTitle)
        tvOtherDetailsTitle = findViewById(R.id.tvOtherDetailsTitle)

        // Referral Data TextView
        tvNoReferral = findViewById(R.id.tvNoReferral)
        tvHealthPost = findViewById(R.id.tvHealthPost)
        tvHygienist = findViewById(R.id.tvHygienist)
        tvDentist = findViewById(R.id.tvDentist)
        tvGeneralPhysician = findViewById(R.id.tvGeneralPhysician)
        tvOtherDetails = findViewById(R.id.tvOtherDetails)

        hideBoolean(referral.no_referral, tvNoReferralTitle, tvNoReferral)
        hideBoolean(referral.health_post, tvHealthPostTitle, tvHealthPost)
        hideBoolean(referral.hygienist, tvHygienistTitle, tvHygienist)
        hideBoolean(referral.dentist, tvDentistTitle, tvDentist)
        hideBoolean(referral.general_physician, tvGeneralPhysicianTitle, tvGeneralPhysician)
        hideString(referral.other_details, tvOtherDetailsTitle, tvOtherDetails)

        // schedule

        // Schedule Title TextView
        tvRecallDate = findViewById(R.id.tvRecallDate)
        tvRecallTime = findViewById(R.id.tvRecallTime)
        tvRecallActivity = findViewById(R.id.tvRecallActivity)
        tvRecallGeography = findViewById(R.id.tvRecallGeography)

        // Schedule Data TextView
        tvRecallDate.text = recall.date
        tvRecallTime.text = recall.time
        tvRecallActivity.text = recall.activity
        tvRecallGeography.text = recall.geography

    }

    private fun hideBoolean(disease : Boolean, viewTitle : View, view : TextView) {
        if (disease) {
            view.text = disease.toString()

        } else {
            viewTitle.visibility = View.GONE
            view.visibility = View.GONE
        }
    }

    private fun hideString(disease: String, viewTitle: View, view: TextView) {
        if (!disease.isNullOrBlank()) {
            view.text = disease
        } else {
            viewTitle.visibility = View.GONE
            view.visibility = View.GONE
        }
    }

    private fun hideInt(disease: Int, viewTitle: View, view: TextView) {
        if (disease != 0) {
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
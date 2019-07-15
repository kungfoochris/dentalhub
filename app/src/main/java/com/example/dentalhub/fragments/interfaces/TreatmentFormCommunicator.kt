package com.example.dentalhub.fragments.interfaces

interface TreatmentFormCommunicator {
    fun updateTreatment(
        notes: String,
        fvApplied: Boolean,
        treatmentPlanComplete: Boolean,
        teeth: Array<String>
    )
}
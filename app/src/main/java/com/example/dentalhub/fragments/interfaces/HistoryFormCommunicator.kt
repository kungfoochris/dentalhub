package com.example.dentalhub.fragments.interfaces

interface HistoryFormCommunicator {
    fun updateHistory(
        bloodDisorders: Boolean, diabetes: Boolean, liverProblem: Boolean,
        rheumaticFever: Boolean, seizuresOrEpilepsy: Boolean, hepatitisBOrC: Boolean,
        hiv: Boolean, other: String, noUnderlyingMedicalRecord: Boolean, medications: String,
        notTakingAnyMedications: Boolean, no_allergies: Boolean, allergies: String
    )
}
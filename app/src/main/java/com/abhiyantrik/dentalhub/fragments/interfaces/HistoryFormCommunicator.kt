package com.abhiyantrik.dentalhub.fragments.interfaces

interface HistoryFormCommunicator {
    fun updateHistory(
        bloodDisorders: Boolean, diabetes: Boolean, liverProblem: Boolean,
        rheumaticFever: Boolean, seizuresOrEpilepsy: Boolean, hepatitisBOrC: Boolean,
        hiv: Boolean, other: String, noUnderlyingMedicalCondition: Boolean, medications: String,
        notTakingAnyMedications: Boolean, noAllergies: Boolean, allergies: String
    )
}
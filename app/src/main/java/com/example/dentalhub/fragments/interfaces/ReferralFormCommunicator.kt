package com.example.dentalhub.fragments.interfaces

interface ReferralFormCommunicator {
    fun updateReferral(
        noReferral: Boolean,
        healthPost: Boolean,
        hygienist: Boolean,
        dentist: Boolean,
        generalPhysician: Boolean,
        other: Boolean,
        otherDetails: String
    )
}
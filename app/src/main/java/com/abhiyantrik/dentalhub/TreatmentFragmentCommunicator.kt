package com.abhiyantrik.dentalhub

interface TreatmentFragmentCommunicator {
    fun goBack()
    fun goForward()
    fun getEncounterIdForUpdate(): Long
}
package com.example.dentalhub.fragments.interfaces

interface RecallFormCommunicator {
    fun updateRecall(
        recallDate: String,
        recallTime: String,
        selectedGeography: String,
        selectedActivity: String
    )
}
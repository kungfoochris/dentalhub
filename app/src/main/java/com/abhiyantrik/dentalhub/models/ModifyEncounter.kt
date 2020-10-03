package com.abhiyantrik.dentalhub.models

data class ModifyEncounter(
    val id: String,
    val encounter_type: String,
    val patient: ModifyPatient
)
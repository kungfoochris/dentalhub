package com.example.dentalhub.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class Encounter(
    @Id var id: Long,
    var encounter_type: String,
    var patient_id: Int,
    var author_id: Int,
    var date: String
)
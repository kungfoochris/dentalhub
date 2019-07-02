package com.example.dentalhub.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class Treatment(
    @Id var id: Long,
    var encounter_id: Long
    )
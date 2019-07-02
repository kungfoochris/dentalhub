package com.example.dentalhub.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class Screening(
    @Id var id: Long,
    var encounter_id: Long,
    var carries_risk: String,
    var decayed_pimary_teeth: Int,
    var decayed_permanent_teeth: Int,
    var cavity_permanent_anterior: Boolean,
    var cavity_permanent_tooth: Boolean,
    var active_infection: Boolean,
    var need_art_filling: Boolean,
    var need_sealant: Boolean,
    var need_sdf: Boolean,
    var need_extraction: Boolean
)
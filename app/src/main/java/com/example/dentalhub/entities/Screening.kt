package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Screening: Parcelable{
    @Id var id: Long = 0
    var encounter: ToOne<Encounter>? = null
    var carries_risk: String = ""
    var decayed_pimary_teeth: Int = 0
    var decayed_permanent_teeth: Int = 0
    var cavity_permanent_anterior: Boolean = false
    var cavity_permanent_tooth: Boolean = false
    var active_infection: Boolean = false
    var need_art_filling: Boolean = false
    var need_sealant: Boolean = false
    var need_sdf: Boolean = false
    var need_extraction: Boolean = false
}
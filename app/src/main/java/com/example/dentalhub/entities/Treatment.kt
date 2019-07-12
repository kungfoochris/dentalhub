package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Treatment: Parcelable{
    @Id var id: Long = 0
    var fv_applied: Boolean = false
    var treatment_plan_complete: Boolean = false
    var notes: String = ""

    var tooth18 = "none"
    var tooth17 = "none"
    var tooth16 = "none"
    var tooth15 = "none"
    var tooth14 = "none"
    var tooth13 = "none"
    var tooth12 = "none"
    var tooth11 = "none"

    var tooth21 = "none"
    var tooth22 = "none"
    var tooth23 = "none"
    var tooth24 = "none"
    var tooth25 = "none"
    var tooth26 = "none"
    var tooth27 = "none"
    var tooth28 = "none"

    var tooth48 = "none"
    var tooth47 = "none"
    var tooth46 = "none"
    var tooth45 = "none"
    var tooth44 = "none"
    var tooth43 = "none"
    var tooth42 = "none"
    var tooth41 = "none"

    var tooth31 = "none"
    var tooth32 = "none"
    var tooth33 = "none"
    var tooth34 = "none"
    var tooth35 = "none"
    var tooth36 = "none"
    var tooth37 = "none"
    var tooth38 = "none"

    var encounter: ToOne<Encounter>? = null
}

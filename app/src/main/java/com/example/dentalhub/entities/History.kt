package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class History : Parcelable {
    @Id
    var id: Long = 0
    var encounter: ToOne<Encounter>? = null
    var blood_disorder: Boolean = false
    var diabetes: Boolean = false
    var liver_problem: Boolean = false
    var rheumatic_fever: Boolean = false
    var seizuers_or_epilepsy: Boolean = false
    var hepatitis_b_or_c: Boolean = false
    var hiv: Boolean = false
    var other: String = ""
    var no_underlying_medical_condition: Boolean = false
    var medications: String = ""
    var not_taking_any_medications: Boolean = false
    var no_allergies: Boolean = true
    var allergies: String = ""
}

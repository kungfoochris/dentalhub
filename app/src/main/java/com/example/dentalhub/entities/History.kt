package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class History(
    @Id var id: Long,
    var encounter_id: Long,
    var blood_disorder: Boolean,
    var diabetes: Boolean,
    var liver_problem: Boolean,
    var rheumatic_fever: Boolean,
    var seizuers_or_epilepsy: Boolean,
    var hepatitis_b_or_c: Boolean,
    var hiv: Boolean,
    var other: String,
    var no_underlying_medical_record: Boolean,
    var medications: String,
    var allergies: String
): Parcelable
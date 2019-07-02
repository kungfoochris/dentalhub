package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Referral(
    @Id var id: Long,
    var encounter_id: Long,
    var no_referral: Boolean,
    var health_post: Boolean,
    var hygienist: Boolean,
    var dentist: Boolean,
    var general_physician: Boolean,
    var other: String
): Parcelable
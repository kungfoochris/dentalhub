package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Referral(
    @Id var id: Long,
    var encounter: ToOne<Encounter>,
    var no_referral: Boolean,
    var health_post: Boolean,
    var hygienist: Boolean,
    var dentist: Boolean,
    var general_physician: Boolean,
    var other: String
): Parcelable
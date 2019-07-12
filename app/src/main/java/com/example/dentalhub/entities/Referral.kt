package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Referral: Parcelable{
    @Id var id: Long = 0
    var encounter: ToOne<Encounter>? = null
    var no_referral: Boolean = false
    var health_post: Boolean = false
    var hygienist: Boolean = false
    var dentist: Boolean = false
    var general_physician: Boolean = false
    var other: String = ""
}

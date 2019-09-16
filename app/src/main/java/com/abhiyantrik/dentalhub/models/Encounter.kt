package com.abhiyantrik.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Encounter : Parcelable {
    var id: Int = 0
    var uid: String = ""
    var encounter_type: String = ""
    var other_detail: String = ""
    var created_at: String = ""
    var updated_at: String = ""
    var uploaded: Boolean = false

    var history: History? = null
    var screening: Screening? = null
    var treatment: Treatment? = null
    var referral: Referral? = null
}
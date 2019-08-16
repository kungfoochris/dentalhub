package com.example.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Encounter : Parcelable {
    var id: Int = 0
    var encounter_type: String = ""
    var created_at: String = ""
    var updated_at: String = ""
    var uploaded: Boolean = false
}
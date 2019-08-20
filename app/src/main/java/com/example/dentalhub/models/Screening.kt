package com.example.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Screening : Parcelable {
    var id: Long = 0
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

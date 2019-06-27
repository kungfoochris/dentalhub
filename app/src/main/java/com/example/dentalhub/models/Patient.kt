package com.example.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Patient(
    var id:String,
    var first_name: String,
    var middle_name: String,
    var last_name: String,
    var full_name: String,
    //var address: String,
    var gender: String,
    var dob: String,
    var phone: String,
    var education: String,
    var city: String,
    var state: String,
    var country: String,
    var latitude: String,
    var longitude: String,
    var date: String
): Parcelable
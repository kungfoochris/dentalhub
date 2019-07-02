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
    var gender: String,
    var dob: String,
    var phone: String,
    var education: String,
    var marital_status: String,
    var street_address: String,
    var ward: Int,
    var city: String,
    var state: String,
    var country: String,
    var latitude: String,
    var longitude: String,
    var date: String
): Parcelable{
    fun address(): String{
        return "$street_address $ward, $city, $country"
    }
}
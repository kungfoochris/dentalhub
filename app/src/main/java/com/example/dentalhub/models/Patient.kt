package com.example.dentalhub.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Patient(
    var id: Int,
    var first_name: String,
    var middle_name: String,
    var last_name: String,
    var gender: String,
    var dob: String,
    var phone: String,
    var education: String,
    var ward: Int,
    var municipality: Int,
    var district: Int,
    var latitude: String,
    var longitude: String,
    var geography_id: String,
    var activityarea_id: String,
    var created_at: String,
    var updated_at: String,
    var uploaded: Boolean
): Parcelable{
    fun address(): String {
        return "$municipality $ward, $district"
    }

    fun fullName(): String {
        return "$first_name $middle_name $last_name"
    }

    fun age(): String {
        val year: Int = dob.substring(0, 4).toInt()
        val month: Int = dob.substring(5, 7).toInt()
        val day: Int = dob.substring(8, 10).toInt()
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        dob.set(year, month, day)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        if (age < 0) {
            age = 0
        }

        val ageInt = age

        return ageInt.toString()
    }
}
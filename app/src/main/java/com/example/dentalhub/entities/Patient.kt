package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*


@Entity
@Parcelize
class Patient(
    @Id var id: Long,
    var first_name: String,
    var middle_name: String,
    var last_name: String,
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
    var created_at: String,
    var updated_at: String,
    var uploaded: Boolean
) : Parcelable {

    @IgnoredOnParcel
    @Backlink(to = "patient")
    var encounters: ToMany<Encounter>? = null

    fun address(): String {
        return "$street_address $ward, $city, $country"
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

        val ageInt = age

        return ageInt.toString()
    }
}
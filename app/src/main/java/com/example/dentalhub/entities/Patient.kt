package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import kotlinx.android.parcel.Parcelize

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
    var date: String
    //var encounters: ToMany<Encounter>?
): Parcelable{
    fun address(): String{
        return "$street_address $ward, $city, $country"
    }
    fun fullName(): String{
        return "$first_name $middle_name $last_name"
    }
}
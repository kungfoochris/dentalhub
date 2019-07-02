package com.example.dentalhub.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class Patient(
    @Id var id: Long,
    var first_name: String,
    var middle_name: String,
    var last_name: String,
    var phone: String,
    var education: String,
    var gender: String,
    var dob: String,
    var street_address: String,
    var ward: Int,
    var city: String,
    var state: String,
    var country: String,
    var latitude: String,
    var longitude: String,
    var date: String
)
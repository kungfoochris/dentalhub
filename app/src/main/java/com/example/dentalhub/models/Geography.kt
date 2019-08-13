package com.example.dentalhub.models

data class Geography(
        var id: Int,
        var street_address: String,
        var city: String,
        var state: String,
        var country: String
){
    fun address(): String {
        return "$street_address $city"
    }
}
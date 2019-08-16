package com.example.dentalhub.models

data class Geography(
        var id: Int,
        var ward: Ward,
        var municipality: Municipality,
        var district: District
){
    fun address(): String {
        return "${municipality.name}-${ward.ward}, ${district.name}"
    }
}
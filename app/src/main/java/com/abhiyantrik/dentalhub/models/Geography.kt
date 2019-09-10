package com.abhiyantrik.dentalhub.models

data class Geography(
    var id: String,
    var district: String,
    var municipality: String,
    var ward: Int,
    var tole: String,
    var location: String
)

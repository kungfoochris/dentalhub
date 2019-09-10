package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Geography(
    @Id var id: Long,
    var remote_id: Long,
    var street_address: String,
    var city: String,
    var state: String,
    var country: String
) : Parcelable {
    fun address(): String {
        return "$street_address $city"
    }

}

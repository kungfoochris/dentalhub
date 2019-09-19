package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Geography : Parcelable {
    @Id var id: Long = 0
    var remote_id: String = ""
    var district: String = ""
    var municipality: String = ""
    var ward: String = ""
    var tole: String = ""

    fun address(): String {
        return "$tole, $municipality-$ward, $district"
    }
}

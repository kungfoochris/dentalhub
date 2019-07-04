package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Encounter(
    @Id var id: Long,
    var encounter_type: String,
    var date: String
):Parcelable{
    @IgnoredOnParcel
    public var patient: ToOne<Patient>? = null
}
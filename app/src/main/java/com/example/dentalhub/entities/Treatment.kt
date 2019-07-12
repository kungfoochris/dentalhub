package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Treatment: Parcelable{
    @Id var id: Long = 0
    var encounter: ToOne<Encounter>? = null
}

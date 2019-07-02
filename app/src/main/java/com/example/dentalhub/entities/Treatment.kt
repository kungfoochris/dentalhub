package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class Treatment(
    @Id var id: Long,
    var encounter_id: Long
    ): Parcelable
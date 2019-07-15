package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Activity(
    @Id var id: Long,
    var remote_id: Long,
    var name: String
) : Parcelable {
}
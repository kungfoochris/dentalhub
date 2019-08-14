package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class Municipality(
    @Id var id: Long,
    var name: String,
    var district: ToOne<District>? = null
) : Parcelable
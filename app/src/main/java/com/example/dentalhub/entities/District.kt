package com.example.dentalhub.entities

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class District(
    @Id var id: Long,
    var name: String,
    var municipalities: ToMany<Municipality>?
) : Parcelable
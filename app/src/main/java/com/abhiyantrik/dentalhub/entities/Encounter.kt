package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import com.abhiyantrik.dentalhub.DentalApp
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity
class Encounter : Parcelable {
    @Id
    var id: Long = 0
    var remote_id: String = ""
    var encounter_type: String = ""
    var created_at: String = ""
    var updated_at: String = ""
    var uploaded: Boolean = false
    var patient: ToOne<Patient>? = null

    fun isEditable(): Boolean {
        val date1 = Date()
        val date2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(created_at+" 00:01:00")
        val difference = date1.time - date2.time
        return difference < DentalApp.editableDuration * 100
    }
}

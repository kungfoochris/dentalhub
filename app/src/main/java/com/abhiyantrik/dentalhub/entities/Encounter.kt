package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.utils.DateHelper
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Parcelize
@Entity
class Encounter : Parcelable {
    @Id
    var id: Long = 0
    var remote_id: String = ""
    var encounter_type: String = ""
    var other_problem: String = ""
    var created_at: String = ""
    var updated_at: String = ""
    var uploaded: Boolean = false
    var updated: Boolean = false
    var author: String = ""
    var geography_id: String = ""
    var activityarea_id: String = ""
    var updated_by: String? = ""
    var patient: ToOne<Patient>? = null

    fun isEditable(): Boolean {
        val date1 = Date()
        val createdAt: String = if(created_at.isEmpty()){
            DateHelper.getCurrentDate()
        }else{
            created_at.plus(" 00:01:00")
        }
        val date2 =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(createdAt)
        val difference = abs(date1.time - date2.time)
        return difference / 1000.0 < DentalApp.editableDuration
    }
}

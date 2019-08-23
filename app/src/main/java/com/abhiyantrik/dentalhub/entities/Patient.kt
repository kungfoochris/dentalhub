package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import com.abhiyantrik.dentalhub.ObjectBox
import com.hornet.dateconverter.DateConverter
import com.hornet.dateconverter.Model
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*


@Entity
@Parcelize
class Patient(
    @Id var id: Long,
    var remote_id: String,
    var first_name: String,
    var middle_name: String,
    var last_name: String,
    var gender: String,
    var dob: String,
    var phone: String,
    var education: String,
    var ward: Int,
    var municipality: Int,
    var district: Int,
    var latitude: String,
    var longitude: String,
    var geography_id: String,
    var activityarea_id: String,
    var created_at: String,
    var updated_at: String,
    var uploaded: Boolean,
    var updated: Boolean
) : Parcelable {

    @IgnoredOnParcel
    @Backlink(to = "patient")
    var encounters: ToMany<Encounter>? = null

    fun address(): String {
        val municipality_name = minicipalityName()
        val district_name = districtName()

        return "$municipality_name-$ward, $district_name"
    }

    fun fullName(): String {
        return "$first_name $middle_name $last_name"
    }

    fun minicipalityName(): String {
        val municipalityBox = ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.Municipality::class.java)
        val municipality_name = municipalityBox.query().equal(Municipality_.id, municipality.toLong()).build().findFirst()!!

        return "${municipality_name.name}"
    }

    fun districtName(): String {
        val districtBox = ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.District::class.java)
        val district_name = districtBox.query().equal(District_.id, district.toLong()).build().findFirst()!!

        return "${district_name.name}"
    }

    fun age(): String {
        val year: Int = dob.substring(0, 4).toInt()
        val month: Int = dob.substring(5, 7).toInt()
        val day: Int = dob.substring(8, 10).toInt()
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        var nepaliCalander = DateConverter()

        var todayNepali = nepaliCalander.todayNepaliDate

        var yearToday = todayNepali.year
        var monthToday = todayNepali.month + 1
        var dayToday = todayNepali.day

        dob.set(year, month, day)
        today.set(yearToday, monthToday, dayToday)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        if (age < 0) {
            age = 0
        }

        val ageInt = age

        return ageInt.toString()
    }
}
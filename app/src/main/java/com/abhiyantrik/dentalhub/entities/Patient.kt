package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import android.util.Log
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
    var updated: Boolean,
    @Backlink(to = "patient")
    var recall: ToMany<Recall>? = null
) : Parcelable {

    @IgnoredOnParcel
    @Backlink(to = "patient")
    var encounters: ToMany<Encounter>? = null

    fun address(): String {
        val municipalityName = municipalityName()
        val districtName = districtName()
        val wardNumber = wardNumber()

        return "$municipalityName-$wardNumber, $districtName"
    }

    fun fullName(): String {
        return "$first_name $middle_name $last_name"
    }

    fun wardNumber(): String{
        val wardBox = ObjectBox.boxStore.boxFor(Ward::class.java)
        val ward = wardBox.query().equal(Ward_.remote_id, ward.toLong()).build().findFirst()
        return "${ward?.ward}"
    }

    fun municipalityName(): String {
        val municipalityBox = ObjectBox.boxStore.boxFor(Municipality::class.java)
        val municipalityName = municipalityBox.query().equal(Municipality_.remote_id, municipality.toLong()).build().findFirst()!!

        return municipalityName.name
    }

    fun districtName(): String {
        val districtBox = ObjectBox.boxStore.boxFor(District::class.java)
        val districtName = districtBox.query().equal(District_.remote_id, district.toLong()).build().findFirst()!!

        return districtName.name
    }

    fun age(): String {
        val year: Int = dob.substring(0, 4).toInt()
        val month: Int = dob.substring(5, 7).toInt()
        val day: Int = dob.substring(8, 10).toInt()
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()

        val nepaliCalender = DateConverter()

        val todayNepali = nepaliCalender.todayNepaliDate

        val yearToday = todayNepali.year
        val monthToday = todayNepali.month + 1
        val dayToday = todayNepali.day

        dob.set(year, month, day)
        today.set(yearToday, monthToday, dayToday)

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        Log.d("AGE ", age.toString())
        Log.d("Month", ((today.get(Calendar.DAY_OF_YEAR) - dob.get(Calendar.DAY_OF_YEAR))/30).toString())
        return if (age <= 0) {
            val months = (today.get(Calendar.DAY_OF_YEAR) - dob.get(Calendar.DAY_OF_YEAR))/30
            "$months months"
        }else{
            "$age years"
        }
    }
}
package com.abhiyantrik.dentalhub.entities

import android.os.Parcelable
import com.abhiyantrik.dentalhub.DentalApp
import com.hornet.dateconverter.DateConverter
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
    var ward_id: Int = 0
    var activityarea_id: String = ""
    var updated_by: String? = ""
    var patient: ToOne<Patient>? = null

    fun isEditable(): Boolean {

        val year: Int = created_at.substring(0, 4).toInt()
        val month: Int = created_at.substring(5, 7).toInt()
        val day: Int = created_at.substring(8, 10).toInt()

        val nepaliCalender = DateConverter()

        val createdDateEnglish =
            nepaliCalender.getEnglishDate(year, month, day)

        var todayMonth = (createdDateEnglish.month + 1).toString()
        if (todayMonth.length == 1) {
            todayMonth = "0" + todayMonth
        }
        var todayDay = createdDateEnglish.day.toString()
        if (todayDay.length == 1) {
            todayDay = "0" + todayDay
        }

        val todayDate = createdDateEnglish.year.toString() + "-" + todayMonth + "-" + todayDay + " 00:01:00"
//        println("This day is " + todayDate)

//        val createdAt: String = if(created_at.isEmpty()){
//            DateHelper.getCurrentDate()
//        }else{
//            created_at.plus(" 00:01:00")
//        }

        val thisDay = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(todayDate)
        val date2 = Date()
//        println("Date Today check" + date2)
//            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(createdAt)
        val difference = abs(thisDay.time - date2.time)
//        println("Date Today " + todayDate)
//        println("Date 1" + thisDay.toString())
//        println("Date 2" + date2.toString())
//        println("Date Difference" + difference.toString())
//        println("Date check " + ((difference / 1000.0) < DentalApp.editableDuration).toString())
        return (difference / 1000.0) < DentalApp.editableDuration
    }
}

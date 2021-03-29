package com.abhiyantrik.dentalhub.utils

import android.content.Context
import android.util.Log
import com.abhiyantrik.dentalhub.AddEncounterActivity
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.R
import com.hornet.dateconverter.DateConverter
import com.hornet.dateconverter.Model
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {

        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            return sdf.format(Date())
        }

        fun getReadableNepaliDate(date: String): String{
            val day = date.substring(8, 10).toInt()
            val month = date.substring(5, 7).toInt()
            val year = date.substring(0, 4).toInt()
            var monthName = "Baisakh"
            when(month){
                1 -> monthName = "Baisakh"
                2 -> monthName = "Jestha"
                3 -> monthName = "Ashar"
                4 -> monthName = "Shrawan"
                5 -> monthName = "Bhadra"
                6 -> monthName = "Ashoj"
                7 -> monthName = "Kartik"
                8 -> monthName = "Mangsir"
                9 -> monthName = "Poush"
                10 -> monthName = "Magh"
                11 -> monthName = "Falgun"
                12 -> monthName = "Chaitra"
            }
            return "$year-$monthName-$day"
        }

        fun getTodaysNepaliDate(): String {
            val todayNepali = DateConverter().todayNepaliDate

            val yearToday = todayNepali.year

            val monthToday = DecimalFormat("00").format(todayNepali.month + 1).toString()
            val dayToday = DecimalFormat("00").format(todayNepali.day).toString()

            return "$yearToday-$monthToday-$dayToday"
        }

        fun getNextDay(date: String): String {
            Timber.d("Add one day to : %s", date)
            var day = date.substring(8, 10).toInt()
            var month = date.substring(5, 7).toInt()
            var year = date.substring(0, 4).toInt()
            if (day == 32 && month == 12) {
                year += 1
                month = 1
                day = 1
            } else if (day == 32) {
                month += 1
                day = 1
            } else {
                day += 1
            }

            return DecimalFormat("0000").format(year) + "-" + DecimalFormat("00").format(month) + "-" + DecimalFormat(
                "00"
            ).format(day)
        }

        fun getPreviousDay(date: String): String {
            Timber.d("Subtract one day to : %s", date)
            var day = date.substring(8, 10).toInt()
            val month = date.substring(5, 7).toInt()
            var year = date.substring(0, 4).toInt()
            if (day == 1) {
                if (month == 1) {
                    year -= 1
                }
                day = 30
            } else {
                day -= 1
            }
            return DecimalFormat("0000").format(year) + "-" + DecimalFormat("00").format(month) + "-" + DecimalFormat(
                "00"
            ).format(day)
        }

        fun formatNepaliDate(context: Context, date: String): String {
            Timber.d("FORMAT: %s", date)
            var nepaliFormattedDate: String
            if(date.isNotEmpty()){
                try {
                    // write the formatting logic
                    nepaliFormattedDate = date.substring(8, 10) + " " + getNepaliMonthName(
                        context,
                        date.substring(5, 7).toInt()
                    ) + " " + date.substring(0, 4)
                } catch (e: IllegalArgumentException) {
                    nepaliFormattedDate = "-"
                    Timber.d("DateHelper %s", e.printStackTrace().toString())
                } catch (e: StringIndexOutOfBoundsException) {
                    nepaliFormattedDate = "-"
                    Timber.d("DateHelper %s", e.printStackTrace().toString())
                }
            }else{
                nepaliFormattedDate = "-"
            }

            return nepaliFormattedDate
        }

        fun getNepaliMonthName(context: Context, month: Int): String {
            return if (month in 1..12) {
                context.resources.getStringArray(R.array.months)[month - 1]
            } else {
                "-"
            }

        }


        fun getSimpleDateFormat(date: String): String {
            val dateObj = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date)
            val sdf = SimpleDateFormat("yyyy-MMM dd", Locale.US)
            return sdf.format(dateObj)
        }

        fun getDaysLaterDate(year: Int, month: Int, day: Int, numberOfDaysLater: Int) : String {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, day)
            Timber.d("Backdate test $cal")
            if (numberOfDaysLater != 0) {
                cal.add(Calendar.DATE, numberOfDaysLater)
            }
            Timber.d("Backdate test $cal")

            val dateObj = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
            Timber.d("Backdate test $dateObj")
            return dateObj
        }

        fun getNepaliDaysLaterDate(mContext: Context, numberOfDaysLater: Int): String {
            val backDate =
                DentalApp.readFromPreference(
                    mContext,
                    Constants.PERF_SELECTED_BACKDATE,
                    getTodaysNepaliDate()
                )
            val nepaliDateConverter = DateConverter()
            val nepaliday = backDate.substring(8, 10).toInt()
            val nepalimonth = backDate.substring(5, 7).toInt()
            val nepaliyear = backDate.substring(0, 4).toInt()
            // first convert nepali date to english
            val englishBackDate = nepaliDateConverter.getEnglishDate(nepaliyear, nepalimonth, nepaliday)
            val convertedDate = getDaysLaterDate(englishBackDate.year, englishBackDate.month, englishBackDate.day, numberOfDaysLater)
            val day = convertedDate.substring(8, 10).toInt()
            val month = convertedDate.substring(5, 7).toInt()
            val year = convertedDate.substring(0, 4).toInt()
            // then convert to nepali date
            val nepaliBackDate = nepaliDateConverter.getNepaliDate(year, month, day)

            val yearToday = nepaliBackDate.year
            val monthToday = DecimalFormat("00").format(nepaliBackDate.month + 1).toString()
            val dayToday = DecimalFormat("00").format(nepaliBackDate.day).toString()

            return "$yearToday-$monthToday-$dayToday"
        }
    }
}


package com.abhiyantrik.dentalhub.utils

import android.content.Context
import android.util.Log
import com.abhiyantrik.dentalhub.R
import com.hornet.dateconverter.DateConverter
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            return sdf.format(Date())
        }

        fun getCurrentNepaliDate(): String {
            val todayNepali = DateConverter().todayNepaliDate

            val yearToday = todayNepali.year

            val monthToday = DecimalFormat("00").format(todayNepali.month + 1).toString()
            val dayToday = DecimalFormat("00").format(todayNepali.day).toString()

            return "$yearToday-$monthToday-$dayToday"
        }

        fun getNextDay(date: String): String {
            Log.d("Add one day to : ", date)
            var day = date.substring(8, 10).toInt()
            var month = date.substring(5, 7).toInt()
            var year = date.substring(0, 4).toInt()
            if (day == 30 && month == 12) {
                year += 1
                month = 1
                day = 1
            } else if (day == 30) {
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
            Log.d("Subtract one day to : ", date)
            var day = date.substring(8, 10).toInt()
            var month = date.substring(5, 7).toInt()
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
            var nepaliFormattedDate = ""
            try {
                // write the formatting logic
                nepaliFormattedDate = date.substring(0, 4) + " " + getNepaliMonthName(
                    context,
                    date.substring(5, 7).toInt()
                ) + " " + date.substring(8, 10)
            } catch (e: IllegalArgumentException) {
                nepaliFormattedDate = "-"
                Log.d("DateHelper", e.printStackTrace().toString())
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


        fun formatDate(date: String): String {
            val dateObj = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date)
            val sdf = SimpleDateFormat("yyyy-MMM dd", Locale.US)
            return sdf.format(dateObj)
        }
    }
}


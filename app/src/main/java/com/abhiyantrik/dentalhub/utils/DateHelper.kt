package com.abhiyantrik.dentalhub.utils

import android.content.Context
import android.util.Log
import com.abhiyantrik.dentalhub.R
import com.hornet.dateconverter.DateConverter
import java.lang.IllegalArgumentException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            return sdf.format(Date())
        }
        fun getCurrentNepaliDate(): String{
            val todayNepali = DateConverter().todayNepaliDate

            val yearToday = todayNepali.year

            val monthToday = DecimalFormat("00").format(todayNepali.month+1).toString()
            val dayToday = DecimalFormat("00").format(todayNepali.day).toString()

            return "$yearToday-$monthToday-$dayToday"
        }

        fun formatNepaliDate(context:Context, date: String): String{
            var nepaliFormattedDate = ""
            try{
                // write the formatting logic
                nepaliFormattedDate = date.substring(0,4)+"-"+getNepaliMonthName(context, date.substring(5,7).toInt())+" "+date.substring(8,10)
            }catch (e: IllegalArgumentException){
                nepaliFormattedDate = "-"
                Log.d("DateHelper", e.printStackTrace().toString())
            }
            return nepaliFormattedDate
        }

        fun getNepaliMonthName(context: Context, month:Int):String{
            return context.resources.getStringArray(R.array.months).get(month-1)
        }


        fun formatDate(date: String): String{
            val dateObj = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date)
            val sdf = SimpleDateFormat("yyyy-MMM dd", Locale.US)
            return sdf.format(dateObj)
        }
    }
}


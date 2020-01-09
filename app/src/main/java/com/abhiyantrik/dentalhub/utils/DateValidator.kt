package com.abhiyantrik.dentalhub.utils

import android.util.Log
import com.hornet.dateconverter.DateConverter
import com.hornet.dateconverter.Model
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat

class DateValidator {
    companion object {
        fun isValid(year: Int, month: Int, day: Int): Boolean {
            var status = false

            try {
                val nepaliCalender = DateConverter()
                val todayEnglish = nepaliCalender.getEnglishDate(year, month + 1, day)
                val todayNepali = nepaliCalender.getNepaliDate(todayEnglish.year, todayEnglish.month + 1, todayEnglish.day)

//                Log.d("DateValidator", "todayEnglish " + todayEnglish.year + todayEnglish.month + todayEnglish.day)
//                Log.d("DateValidator", "todayNepali " + todayNepali.year + todayNepali.month + todayNepali.day)
//                Log.d("DateValidator", "enteredbyUser " + year + month + day)
//                format.parse(date)
                if ((todayNepali.year == year) && (todayNepali.month == month) && (todayNepali.day == day)) {
                    status = true
                }
            } catch (e: ParseException) {
                status = false
            } finally {
                return status
            }
        }
    }
}
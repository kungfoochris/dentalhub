package com.abhiyantrik.dentalhub.utils

import com.hornet.dateconverter.DateConverter
import java.text.ParseException

class DateValidator {
    companion object {
        fun isValid(year: Int, month: Int, day: Int): Boolean {
            var status = false

            try {
                val nepaliCalender = DateConverter()
                val todayEnglish = nepaliCalender.getEnglishDate(year, month + 1, day)
                val todayNepali = nepaliCalender.getNepaliDate(todayEnglish.year, todayEnglish.month + 1, todayEnglish.day)

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
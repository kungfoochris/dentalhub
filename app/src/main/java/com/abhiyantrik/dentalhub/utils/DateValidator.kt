package com.abhiyantrik.dentalhub.utils

import com.hornet.dateconverter.DateConverter
import java.lang.Exception
import java.text.ParseException

class DateValidator {
    companion object {
        fun isValid(year: Int, month: Int, day: Int): Boolean {
            var status = false

            try {
                val nepaliCalender = DateConverter()
                // since from the year 2000 is not converted by the nepali calender library
                if (year > 2000) {
                    val todayEnglish = nepaliCalender.getEnglishDate(year, month + 1, day)
                    val todayNepali = nepaliCalender.getNepaliDate(todayEnglish.year, todayEnglish.month + 1, todayEnglish.day)

                    if ((todayNepali.year == year) && (todayNepali.month == month) && (todayNepali.day == day)) {
                        status = true
                    }
                } else {
                    if (checkNumberOfDayInMonth(month, day)) {
                        status = true
                    }
                }
            } catch (e: Exception) {
                status = false
            } finally {
                return status
            }
        }

        private fun checkNumberOfDayInMonth(month: Int, day: Int): Boolean {
            when (month) {
                0 -> {
                    return day <= 31
                }
                1 -> {
                    return day <= 28
                }
                2 -> {
                    return day <= 31
                }
                3 -> {
                    return day <= 30
                }
                4 -> {
                    return day <= 31
                }
                5 -> {
                    return day <= 30
                }
                6 -> {
                    return day <= 31
                }
                7 -> {
                    return day <= 31
                }
                8 -> {
                    return day <= 30
                }
                9 -> {
                    return day <= 31
                }
                10 -> {
                    return day <= 30
                }
                11 -> {
                    return day <= 31
                }
                else -> {
                    return false
                }
            }
        }
    }
}
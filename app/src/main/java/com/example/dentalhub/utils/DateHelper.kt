package com.example.dentalhub.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateHelper {
    companion object {
        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            return sdf.format(Date())
        }

        fun formatDate(date: String): String{
            val dateObj = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(date)
            val sdf = SimpleDateFormat("yyyy-MMM dd", Locale.US)
            return sdf.format(dateObj)
        }
    }
}


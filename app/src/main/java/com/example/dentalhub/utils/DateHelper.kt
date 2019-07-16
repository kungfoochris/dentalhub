package com.example.dentalhub.utils

import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            return sdf.format(Date())
        }
    }
}


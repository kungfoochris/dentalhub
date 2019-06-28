package com.example.dentalhub.utils

import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object{
        fun getCurrentDate(): String{
            val sdf = SimpleDateFormat("yyyy-mm-dd")
            return sdf.format(Date())
        }
    }
}


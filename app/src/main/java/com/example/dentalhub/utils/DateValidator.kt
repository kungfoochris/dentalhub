package com.example.dentalhub.utils

import java.text.ParseException
import java.text.SimpleDateFormat

class DateValidator {
    companion object {
        fun isValid(date: String): Boolean {
            var status = false
            val format = SimpleDateFormat("yyyy-MM-dd")
            format.isLenient = false
            try {
                format.parse(date)
                status = true
            } catch (e: ParseException) {
                status = false
            } finally {
                return status
            }

        }
    }
}
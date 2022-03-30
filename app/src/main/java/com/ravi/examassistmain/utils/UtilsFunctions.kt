package com.ravi.examassistmain.utils

import java.text.SimpleDateFormat
import java.util.*

class UtilsFunctions {
    companion object {
        fun getDate(milliSeconds: Long, dateFormat: String?): String? {
            val formatter = SimpleDateFormat(dateFormat)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }
    }
}
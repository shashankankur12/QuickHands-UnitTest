package com.quickhandslogistics.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        const val PATTERN_NORMAL = "EEEE MMMM dd, yyyy"
        fun getDateString(pattern: String, date: Date): String {
            val dateFormat = SimpleDateFormat(pattern)
            return dateFormat.format(date)
        }

        fun isCurrentDate(selectedTime: Long): Boolean {
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = selectedTime
            val currentCalendar = Calendar.getInstance()

            return selectedCalendar[Calendar.DAY_OF_YEAR] == currentCalendar[Calendar.DAY_OF_YEAR] && selectedCalendar[Calendar.YEAR] == currentCalendar[Calendar.YEAR]
        }
    }
}
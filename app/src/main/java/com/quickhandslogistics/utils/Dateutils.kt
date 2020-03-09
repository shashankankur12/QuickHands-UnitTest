package com.quickhandslogistics.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        const val PATTERN_NORMAL = "MMM dd, yyyy"
        fun getDateString(pattern: String, date: Date): String {
            val dateFormat = SimpleDateFormat(pattern)
            return dateFormat.format(date)
        }
    }
}
package com.quickhandslogistics.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        const val PATTERN_NORMAL = "EEEE MMMM dd, yyyy"
        const val PATTERN_API_REQUEST_PARAMETER = "yyyy-MM-dd"
        const val PATTERN_API_RESPONSE = "yyyy-mm-dd'T'hh:mm:ss.SSS'Z'"
        private const val PATTERN_TIME = "hh:mm a"

        fun getDateString(pattern: String, date: Date): String {
            val dateFormat = SimpleDateFormat(pattern)
            return dateFormat.format(date)
        }

        fun getUTCDateString(pattern: String, date: Date): String {
            val dateFormat = SimpleDateFormat(pattern)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return dateFormat.format(date)
        }

        fun changeDateString(
            patternFrom: String,
            patternTo: String,
            dateString: String = ""
        ): String {
            val dateFormatFrom = SimpleDateFormat(patternFrom)
            val dateFormatTo = SimpleDateFormat(patternTo)
            try {
                val date = dateFormatFrom.parse(dateString)
                date?.let {
                    return dateFormatTo.format(date)
                }
            } catch (e: ParseException) {
            }
            return dateString
        }

        fun getMillisecondsFromDateString(pattern: String, dateString: String?): Long {
            val dateFormatFrom = SimpleDateFormat(pattern)
            dateString?.let {
                val date = dateFormatFrom.parse(dateString)
                date?.let {
                    return date.time
                }
            }
            return 0
        }

        fun isCurrentDate(selectedTime: Long): Boolean {
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = selectedTime
            val currentCalendar = Calendar.getInstance()

            return selectedCalendar[Calendar.DAY_OF_YEAR] == currentCalendar[Calendar.DAY_OF_YEAR] && selectedCalendar[Calendar.YEAR] == currentCalendar[Calendar.YEAR]
        }

        fun isFutureDate(selectedTime: Long): Boolean {
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = selectedTime
            val currentCalendar = Calendar.getInstance()

            return selectedCalendar.after(currentCalendar)
        }

        fun convertDateStringToTime(
            patternDate: String,
            dateString: String? = ""
        ): String {
            val dateStringValue = ValueUtils.getDefaultOrValue(dateString)

            val dateFormatFrom = SimpleDateFormat(patternDate)
            dateFormatFrom.timeZone = TimeZone.getTimeZone("UTC")
            val dateFormatTo = SimpleDateFormat(PATTERN_TIME)
            try {
                val date = dateFormatFrom.parse(dateStringValue)
                date?.let {
                    return dateFormatTo.format(date)
                }
            } catch (e: ParseException) {
            }
            return dateStringValue
        }

    }
}
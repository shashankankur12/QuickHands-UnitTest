package com.quickhandslogistics.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        const val PATTERN_NORMAL = "EEEE MMMM dd, yyyy"
        const val PATTERN_API_REQUEST_PARAMETER = "yyyy-MM-dd"
        const val PATTERN_API_RESPONSE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
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

        fun changeDateString(patternFrom: String, patternTo: String, dateString: String = ""): String {
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

        fun convertDateStringToTime(patternDate: String, dateString: String? = ""): String {
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

        fun convertUTCDateStringToLocalDateString(pattern: String, dateString: String? = ""): String {
            val dateStringValue = ValueUtils.getDefaultOrValue(dateString)

            val dateFormatFrom = SimpleDateFormat(pattern)
            dateFormatFrom.timeZone = TimeZone.getTimeZone("UTC")
            val dateFormatTo = SimpleDateFormat(pattern)
            try {
                val date = dateFormatFrom.parse(dateStringValue)
                date?.let {
                    return dateFormatTo.format(date)
                }
            } catch (e: ParseException) {
            }
            return dateStringValue
        }

        fun convertMillisecondsToTimeString(milliseconds: Long): String {
            val dateFormatTo = SimpleDateFormat(PATTERN_TIME)
            return dateFormatTo.format(Date(milliseconds))
        }

        fun convertMillisecondsToUTCTimeString(milliseconds: String?): String? {
            return try {
                val dateFormatFrom = SimpleDateFormat(PATTERN_API_RESPONSE)
                dateFormatFrom.timeZone = TimeZone.getTimeZone("UTC")
                val formattedDateString =
                    dateFormatFrom.format(Date(milliseconds!!.toLong() * 1000L))
                val formattedDate = dateFormatFrom.parse(formattedDateString)

                val dateFormatTo = SimpleDateFormat(PATTERN_TIME)
                dateFormatTo.format(formattedDate)
            } catch (e: Exception) {
                milliseconds
            }
        }

        fun convertUTCDateStringToMilliseconds(patternDate: String, dateString: String? = ""): Long {
            val dateStringValue = ValueUtils.getDefaultOrValue(dateString)

            val dateFormatFrom = SimpleDateFormat(patternDate)
            dateFormatFrom.timeZone = TimeZone.getTimeZone("UTC")
            try {
                val date = dateFormatFrom.parse(dateStringValue)
                date?.let {
                    return date.time
                }
            } catch (e: ParseException) {
            }
            return 0
        }

        fun isFutureTime(beforeTime: Long, afterTime: Long): Boolean {
            val beforeCalendar = Calendar.getInstance()
            beforeCalendar.timeInMillis = beforeTime
            val afterCalendar = Calendar.getInstance()
            afterCalendar.timeInMillis = afterTime

            val isPastHour = beforeCalendar[Calendar.HOUR_OF_DAY] < afterCalendar[Calendar.HOUR_OF_DAY]
            return if (isPastHour) {
                true
            } else {
                (beforeCalendar[Calendar.HOUR_OF_DAY] == afterCalendar[Calendar.HOUR_OF_DAY] && beforeCalendar[Calendar.MINUTE] < afterCalendar[Calendar.MINUTE])
            }
        }
    }
}
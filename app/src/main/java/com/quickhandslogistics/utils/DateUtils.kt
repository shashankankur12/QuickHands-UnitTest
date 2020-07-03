package com.quickhandslogistics.utils

import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.dashboard.ShiftDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        const val PATTERN_NORMAL = "EEEE MMMM dd, yyyy"
        const val PATTERN_API_REQUEST_PARAMETER = "yyyy-MM-dd"
        const val PATTERN_API_RESPONSE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val PATTERN_DATE_DISPLAY = "dd MMM yyyy"
        const val PATTERN_DATE_TIME_DISPLAY = "dd MMM yyyy, HH:mm a"
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

        fun changeUTCDateStringToLocalDateString(patternFrom: String, patternTo: String, dateString: String = ""): String {
            val dateFormatFrom = SimpleDateFormat(patternFrom)
            dateFormatFrom.timeZone = TimeZone.getTimeZone("UTC")
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

        fun getDateFromDateString(pattern: String, dateString: String?): Date {
            val dateFormatFrom = SimpleDateFormat(pattern)
            dateString?.let {
                val date = dateFormatFrom.parse(dateString)
                date?.let {
                    return date
                }
            }
            return Date()
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

        fun isSameDates(date1: Date, date2: Date): Boolean {
            val calendar1 = Calendar.getInstance()
            calendar1.time = date1
            val calendar2 = Calendar.getInstance()
            calendar2.time = date2

            return calendar1[Calendar.DAY_OF_YEAR] == calendar2[Calendar.DAY_OF_YEAR] && calendar1[Calendar.YEAR] == calendar2[Calendar.YEAR]
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
                    dateFormatFrom.format(Date(milliseconds!!.toLong()))
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

        fun getCurrentDateStringByEmployeeShift(sharedPref: SharedPref, pattern: String = PATTERN_API_REQUEST_PARAMETER, originalDate: Date = Date()): String {
            var dateString = ""
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            leadProfile?.let {
                when (leadProfile.shift) {
                    AppConstant.EMPLOYEE_SHIFT_MORNING -> {
                        val shiftDetail = leadProfile.buildingDetailData?.morningShift
                        dateString = calculateDateByShiftStartTime(shiftDetail, pattern, originalDate)
                    }
                    AppConstant.EMPLOYEE_SHIFT_SWING -> {
                        val shiftDetail = leadProfile.buildingDetailData?.swingShift
                        dateString = calculateDateByShiftStartTime(shiftDetail, pattern, originalDate)
                    }
                    AppConstant.EMPLOYEE_SHIFT_NIGHT -> {
                        val shiftDetail = leadProfile.buildingDetailData?.nightShift
                        dateString = calculateDateByShiftStartTime(shiftDetail, pattern, originalDate)
                    }
                }
            }

            if (dateString.isEmpty()) {
                dateString = getDateString(pattern, originalDate)
            }
            return dateString
        }

        private fun calculateDateByShiftStartTime(shiftDetail: ShiftDetail?, pattern: String, originalDate: Date): String {
            var dateString = ""

            //Create original date calendar instance
            val calendar = Calendar.getInstance()
            calendar.time = originalDate

            shiftDetail?.let {
                val startTime = shiftDetail.startTime
                startTime?.let {

                    //Create shift start time calendar instance with original date
                    val startTimeCalendar = Calendar.getInstance();
                    startTimeCalendar.time = Date(startTime)
                    startTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE))

                    // Check if shift start or not. If not then show pass previous date
                    if (startTimeCalendar.timeInMillis > calendar.timeInMillis) {
                        calendar.add(Calendar.DATE, -1)
                    }
                    dateString = getDateString(pattern, calendar.time)
                }
            }
            return dateString
        }
    }
}
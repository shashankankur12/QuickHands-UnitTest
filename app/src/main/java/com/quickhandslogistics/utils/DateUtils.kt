package com.quickhandslogistics.utils

import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.dashboard.ShiftDetail
import com.quickhandslogistics.utils.AppConstant.Companion.EMPLOYEE_SHIFT_NIGHT
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        const val PATTERN_NORMAL = "EEEE, MMMM dd, yyyy"
        const val PATTERN_NORMAL_Week = "EEEE, MMMM dd, yyyy: hh:mm a"
        const val PATTERN_API_REQUEST_PARAMETER = "yyyy-MM-dd"
        const val PATTERN_API_RESPONSE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val PATTERN_DATE_DISPLAY = "dd MMM yyyy"
        const val PATTERN_DATE_DISPLAY_SHEET= "dd/MM/yyyy"
        const val PATTERN_DATE_DISPLAY_CUSTOMER_SHEET= "MM/dd/yyyy"
        const val PATTERN_MONTH_DAY_DISPLAY = "MMMM dd, yyyy"
        const val PATTERN_DATE_TIME_DISPLAY = "dd MMM yyyy, HH:mm a"
        private const val PATTERN_TIME = "hh:mm a"

        var sharedPref: SharedPref = SharedPref.getInstance()

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
            currentCalendar.time = getCurrentDateByEmployeeShift()

            return selectedCalendar[Calendar.DAY_OF_YEAR] == currentCalendar[Calendar.DAY_OF_YEAR] && selectedCalendar[Calendar.YEAR] == currentCalendar[Calendar.YEAR]
        }

        fun isFutureDate(selectedTime: Long): Boolean {
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = selectedTime
            val currentCalendar = Calendar.getInstance()
            currentCalendar.time = getCurrentDateByEmployeeShift()

            return selectedCalendar.after(currentCalendar)
        }

        fun isSameDates(date1: Date, date2: Date): Boolean {
            val calendar1 = Calendar.getInstance()
            calendar1.time = date1
            val calendar2 = Calendar.getInstance()
            calendar2.time = date2

            return calendar1[Calendar.DAY_OF_YEAR] == calendar2[Calendar.DAY_OF_YEAR] && calendar1[Calendar.YEAR] == calendar2[Calendar.YEAR]
        }

        fun isTwoHourFromCurrentTime(milliseconds: Long): Boolean {
            val calendar1 = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            calendar1.time = getCurrentDateByEmployeeShift(Date())
            calendar2.time = Date(milliseconds)
            calendar2.add(Calendar.HOUR_OF_DAY, 2)

            return calendar2.timeInMillis >calendar1.timeInMillis
        }

        fun getTimeMilisAccordingShift(): Long {
            val calendar1 = Calendar.getInstance()
            calendar1.time = getCurrentDateByEmployeeShift(Date())

            return calendar1.timeInMillis
        }

        fun getDateTimeCalculeted(morningPunchIn: String, eveningPunchOut: String): String {
            val punchIn= convertUTCDateStringToMilliseconds(PATTERN_API_RESPONSE, morningPunchIn)
            val punchOut= convertUTCDateStringToMilliseconds(PATTERN_API_RESPONSE, eveningPunchOut)
            val diffrence=punchOut-punchIn

            return String.format("%s H %s M",
                (diffrence / (1000 * 60 * 60) % 24),
                (diffrence / (1000 * 60) % 60)
            )
        }


        fun getDateTimeCalculetedLong(morningPunchIn: Long, eveningPunchOut: Long): String {
            val diffrence=eveningPunchOut-morningPunchIn

            return String.format("%s H %s M",
                (diffrence / (1000 * 60 * 60) % 24),
                (diffrence / (1000 * 60) % 60)
            )
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

        fun convertMillisecondsToUTCDateString(patternDate: String, milliseconds: Long?): String {
            var dateString = ""
            try {
              /*  val dateFormatFrom = SimpleDateFormat(PATTERN_API_RESPONSE)
                val formattedDateString = dateFormatFrom.format(Date(milliseconds!!.toLong()))
                val formattedDate = dateFormatFrom.parse(formattedDateString)
*/
                val dateFormatTo = SimpleDateFormat(patternDate)
                dateFormatTo.timeZone = TimeZone.getTimeZone("UTC")
                dateString = dateFormatTo.format(Date(milliseconds!!))
            } catch (e: Exception) {

            }
            return dateString
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

        fun getCurrentDateStringByEmployeeShift(pattern: String = PATTERN_API_REQUEST_PARAMETER, originalDate: Date = Date()): String {
            var dateString = ""
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            leadProfile?.let {
                var shiftDetail: ShiftDetail? = null
                when (leadProfile.shift) {
                    AppConstant.EMPLOYEE_SHIFT_MORNING -> {
                        shiftDetail = leadProfile.buildingDetailData?.morningShift
                    }
                    AppConstant.EMPLOYEE_SHIFT_SWING -> {
                        shiftDetail = leadProfile.buildingDetailData?.swingShift
                    }
                    AppConstant.EMPLOYEE_SHIFT_NIGHT -> {
                        shiftDetail = leadProfile.buildingDetailData?.nightShift
                    }
                }
                val date = calculateDateByShiftStartTime(
                    shiftDetail,
                    originalDate,
                    leadProfile.shift
                )
                dateString = getDateString(pattern, date)
            }

            if (dateString.isEmpty()) {
                dateString = getDateString(pattern, originalDate)
            }
            return dateString
        }

        fun getCurrentDateByEmployeeShift(originalDate: Date = Date()): Date {
            var date: Date? = null
            val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            leadProfile?.let {
                var shiftDetail: ShiftDetail? = null
                when (leadProfile.shift) {
                    AppConstant.EMPLOYEE_SHIFT_MORNING -> {
                        shiftDetail = leadProfile.buildingDetailData?.morningShift
                    }
                    AppConstant.EMPLOYEE_SHIFT_SWING -> {
                        shiftDetail = leadProfile.buildingDetailData?.swingShift
                    }
                    AppConstant.EMPLOYEE_SHIFT_NIGHT -> {
                        shiftDetail = leadProfile.buildingDetailData?.nightShift
                    }
                }
                date = calculateDateByShiftStartTime(shiftDetail, originalDate, leadProfile.shift)
            }

            return date!!
        }

        private fun calculateDateByShiftStartTime(shiftDetail: ShiftDetail?, originalDate: Date, shift: String?): Date {
            var date: Date? = null

            //Create original date calendar instance
            val calendar = Calendar.getInstance()
            calendar.time = originalDate

            if(shift.equals(EMPLOYEE_SHIFT_NIGHT))
            shiftDetail?.let {

                val startTime = shiftDetail.startTime
                startTime?.let {

                    //Create shift start time calendar instance with original date
                    val startTimeCalendar = Calendar.getInstance();
                    startTimeCalendar.time = Date(startTime)
                    startTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE))

                    val calendarMaxLimit = Calendar.getInstance()
                    calendarMaxLimit.time = originalDate
                    calendarMaxLimit.set(Calendar.HOUR_OF_DAY, 12)
                    calendarMaxLimit.set(Calendar.MINUTE, 0)
                    calendarMaxLimit.set(Calendar.SECOND, 0)

                    // Check if shift start or not. If not then show pass previous date
                    if (startTimeCalendar.timeInMillis > calendar.timeInMillis && calendar.before(calendarMaxLimit)) {
                        calendar.add(Calendar.DATE, -1)
                    }
                    date = calendar.time
                }
            }
            if (date == null) {
                date = originalDate
            }

            return date!!
        }

    }

}
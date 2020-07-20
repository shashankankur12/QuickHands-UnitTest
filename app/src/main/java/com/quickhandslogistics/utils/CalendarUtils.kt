package com.quickhandslogistics.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.quickhandslogistics.R
import kotlinx.android.synthetic.main.item_calendar_view.view.*
import java.util.*

object CalendarUtils {

    fun initializeCalendarView(context: Context, calendarView: SingleRowCalendar, dates: List<Date>, listener: CalendarSelectionListener) {
        val myCalendarViewManager = object : CalendarViewManager {
            override fun bindDataToCalendarView(holder: SingleRowCalendarAdapter.CalendarViewHolder, date: Date, position: Int, isSelected: Boolean) {
                holder.itemView.tv_date_calendar_item.text = com.michalsvec.singlerowcalendar.utils.DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = com.michalsvec.singlerowcalendar.utils.DateUtils.getDay3LettersName(date)

                if (isSelected) {
                    holder.itemView.tv_date_calendar_item.setTextColor(Color.WHITE)
                    holder.itemView.tv_date_calendar_item.setBackgroundResource(R.drawable.selected_calendar_item_background)
                } else {
                    holder.itemView.tv_date_calendar_item.setTextColor(ContextCompat.getColor(context, R.color.detailHeader))
                    holder.itemView.tv_date_calendar_item.setBackgroundColor(Color.TRANSPARENT)
                }
            }

            override fun setCalendarViewResourceId(position: Int, date: Date, isSelected: Boolean): Int {
                return R.layout.item_calendar_view
            }
        }

        val myCalendarChangesObserver = object : CalendarChangesObserver {
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                if (isSelected) {
                    listener.onSelectCalendarDate(date, isSelected, position)
                }
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                return true
            }
        }

        calendarView.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(dates)
            init()
            scrollToPosition(dates.size - 1)
        }
    }

    /**
     * This method will return a Pair<List<Date>, Int>
     * First Item is List of dates starting from past 2 weeks to future 5 days.
     * Second Item is Current Date Position
     */
    fun getPastFutureCalendarDates(): Pair<List<Date>, Int> {
        val list: MutableList<Date> = mutableListOf()
        var currentDatePosition = 0

        val calendar = Calendar.getInstance()
        calendar.time = DateUtils.getCurrentDateByEmployeeShift()

        // Add Last Date to be displayed for the Picker
        calendar.add(Calendar.DAY_OF_YEAR, 10)
        val lastDate = calendar[Calendar.DATE]

        // Add Current Date to be displayed for the Picker
        calendar.add(Calendar.DAY_OF_YEAR, -10)
        val currentDate = calendar[Calendar.DATE]

        // Add First Date to be displayed for the Picker
        calendar.add(Calendar.DAY_OF_YEAR, -10)
        //calendar.add(Calendar.WEEK_OF_YEAR, -2)

        while (lastDate != calendar[Calendar.DATE]) {
            calendar.add(Calendar.DATE, 1)
            //if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            list.add(calendar.time)
            if (currentDate == calendar[Calendar.DATE]) {
                currentDatePosition = list.size - 1
            }
            //}
        }
        return Pair(list, currentDatePosition)
    }

    /**
     * This method will return a List of dates starting from past 2 weeks to current date.
     */
    fun getPastCalendarDates(): List<Date> {
        val list: MutableList<Date> = mutableListOf()

        val calendar = Calendar.getInstance()
        calendar.time = DateUtils.getCurrentDateByEmployeeShift()

        // Add Current Date to be displayed for the Picker
        val currentDate = calendar[Calendar.DATE]

        // Add First Date to be displayed for the Picker
        calendar.add(Calendar.DAY_OF_YEAR, -10)
        //calendar.add(Calendar.WEEK_OF_YEAR, -2)

        while (currentDate != calendar[Calendar.DATE]) {
            calendar.add(Calendar.DATE, 1)
            //if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            list.add(calendar.time)
            //}
        }
        return list
    }

    fun getSelectedDatePosition(dates: List<Date>, selectedDateString: String?): Int {
        var selectedDatePosition = -1

        if (!selectedDateString.isNullOrEmpty()) {
            val selectedDate = DateUtils.getDateFromDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedDateString)
            for (date in dates) {
                if (DateUtils.isSameDates(date, selectedDate)) {
                    selectedDatePosition = dates.indexOf(date)
                    break
                }
            }
        }
        return selectedDatePosition
    }

    interface CalendarSelectionListener {
        fun onSelectCalendarDate(
            date: Date,
            selected: Boolean,
            position: Int
        )
    }
}
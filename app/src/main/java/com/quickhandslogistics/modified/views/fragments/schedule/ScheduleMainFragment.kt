package com.quickhandslogistics.modified.views.fragments.schedule

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.data.schedule.ScheduleMainResponse
import com.quickhandslogistics.modified.presenters.schedule.ScheduleMainPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.ScheduleMainPagerAdapter
import kotlinx.android.synthetic.main.calendar_item.view.*
import kotlinx.android.synthetic.main.fragment_schedule_main.*
import java.util.*

class ScheduleMainFragment : BaseFragment(), ScheduleMainContract.View {

    private lateinit var availableDates: List<Date>

    private lateinit var scheduleMainPresenter: ScheduleMainPresenter
    private lateinit var sectionsMainPagerAdapter: ScheduleMainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleMainPresenter = ScheduleMainPresenter(this)

        // Setup DatePicker Dates
        availableDates = getAvailableDates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentDate = Date()

        sectionsMainPagerAdapter =
            ScheduleMainPagerAdapter(
                childFragmentManager,
                resources,
                currentDate.time
            )
        viewPagerSchedule.adapter = sectionsMainPagerAdapter
        tabLayoutSchedule.setupWithViewPager(viewPagerSchedule)

        initializeCalendar()

        Handler().postDelayed({
            main_single_row_calendar.select(availableDates.size - 1)
        }, 500)
    }

    private fun initializeCalendar() {
        val myCalendarViewManager = object : CalendarViewManager {
            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

                if (isSelected) {
                    holder.itemView.tv_date_calendar_item.setTextColor(Color.WHITE)
                    holder.itemView.tv_date_calendar_item.setBackgroundResource(R.drawable.selected_calendar_item_background)
                } else {
                    holder.itemView.tv_date_calendar_item.setTextColor(Color.BLACK)
                    holder.itemView.tv_date_calendar_item.setBackgroundColor(Color.TRANSPARENT)
                }
            }

            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                return R.layout.calendar_item
            }
        }

        val myCalendarChangesObserver = object : CalendarChangesObserver {
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                scheduleMainPresenter.showSchedulesByDate(date)
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                return true
            }
        }

        main_single_row_calendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(availableDates)
            init()
            scrollToPosition(availableDates.size - 1)
        }
    }

    override fun showDateString(dateString: String, timeInMills: Long) {
        textViewDate.text = dateString
    }

    override fun showScheduleData(
        selectedDate: Date,
        scheduleMainResponse: ScheduleMainResponse
    ) {
        tabLayoutSchedule.getTabAt(0)?.select()
        sectionsMainPagerAdapter.updateScheduleList(selectedDate, scheduleMainResponse)
    }

    private fun getAvailableDates(): List<Date> {
        val list: MutableList<Date> = mutableListOf()

        val calendar = Calendar.getInstance()
        val currentDate = calendar[Calendar.DATE]
        calendar.add(Calendar.WEEK_OF_YEAR, -2)

        list.add(calendar.time)
        while (currentDate != calendar[Calendar.DATE]) {
            calendar.add(Calendar.DATE, 1)
            list.add(calendar.time)
        }
        return list
    }
}

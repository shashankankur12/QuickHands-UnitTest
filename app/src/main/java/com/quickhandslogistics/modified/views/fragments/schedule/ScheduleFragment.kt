package com.quickhandslogistics.modified.views.fragments.schedule

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.schedule.SchedulePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.activities.schedule.MarkAttendanceActivity
import com.quickhandslogistics.modified.views.activities.schedule.ScheduledWorkItemDetailActivity
import com.quickhandslogistics.modified.views.adapters.ScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import kotlinx.android.synthetic.main.calendar_item.view.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.util.*

class ScheduleFragment : BaseFragment(), ScheduleContract.View.OnAdapterItemClickListener,
    ScheduleContract.View, View.OnClickListener {

    private lateinit var schedulePresenter: SchedulePresenter
    private lateinit var scheduledWorkItemAdapter: ScheduledWorkItemAdapter
    private var onScheduleFragmentInteractionListener: ScheduleMainContract.View.OnScheduleFragmentInteractionListener? =
        null

    private var selectedTime: Long = 0
    private var isCurrentDate: Boolean = true

    private lateinit var availableDates: List<Date>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is ScheduleMainContract.View.OnScheduleFragmentInteractionListener) {
            onScheduleFragmentInteractionListener =
                parentFragment as ScheduleMainContract.View.OnScheduleFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schedulePresenter = SchedulePresenter(this, resources, sharedPref)

        // Setup DatePicker Dates
        selectedTime = Date().time
        availableDates = getAvailableDates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewSchedule.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            scheduledWorkItemAdapter =
                ScheduledWorkItemAdapter(resources, this@ScheduleFragment)
            adapter = scheduledWorkItemAdapter
        }

        initializeCalendar()

        singleRowCalendarSchedule.select(availableDates.size - 1)
        buttonMarkAttendance.setOnClickListener(this)
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
                    holder.itemView.tv_date_calendar_item.setTextColor(
                        ContextCompat.getColor(
                            fragmentActivity!!,
                            R.color.detailHeader
                        )
                    )
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
                if (isSelected) {
                    schedulePresenter.getScheduledWorkItemsByDate(date)
                }
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                return true
            }
        }

        singleRowCalendarSchedule.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(availableDates)
            init()
            scrollToPosition(availableDates.size - 1)
        }
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

    /*
    * Native Views Listeners
    */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonMarkAttendance.id -> {
                    startIntent(MarkAttendanceActivity::class.java)
                }
            }
        }
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onWorkItemClick() {
        val bundle = Bundle()
        bundle.putBoolean(ScheduledWorkItemDetailActivity.ARG_ALLOW_UPDATE, isCurrentDate)
        startIntent(ScheduledWorkItemDetailActivity::class.java, bundle = bundle)
    }

    override fun onLumperImagesClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    /*
    * Presenter Listeners
    */
    override fun showDateString(dateString: String) {
        textViewDate.text = dateString
    }

    override fun showScheduleData(selectedDate: Date, workItemsList: ArrayList<WorkItemDetail>) {
        selectedTime = selectedDate.time
        isCurrentDate = com.quickhandslogistics.utils.DateUtils.isCurrentDate(selectedTime)
        scheduledWorkItemAdapter.updateList(workItemsList)

        buttonMarkAttendance.visibility = if (isCurrentDate) View.VISIBLE else View.GONE
        textViewEmptyData.visibility = View.GONE
        recyclerViewSchedule.visibility = View.VISIBLE
        textViewDate.visibility = View.VISIBLE
    }

    override fun hideProgressDialog() {
        onScheduleFragmentInteractionListener?.hideProgressDialog()
    }

    override fun showProgressDialog(message: String) {
        onScheduleFragmentInteractionListener?.showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        TODO("Not yet implemented")
    }

    override fun fetchUnsScheduledWorkItems() {
        onScheduleFragmentInteractionListener?.fetchUnsScheduledWorkItems()
    }

    override fun showEmptyData() {
        textViewEmptyData.visibility = View.VISIBLE
        recyclerViewSchedule.visibility = View.GONE
        textViewDate.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScheduleFragment()
    }
}
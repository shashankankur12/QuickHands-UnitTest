package com.quickhandslogistics.modified.views.schedule

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.presenters.schedule.SchedulePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.common.DisplayLumpersListActivity
import com.quickhandslogistics.modified.adapters.schedule.ScheduleAdapter
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_ALLOW_UPDATE
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_IDENTITY
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.item_calendar_view.view.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.util.*

class ScheduleFragment : BaseFragment(), ScheduleContract.View,
    ScheduleContract.View.OnAdapterItemClickListener {

    private lateinit var schedulePresenter: SchedulePresenter
    private lateinit var scheduleAdapter: ScheduleAdapter
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
        schedulePresenter = SchedulePresenter(this, resources)

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
            scheduleAdapter = ScheduleAdapter(resources, this@ScheduleFragment)
            adapter = scheduleAdapter
        }

        initializeCalendar()

        singleRowCalendarSchedule.select(availableDates.size - 1)
    }

    private fun initializeCalendar() {
        val myCalendarViewManager = object : CalendarViewManager {
            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date, position: Int, isSelected: Boolean
            ) {
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

                if (isSelected) {
                    holder.itemView.tv_date_calendar_item.setTextColor(Color.WHITE)
                    holder.itemView.tv_date_calendar_item.setBackgroundResource(R.drawable.selected_calendar_item_background)
                } else {
                    holder.itemView.tv_date_calendar_item.setTextColor(
                        ContextCompat.getColor(fragmentActivity!!, R.color.detailHeader)
                    )
                    holder.itemView.tv_date_calendar_item.setBackgroundColor(Color.TRANSPARENT)
                }
            }

            override fun setCalendarViewResourceId(
                position: Int, date: Date, isSelected: Boolean
            ): Int {
                return R.layout.item_calendar_view
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

        while (currentDate != calendar[Calendar.DATE]) {
            calendar.add(Calendar.DATE, 1)
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
            ) {
                list.add(calendar.time)
            }
        }
        return list
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            fetchScheduledWorkItems()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        schedulePresenter.onDestroy()
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onScheduleItemClick(scheduleDetail: ScheduleDetail) {
        val bundle = Bundle()
        bundle.putBoolean(ARG_ALLOW_UPDATE, isCurrentDate)
        bundle.putString(ARG_SCHEDULE_IDENTITY, scheduleDetail.scheduleIdentity)
        bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(
            ScheduleDetailActivity::class.java, bundle = bundle,
            requestCode = AppConstant.REQUEST_CODE_CHANGED
        )
    }

    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(DisplayLumpersListActivity.ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }

    /*
    * Presenter Listeners
    */
    override fun showDateString(dateString: String) {
        textViewDate.text = dateString
    }

    override fun showScheduleData(selectedDate: Date, workItemsList: ArrayList<ScheduleDetail>) {
        selectedTime = selectedDate.time
        isCurrentDate = com.quickhandslogistics.utils.DateUtils.isCurrentDate(selectedTime)
        scheduleAdapter.updateList(workItemsList)

        textViewEmptyData.visibility = View.GONE
        recyclerViewSchedule.visibility = View.VISIBLE
        textViewDate.visibility = View.VISIBLE
    }

   /* override fun hideProgressDialog() {
        onScheduleFragmentInteractionListener?.hideProgressDialog()
    }

    override fun showProgressDialog(message: String) {
        onScheduleFragmentInteractionListener?.showProgressDialog(message)
    }*/

    override fun showAPIErrorMessage(message: String) {
        recyclerViewSchedule.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun fetchUnsScheduledWorkItems() {
        onScheduleFragmentInteractionListener?.fetchUnScheduledWorkItems()
    }

    override fun showEmptyData() {
        textViewEmptyData.visibility = View.VISIBLE
        recyclerViewSchedule.visibility = View.GONE
        textViewDate.visibility = View.GONE
    }

    fun fetchScheduledWorkItems() {
        if (singleRowCalendarSchedule.getSelectedDates().isNotEmpty()) {
            schedulePresenter.getScheduledWorkItemsByDate(singleRowCalendarSchedule.getSelectedDates()[0])
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ScheduleFragment()
    }
}
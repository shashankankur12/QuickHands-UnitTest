package com.quickhandslogistics.modified.views.customerSheet

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.customerSheet.CustomerSheetPagerAdapter
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.contracts.customerSheet.CustomerSheetContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.modified.presenters.customerSheet.CustomerSheetPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.fragment_customer_sheet.*
import kotlinx.android.synthetic.main.item_calendar_view.view.*
import java.util.*

class CustomerSheetFragment : BaseFragment(), CustomerSheetContract.View {

    private var listener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private lateinit var customerSheetPresenter: CustomerSheetPresenter
    private lateinit var adapter: CustomerSheetPagerAdapter

    private var selectedTime: Long = 0

    private lateinit var availableDates: List<Date>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerSheetPresenter = CustomerSheetPresenter(this, resources, sharedPref)

        // Setup DatePicker Dates
        selectedTime = Date().time
        availableDates = getAvailableDates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CustomerSheetPagerAdapter(childFragmentManager, resources)
        viewPagerCustomerSheet.offscreenPageLimit = adapter.count
        viewPagerCustomerSheet.adapter = adapter
        tabLayoutCustomerSheet.setupWithViewPager(viewPagerCustomerSheet)

        initializeCalendar()

        singleRowCalendarCustomerSheet.select(availableDates.size - 1)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)

        // Reset Whole Screen Data
        textViewBuildingName.text = ""
        textViewWorkItemsDate.text = ""
        textViewTotalCount.text = ""
    }

    override fun showCustomerSheets(data: WorkSheetListAPIResponse.Data) {
        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(data.inProgress!!)
        onGoingWorkItems.addAll(data.onHold!!)
        onGoingWorkItems.addAll(data.scheduled!!)

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(onGoingWorkItems)
        allWorkItems.addAll(data.cancelled!!)
        allWorkItems.addAll(data.completed!!)
        textViewTotalCount.text =
            String.format(getString(R.string.total_containers_s), allWorkItems.size)

        adapter.updateCustomerSheetList(onGoingWorkItems, data.cancelled!!, data.completed!!)
    }

    override fun showHeaderInfo(buildingName: String, date: String) {
        textViewBuildingName.text = buildingName.capitalize()
        textViewWorkItemsDate.text = date
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
                    customerSheetPresenter.getCustomerSheetByDate(date)
                }
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                return true
            }
        }

        singleRowCalendarCustomerSheet.apply {
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

    override fun onDestroy() {
        super.onDestroy()
        customerSheetPresenter.onDestroy()
    }
}

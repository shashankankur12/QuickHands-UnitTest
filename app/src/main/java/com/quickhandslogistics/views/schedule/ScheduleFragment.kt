package com.quickhandslogistics.views.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.schedule.ScheduleAdapter
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.presenters.schedule.SchedulePresenter
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CalendarUtils
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.common.DisplayLumpersListActivity
import com.quickhandslogistics.views.lumperSheet.LumperSheetFragment
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.mainConstraintLayout
import kotlinx.android.synthetic.main.fragment_schedule.textViewDate
import kotlinx.android.synthetic.main.fragment_schedule.textViewEmptyData
import kotlinx.android.synthetic.main.fragment_schedule_time.*
import java.util.*

class ScheduleFragment : BaseFragment(), ScheduleContract.View, ScheduleContract.View.OnAdapterItemClickListener, CalendarUtils.CalendarSelectionListener {

    private var currentPageIndex: Int = 1
    private var nextPageIndex: Int = 1
    private var totalPagesCount: Int = 1

    private var selectedTime: Long = 0
    private var currentDatePosition: Int = 0
    private lateinit var availableDates: List<Date>
    private var workItemsList: ArrayList<ScheduleDetail> = ArrayList<ScheduleDetail>()
    private var selectedDate: Date= Date()
    private  var dateString: String ?= null
    private var isSavedState: Boolean = false
    private var datePosition: Int = 0


    private lateinit var schedulePresenter: SchedulePresenter
    private lateinit var scheduleAdapter: ScheduleAdapter

    companion object {
        const val ARG_ALLOW_UPDATE = "ARG_ALLOW_UPDATE"
        const val ARG_BUILDING_PARAMETERS = "ARG_BUILDING_PARAMETERS"
        const val ARG_BUILDING_PARAMETER_VALUES = "ARG_BUILDING_PARAMETER_VALUES"
        const val ARG_IS_FUTURE_DATE = "ARG_IS_FUTURE_DATE"

        const val ARG_SELECTED_DATE_MILLISECONDS = "ARG_SELECTED_DATE_MILLISECONDS"

        const val ARG_SCHEDULE_IDENTITY = "ARG_SCHEDULE_IDENTITY"

        const val ARG_WORK_ITEM_ID = "ARG_WORK_ITEM_ID"
        const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"
        const val ARG_WORK_ITEM_TYPE_DISPLAY_NAME = "ARG_WORK_ITEM_TYPE_DISPLAY_NAME"

        const val ARG_SCHEDULED_TIME_NOTES = "ARG_SCHEDULED_TIME_NOTES"
        const val ARG_SCHEDULED_TIME_LIST = "ARG_SCHEDULED_TIME_LIST"
        const val ARG_SCHEDULED_LUMPERS_COUNT = "ARG_SCHEDULED_LUMPERS_COUNT"

        const val WORK_ITEM_LIST = "WORK_ITEM_LIST"
        const val DATE = "DATE"
        const val DATE_SELECTED = "DATE_SELECTED"
        const val TOTAL_PAGE_COUNT = "TOTAL_PAGE_COUNT"
        const val NEXT_PAGE = "NEXT_PAGE"
        const val CURRENT_PAGE = "CURRENT_PAGE"
        const val SELECTED_DATE = "SELECTED_DATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schedulePresenter = SchedulePresenter(this, resources)

        // Setup Calendar Dates
        selectedTime = Date().time
        val pair = CalendarUtils.getPastFutureCalendarDates()
        availableDates = pair.first
        currentDatePosition = pair.second
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewSchedule.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            scheduleAdapter = ScheduleAdapter(resources, this@ScheduleFragment)
            adapter = scheduleAdapter
            addOnScrollListener(onScrollListener)
        }

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarSchedule, availableDates, this)
        savedInstanceState?.also {
            isSavedState=true
            if (savedInstanceState.containsKey(SELECTED_DATE)) {
                datePosition = savedInstanceState.getInt(SELECTED_DATE)!!
                singleRowCalendarSchedule.select(datePosition)
            }
            if (savedInstanceState.containsKey(CURRENT_PAGE)) {
                currentPageIndex = savedInstanceState.getInt(CURRENT_PAGE)!!
            }
            if (savedInstanceState.containsKey(NEXT_PAGE)) {
                nextPageIndex = savedInstanceState.getInt(NEXT_PAGE)!!
            }
            if (savedInstanceState.containsKey(TOTAL_PAGE_COUNT)) {
                totalPagesCount = savedInstanceState.getInt(TOTAL_PAGE_COUNT)!!
            }
            if(savedInstanceState.containsKey(DATE)) {
                selectedDate = savedInstanceState.getSerializable(LumperSheetFragment.DATE) as Date
            }
            if (savedInstanceState.containsKey(WORK_ITEM_LIST)) {
                workItemsList = savedInstanceState.getParcelableArrayList(WORK_ITEM_LIST)!!
                showScheduleData(
                    selectedDate,
                    workItemsList,
                    totalPagesCount,
                    nextPageIndex,
                    currentPageIndex
                )
            }
            if (savedInstanceState.containsKey(DATE_SELECTED)) {
                dateString = savedInstanceState.getString(DATE_SELECTED)!!
                showDateString(dateString!!)
            }
        } ?: run {
            isSavedState=false
            singleRowCalendarSchedule.select(if (currentDatePosition != 0) currentDatePosition else availableDates.size - 1)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        schedulePresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (workItemsList!=null)
        outState.putParcelableArrayList(WORK_ITEM_LIST, workItemsList)
        if (totalPagesCount!=null)
        outState.putInt(TOTAL_PAGE_COUNT, totalPagesCount)
        if (nextPageIndex!=null)
        outState.putInt(NEXT_PAGE, nextPageIndex)
        if (currentPageIndex!=null)
        outState.putInt(CURRENT_PAGE, currentPageIndex)
        if (selectedDate!=null)
        outState.putSerializable(DATE, selectedDate)
        if (dateString!=null)
        outState.putString(DATE_SELECTED, dateString)
        if (datePosition!=null)
        outState.putInt(SELECTED_DATE,datePosition)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            resetPaginationValues()
            fetchScheduledWorkItems()
        }
    }

    private fun fetchScheduledWorkItems() {
        if (singleRowCalendarSchedule.getSelectedDates().isNotEmpty()) {
            schedulePresenter.getScheduledWorkItemsByDate(singleRowCalendarSchedule.getSelectedDates()[0], currentPageIndex)
        }
    }

    private fun resetPaginationValues() {
        currentPageIndex = 1
        nextPageIndex = 1
        totalPagesCount = 1
    }

    /** Native Views Listeners */
    private val onScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView.layoutManager?.let { layoutManager ->
                if (layoutManager is LinearLayoutManager) {
                    val visibleItemCount: Int = layoutManager.childCount
                    val totalItemCount: Int = layoutManager.itemCount
                    val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                    if (currentPageIndex != totalPagesCount) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                            currentPageIndex = nextPageIndex
                            fetchScheduledWorkItems()
                        }
                    }
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showDateString(dateString: String) {
        this.dateString=dateString
        textViewDate.text = dateString
    }

    override fun showScheduleData(selectedDate: Date, workItemsList: ArrayList<ScheduleDetail>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int) {
        this.selectedDate=selectedDate
        this.workItemsList=workItemsList
        this.currentPageIndex=currentPageIndex

        selectedTime = selectedDate.time
        scheduleAdapter.updateList(workItemsList, currentPageIndex)

        if(!workItemsList.isNullOrEmpty()) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewSchedule.visibility = View.VISIBLE
            textViewDate.visibility = View.VISIBLE
        }else{
            showEmptyData()
        }

        this.totalPagesCount = totalPagesCount
        this.nextPageIndex = nextPageIndex
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewSchedule.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showEmptyData() {
        textViewEmptyData.visibility = View.VISIBLE
        recyclerViewSchedule.visibility = View.GONE
        textViewDate.visibility = View.GONE
        workItemsList.clear()
    }

    /** Adapter Listeners */
    override fun onScheduleItemClick(scheduleDetail: ScheduleDetail) {
        val bundle = Bundle()
        bundle.putBoolean(ARG_ALLOW_UPDATE, DateUtils.isCurrentDate(selectedTime))
        bundle.putBoolean(ARG_IS_FUTURE_DATE, DateUtils.isFutureDate(selectedTime))
        bundle.putString(ARG_SCHEDULE_IDENTITY, scheduleDetail.scheduleIdentity)
        bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(ScheduleDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(DisplayLumpersListActivity.ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(
        date: Date,
        selected: Boolean,
        position: Int
    ) {

        if (!isSavedState) {
            resetPaginationValues()
            schedulePresenter.getScheduledWorkItemsByDate(date, currentPageIndex)
        }
        isSavedState=false
        datePosition=position
    }
}
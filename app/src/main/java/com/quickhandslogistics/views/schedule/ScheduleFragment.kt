package com.quickhandslogistics.views.schedule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.schedule.ScheduleAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.schedule.ScheduleContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.PastFutureDates
import com.quickhandslogistics.data.schedule.ScheduleDetailData
import com.quickhandslogistics.presenters.schedule.SchedulePresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.common.DisplayLumpersListActivity
import kotlinx.android.synthetic.main.content_dashboard.*
import kotlinx.android.synthetic.main.content_schedule_time_fragment.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.fragment_schedule.appBarView
import kotlinx.android.synthetic.main.fragment_schedule.layoutWorkScheduleInfo
import kotlinx.android.synthetic.main.fragment_schedule.mainConstraintLayout
import kotlinx.android.synthetic.main.fragment_schedule.textViewBuildingName
import kotlinx.android.synthetic.main.fragment_schedule.textViewDept
import kotlinx.android.synthetic.main.fragment_schedule.textViewEmptyData
import kotlinx.android.synthetic.main.fragment_schedule.textViewShift
import java.util.*
import kotlin.collections.ArrayList


class ScheduleFragment : BaseFragment(), ScheduleContract.View, ScheduleContract.View.OnAdapterItemClickListener, CalendarUtils.CalendarSelectionListener {

    private var currentPageIndex: Int = 1
    private var nextPageIndex: Int = 1
    private var totalPagesCount: Int = 1

    private var selectedTime: Long = 0
    private var currentDatePosition: Int = 0
    private lateinit var availableDates: List<Date>
    private var pastFutureDates: ArrayList<PastFutureDates> = ArrayList()
    private var workItemsList: ArrayList<ScheduleDetailData> = ArrayList()
    private var selectedDate: Date = Date()
    private var dateString: String? = null
    private var isSavedState: Boolean = false
    private var datePosition: Int = 0
    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null


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
        const val ARG_WORK_ITEM_ORIGIN = "ARG_WORK_ITEM_ORIGIN"

        const val ARG_SCHEDULED_TIME_NOTES = "ARG_SCHEDULED_TIME_NOTES"
        const val ARG_SCHEDULED_TIME_LIST = "ARG_SCHEDULED_TIME_LIST"
        const val ARG_SCHEDULED_LUMPERS_COUNT = "ARG_SCHEDULED_LUMPERS_COUNT"

        const val SCHEDULED_WORK_ITEM_LIST = "SCHEDULED_WORK_ITEM_LIST"
        const val SCHEDULED_DATE_SELECTED = "SCHEDULED_DATE_SELECTED"
        const val SCHEDULED_DATE_HEADER = "SCHEDULED_DATE_HEADER"
        const val SCHEDULED_TOTAL_PAGE_COUNT = "SCHEDULED_TOTAL_PAGE_COUNT"
        const val SCHEDULED_NEXT_PAGE = "SCHEDULED_NEXT_PAGE"
        const val SCHEDULED_CURRENT_PAGE = "SCHEDULED_CURRENT_PAGE"
        const val SCHEDULED_SELECTED_DATE_POSITION = "SCHEDULED_SELECTED_DATE_POSITION"
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        schedulePresenter = SchedulePresenter(this, resources, sharedPref)

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
            layoutManager = LinearLayoutManager(fragmentActivity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            scheduleAdapter = ScheduleAdapter(resources, context, this@ScheduleFragment)
            adapter = scheduleAdapter
            addOnScrollListener(onScrollListener)
        }

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarSchedule, availableDates, this)
        savedInstanceState?.also {
            isSavedState = true
            if (savedInstanceState.containsKey(SCHEDULED_SELECTED_DATE_POSITION)) {
                datePosition = savedInstanceState.getInt(SCHEDULED_SELECTED_DATE_POSITION)
                singleRowCalendarSchedule.select(datePosition)
            }
            if (savedInstanceState.containsKey(SCHEDULED_CURRENT_PAGE)) {
                currentPageIndex = savedInstanceState.getInt(SCHEDULED_CURRENT_PAGE)
            }
            if (savedInstanceState.containsKey(SCHEDULED_NEXT_PAGE)) {
                nextPageIndex = savedInstanceState.getInt(SCHEDULED_NEXT_PAGE)
            }
            if (savedInstanceState.containsKey(SCHEDULED_TOTAL_PAGE_COUNT)) {
                totalPagesCount = savedInstanceState.getInt(SCHEDULED_TOTAL_PAGE_COUNT)
            }
            if (savedInstanceState.containsKey(SCHEDULED_DATE_SELECTED)) {
                selectedDate = savedInstanceState.getSerializable(SCHEDULED_DATE_SELECTED) as Date
            }
            if (savedInstanceState.containsKey(SCHEDULED_WORK_ITEM_LIST)) {
                workItemsList =
                    savedInstanceState.getParcelableArrayList(SCHEDULED_WORK_ITEM_LIST)!!
                showScheduleData(selectedDate, workItemsList, totalPagesCount, nextPageIndex, currentPageIndex)
            }
            if (savedInstanceState.containsKey(SCHEDULED_DATE_HEADER)) {
                dateString = savedInstanceState.getString(SCHEDULED_DATE_HEADER)!!
                showDateString(dateString!!)
            }
        } ?: run {
            isSavedState = false
            singleRowCalendarSchedule.select(if (currentDatePosition != 0) currentDatePosition else availableDates.size - 1)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        schedulePresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (workItemsList != null)
            outState.putParcelableArrayList(SCHEDULED_WORK_ITEM_LIST, workItemsList)
        if (totalPagesCount != null)
            outState.putInt(SCHEDULED_TOTAL_PAGE_COUNT, totalPagesCount)
        if (nextPageIndex != null)
            outState.putInt(SCHEDULED_NEXT_PAGE, nextPageIndex)
        if (currentPageIndex != null)
            outState.putInt(SCHEDULED_CURRENT_PAGE, currentPageIndex)
        if (selectedDate != null)
            outState.putSerializable(SCHEDULED_DATE_SELECTED, selectedDate)
        if (dateString != null)
            outState.putString(SCHEDULED_DATE_HEADER, dateString)
        if (datePosition != null)
            outState.putInt(SCHEDULED_SELECTED_DATE_POSITION, datePosition)
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
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

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
        this.dateString = dateString
        textViewDate.text = UIUtils.getSpannedText(dateString)
        textViewDate.visibility = View.GONE

        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        if (leadProfile?.buildingDetailData?.get(0) != null) {
            textViewBuildingName.text = leadProfile?.buildingDetailData?.get(0)?.buildingName!!.capitalize()
            textViewDept.text = UIUtils.getSpannableText(getString(R.string.bar_header_dept), UIUtils.getDisplayEmployeeDepartment(leadProfile))
            textViewShift.text = UIUtils.getSpannableText(getString(R.string.bar_header_shift), leadProfile.shift?.capitalize().toString())
            textViewLeadNumber.text = UIUtils.getSpannableText(getString(R.string.bar_header_leads), leadProfile.buildingDetailData?.get(0)?.leadIds!!.size.toString())
        } else {
            layoutWorkScheduleInfo.visibility = View.GONE
            appBarView.visibility = View.GONE
        }

        if(!leadProfile?.shift.isNullOrEmpty()){
            textViewHeaderShift.text=UIUtils.getSpannedText(ScheduleUtils.getShiftDetailString(leadProfile))
            textViewHeaderShift.visibility=View.GONE
        } else textViewHeaderShift.visibility=View.GONE

        if (!leadProfile?.department.isNullOrEmpty()){
            textViewHeaderDept.text=UIUtils.getSpannedText("${ ResourceManager.getInstance().getString(R.string.dept_bold)} ${ UIUtils.getDisplayEmployeeDepartment(leadProfile)}")
            textViewHeaderDept.visibility=View.GONE
        }else textViewHeaderDept.visibility=View.GONE

    }

    override fun showScheduleData(selectedDate: Date, workItemsList: ArrayList<ScheduleDetailData>, totalPagesCount: Int, nextPageIndex: Int, currentPageIndex: Int) {
        this.selectedDate = selectedDate
        this.workItemsList = workItemsList
        this.currentPageIndex = currentPageIndex

        selectedTime = selectedDate.time
        if(!workItemsList[0].liveLoads.isNullOrEmpty()|| !workItemsList[0].drops.isNullOrEmpty() || !workItemsList[0].outbounds.isNullOrEmpty())
        scheduleAdapter.updateList(workItemsList, currentPageIndex, selectedDate)

        if (!workItemsList.isNullOrEmpty() && (!workItemsList[0].liveLoads.isNullOrEmpty()|| !workItemsList[0].drops.isNullOrEmpty() || !workItemsList[0].outbounds.isNullOrEmpty())) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewSchedule.visibility = View.VISIBLE
        } else {
            showEmptyData()
        }

        this.totalPagesCount = totalPagesCount
        this.nextPageIndex = nextPageIndex
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewSchedule.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE

        if (message.equals(AppConstant.ERROR_MESSAGE, ignoreCase = true)) {
            CustomProgressBar.getInstance().showValidationErrorDialog(message, fragmentActivity!!)
        } else SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showPastFutureDate(pastFutureDate: ArrayList<PastFutureDates>) {
        isSavedState = true
        this.pastFutureDates= pastFutureDate
        CalendarUtils.pastFutureDatesNew=pastFutureDates
        singleRowCalendarSchedule.adapter?.notifyDataSetChanged()
    }

    override fun showEmptyData() {
        textViewEmptyData.visibility = View.VISIBLE
        recyclerViewSchedule.visibility = View.GONE
        workItemsList.clear()
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */
    override fun onScheduleItemClick(scheduleDetail: ScheduleDetailData) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val bundle = Bundle()
        bundle.putBoolean(ARG_ALLOW_UPDATE, DateUtils.isCurrentDate(selectedTime))
        bundle.putBoolean(ARG_IS_FUTURE_DATE, DateUtils.isFutureDate(selectedTime))
        bundle.putString(ARG_SCHEDULE_IDENTITY, scheduleDetail.scheduleDepartment)
        bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(WorkScheduleActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val bundle = Bundle()
        bundle.putParcelableArrayList(DisplayLumpersListActivity.ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(date: Date, selected: Boolean, position: Int) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        if (!isSavedState) {
            resetPaginationValues()
            schedulePresenter.getScheduledWorkItemsByDate(date, currentPageIndex)
        }
        isSavedState = false
        datePosition = position
    }
}
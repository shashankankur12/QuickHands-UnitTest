package com.quickhandslogistics.views.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.schedule.WorkSchedulePagerAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.schedule.WorkScheduleContract
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.schedule.WorkSchedulePresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.work_schedule_container.*
import java.util.*
import kotlin.collections.ArrayList


class WorkScheduleActivity : BaseActivity(), WorkScheduleContract.View, WorkScheduleContract.View.OnFragmentInteractionListener,
View.OnClickListener   {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private lateinit var workSheetPresenter: WorkSchedulePresenter
    private var adapter: WorkSchedulePagerAdapter? = null
    private var data: ScheduleDetail = ScheduleDetail()
    private lateinit var date: String
    private lateinit var shift: String
    private lateinit var dept: String
    private lateinit var companyName: String
    private var selectedTime: Long = 0
    private var scheduleIdentity = ""
    private lateinit var customerGroupNote:Triple<Pair<ArrayList<String>,ArrayList<String>>, ArrayList<String>, ArrayList<String>>

    companion object {
        const val WORKSHEET_DETAIL = "WORKSHEET_DETAIL"
        const val WORKSHEET_DATE_SELECTED_HEADER = "WORKSHEET_DATE_SELECTED_HEADER"
        const val WORKSHEET_COMPANY_NAME = "WORKSHEET_COMPANY_NAME"
        const val WORKSHEET_SHIFT = "WORKSHEET_SHIFT"
        const val WORKSHEET_DEPT = "WORKSHEET_DEPT"
        const val WORKSHEET_SELECTED_DATE = "WORKSHEET_SELECTED_DATE"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_schedule)
        setupToolbar(getString(R.string.schedule_detail))

        intent.extras?.let { bundle ->
            scheduleIdentity = bundle.getString(ScheduleFragment.ARG_SCHEDULE_IDENTITY, "")
            selectedTime = bundle.getLong(ScheduleFragment.ARG_SELECTED_DATE_MILLISECONDS, 0)
        }

        workSheetPresenter = WorkSchedulePresenter(this, resources, sharedPref)


        textViewGroupNote.setOnClickListener(this)
        savedInstanceState?.also {
            if (savedInstanceState.containsKey(WORKSHEET_DATE_SELECTED_HEADER)) {
                date = savedInstanceState.getString(WORKSHEET_DATE_SELECTED_HEADER)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_SHIFT)) {
                shift = savedInstanceState.getString(WORKSHEET_SHIFT)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_SELECTED_DATE)) {
                selectedTime = savedInstanceState.getString(WORKSHEET_SELECTED_DATE) !!.toLong()
            }
            if (savedInstanceState.containsKey(WORKSHEET_DEPT)) {
                dept = savedInstanceState.getString(WORKSHEET_DEPT)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_COMPANY_NAME)) {
                companyName = savedInstanceState.getString(WORKSHEET_COMPANY_NAME)!!
                showHeaderInfo(companyName, date, shift, dept)
            }
            if (savedInstanceState.containsKey(WORKSHEET_DETAIL)) {
                data = savedInstanceState.getParcelable<ScheduleDetail>(
                    WORKSHEET_DETAIL
                ) as ScheduleDetail
                showWorkSheets(data)

                val allWorkItemLists = createDifferentListData(data)
                initializeViewPager(allWorkItemLists)
            }
        } ?: run {
            initializeViewPager()

            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }
            workSheetPresenter.fetchWorkSheetList(scheduleIdentity, Date(selectedTime))
        }
        refreshData()
    }

    private fun refreshData() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        swipe_pull_refresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            initializeViewPager()
            workSheetPresenter.fetchWorkSheetList(scheduleIdentity, Date(selectedTime))
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        workSheetPresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (data != null)
            outState.putParcelable(WORKSHEET_DETAIL, data)
        if (!date.isNullOrEmpty())
            outState.putString(WORKSHEET_DATE_SELECTED_HEADER, date)
        if (!shift.isNullOrEmpty())
            outState.putString(WORKSHEET_SHIFT, shift)
        if (selectedTime!=null)
            outState.putString(WORKSHEET_SELECTED_DATE, selectedTime.toString())
        if (!dept.isNullOrEmpty())
            outState.putString(WORKSHEET_DEPT, dept)
        outState.putSerializable(WORKSHEET_COMPANY_NAME, companyName)
        super.onSaveInstanceState(outState)
    }


    private fun initializeViewPager(
        allWorkItemLists: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? = null,
        customerSheetData: CustomerSheetData? = null, selectedTime: Long? = null
    ) {
        adapter = if (allWorkItemLists != null) {
            WorkSchedulePagerAdapter(supportFragmentManager, resources, this.selectedTime, allWorkItemLists)
        } else {
            WorkSchedulePagerAdapter(supportFragmentManager, resources,this.selectedTime)
        }
        viewPagerWorkSheet.offscreenPageLimit = adapter?.count!!
        viewPagerWorkSheet.adapter = adapter
        tabLayoutWorkSheet.setupWithViewPager(viewPagerWorkSheet)
        checkDateForSchedule()

    }

    private fun checkDateForSchedule() {
        if(DateUtils.isFutureDate(selectedTime!!)){
            viewPagerWorkSheet.pagingEnabled =false
            (tabLayoutWorkSheet.getChildAt(0) as ViewGroup).getChildAt(2).isEnabled = false
            (tabLayoutWorkSheet.getChildAt(0) as ViewGroup).getChildAt(1).isEnabled = false
        }else{
            viewPagerWorkSheet.pagingEnabled =true        }
    }

    private fun createDifferentListData(data: ScheduleDetail): Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>> {

        textViewTotalCount.text = String.format(getString(R.string.total_containers_s), data.totalNumberOfWorkItems)


        textViewLiveLoadsCount.text = String.format(getString(R.string.live_loads_s), data?.scheduleTypes?.liveLoads?.size)
        textViewDropsCount.text = String.format(getString(R.string.drops_s), data?.scheduleTypes?.drops?.size)
        textViewOutBoundsCount.text = String.format(getString(R.string.out_bounds_s), data?.scheduleTypes?.outbounds?.size)

        var allWorkItem:ArrayList<WorkItemDetail> = ArrayList()
        data?.scheduleTypes?.outbounds?.let { allWorkItem.addAll(it) }
        data?.scheduleTypes?.liveLoads?.let { allWorkItem.addAll(it) }
        data?.scheduleTypes?.drops?.let { allWorkItem.addAll(it) }

        var onGoingWorkItems:ArrayList<WorkItemDetail> = ArrayList()
        var cancelled:ArrayList<WorkItemDetail> = ArrayList()
        var completed:ArrayList<WorkItemDetail> = ArrayList()

        allWorkItem.forEach {
            when {
                it.status.equals(AppConstant.WORK_ITEM_STATUS_COMPLETED) -> {
                    completed.add(it)
                }
                it.status.equals(AppConstant.WORK_ITEM_STATUS_CANCELLED) -> {
                    cancelled.add(it)
                }
                it.status.equals(AppConstant.WORK_ITEM_STATUS_IN_PROGRESS) || it.status.equals(AppConstant.WORK_ITEM_STATUS_SCHEDULED) ||it.status.equals(AppConstant.WORK_ITEM_STATUS_ON_HOLD) -> {
                    onGoingWorkItems.add(it)
                }
            }
        }


        return Triple(getSortList(onGoingWorkItems), getSortList(cancelled), getSortList(completed))

    }

    private fun resetUI() {
        // Reset Whole Screen Data
        textViewCompanyName.text = ""
        textViewWorkItemsDate.text = ""
        textViewWorkItemShift.text = ""
        textViewWorkItemDept.text = ""
        textViewTotalCount.text = ""
        textViewLiveLoadsCount.text = ""
        textViewDropsCount.text = ""
        textViewOutBoundsCount.text = ""
        adapter?.updateWorkItemsList(ArrayList(), ArrayList(), ArrayList(), selectedTime)
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(this, mainConstraintLayout, message)
        swipe_pull_refresh?.isRefreshing = false
        resetUI()
        onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(false)
    }

    override fun showWorkSheets(data: ScheduleDetail?) {
        this.data = data!!
        swipe_pull_refresh?.isRefreshing = false

        customerGroupNote= ScheduleUtils.getGroupNoteListWorkSchedule(data)
        textViewGroupNote.isEnabled = (customerGroupNote!=null&& (customerGroupNote.first.first.size>0 ||customerGroupNote.second.size>0|| customerGroupNote.third.size>0||customerGroupNote.first.second.size>0 ))

        textViewTotalCount.text = String.format(getString(R.string.total_containers_s), data?.totalNumberOfWorkItems)

        textViewLiveLoadsCount.text = String.format(getString(R.string.live_loads_s), data?.scheduleTypes?.liveLoads?.size)
        textViewDropsCount.text = String.format(getString(R.string.drops_s), data?.scheduleTypes?.drops?.size)
        textViewOutBoundsCount.text = String.format(getString(R.string.out_bounds_s), data?.scheduleTypes?.outbounds?.size)

        var allWorkItem:ArrayList<WorkItemDetail> = ArrayList()
        data?.scheduleTypes?.outbounds?.let { allWorkItem.addAll(it) }
        data?.scheduleTypes?.liveLoads?.let { allWorkItem.addAll(it) }
        data?.scheduleTypes?.drops?.let { allWorkItem.addAll(it) }

        var onGoingWorkItems:ArrayList<WorkItemDetail> = ArrayList()
        var cancelled:ArrayList<WorkItemDetail> = ArrayList()
        var completed:ArrayList<WorkItemDetail> = ArrayList()

        allWorkItem.forEach {
            when {
                it.status.equals(AppConstant.WORK_ITEM_STATUS_COMPLETED) -> {
                    completed.add(it)
                }
                it.status.equals(AppConstant.WORK_ITEM_STATUS_CANCELLED) -> {
                    cancelled.add(it)
                }
                it.status.equals(AppConstant.WORK_ITEM_STATUS_IN_PROGRESS) || it.status.equals(AppConstant.WORK_ITEM_STATUS_SCHEDULED) ||it.status.equals(AppConstant.WORK_ITEM_STATUS_ON_HOLD) -> {
                    onGoingWorkItems.add(it)
                }
            }
        }




        adapter?.updateWorkItemsList(getSortList(onGoingWorkItems), getSortList(cancelled!!), getSortList(completed!!), selectedTime)

    }

    private fun getSortList(workItemsList: ArrayList<WorkItemDetail>): ArrayList<WorkItemDetail> {
        var inboundList: ArrayList<WorkItemDetail> = ArrayList()
        var outBoundList: ArrayList<WorkItemDetail> = ArrayList()
        var liveList: ArrayList<WorkItemDetail> = ArrayList()
        var sortedList: ArrayList<WorkItemDetail> = ArrayList()

        workItemsList.forEach {
            when {
                it.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_LIVE) -> {
                    liveList.add(it)
                }
                it.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_INBOUND) -> {
                    inboundList.add(it)
                }
                it.workItemType.equals(AppConstant.WORKSHEET_WORK_ITEM_OUTBOUND) -> {
                    outBoundList.add(it)
                }
            }
        }
        sortedList.addAll(outBoundList)
        sortedList.addAll(liveList)
        sortedList.addAll(inboundList)
        return sortedList
    }

    override fun showHeaderInfo(companyName: String, date: String, shift: String, dept: String) {
        this.companyName = companyName
        this.date = date
        this.shift=shift
        this.dept=dept

        textViewCompanyName.text = companyName.capitalize()
        textViewWorkItemsDate.text = UIUtils.getSpannedText(date)
        textViewWorkItemShift.text = UIUtils.getSpannedText(shift)
        textViewWorkItemDept.text = UIUtils.getSpannedText(dept)
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Child Fragment Interaction Listeners */
    override fun fetchWorkSheetList() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        workSheetPresenter.fetchWorkSheetList(scheduleIdentity, Date(selectedTime))
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            textViewGroupNote.id->{
                if (!ConnectionDetector.isNetworkConnected(activity)) {
                    ConnectionDetector.createSnackBar(activity)
                    return
                }

                if (customerGroupNote!=null&& (customerGroupNote.first.first.size>0 ||customerGroupNote.second.size>0|| customerGroupNote.third.size>0|| customerGroupNote.first.second.size>0))
                    CustomeDialog.showGroupNoteDialog(activity, "Customer Note ", customerGroupNote)

            }
        }
    }
}
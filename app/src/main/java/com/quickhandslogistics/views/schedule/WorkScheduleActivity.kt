package com.quickhandslogistics.views.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.schedule.WorkSchedulePagerAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.schedule.WorkScheduleContract
import com.quickhandslogistics.controls.Quintuple
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.schedule.ScheduleDetailData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.schedule.WorkSchedulePresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.DateUtils.Companion.getDateString
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.DashBoardActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.content_schedule_time_fragment.*
import kotlinx.android.synthetic.main.work_schedule_container.*
import kotlinx.android.synthetic.main.work_schedule_container.mainConstraintLayout
import kotlinx.android.synthetic.main.work_schedule_container.swipe_pull_refresh
import kotlinx.android.synthetic.main.work_schedule_container.tabLayoutWorkSheet
import kotlinx.android.synthetic.main.work_schedule_container.textViewCompanyName
import kotlinx.android.synthetic.main.work_schedule_container.textViewDropsCount
import kotlinx.android.synthetic.main.work_schedule_container.textViewGroupNote
import kotlinx.android.synthetic.main.work_schedule_container.textViewLiveLoadsCount
import kotlinx.android.synthetic.main.work_schedule_container.textViewOutBoundsCount
import kotlinx.android.synthetic.main.work_schedule_container.textViewTotalCount
import kotlinx.android.synthetic.main.work_schedule_container.textViewUnfinishedCount
import kotlinx.android.synthetic.main.work_schedule_container.textViewWorkItemDept
import kotlinx.android.synthetic.main.work_schedule_container.textViewWorkItemShift
import kotlinx.android.synthetic.main.work_schedule_container.textViewWorkItemsDate
import kotlinx.android.synthetic.main.work_schedule_container.viewPagerWorkSheet
import java.util.*
import kotlin.collections.ArrayList


class WorkScheduleActivity : BaseActivity(), WorkScheduleContract.View, WorkScheduleContract.View.OnFragmentInteractionListener,
View.OnClickListener   {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private lateinit var workSheetPresenter: WorkSchedulePresenter
    private var adapter: WorkSchedulePagerAdapter? = null
    private var data: ScheduleDetailData = ScheduleDetailData()
    private lateinit var date: String
    private lateinit var shift: String
    private lateinit var dept: String
    private lateinit var companyName: String
    private var selectedTime: Long = 0
    private var scheduleIdentity = ""
    private lateinit var customerGroupNote:Triple<Pair<ArrayList<String>, ArrayList<String>>, ArrayList<String>, ArrayList<String>>

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


        if(DateUtils.isFutureDate(selectedTime)){
            buttonScheduleLumper.visibility=View.VISIBLE
        }else buttonScheduleLumper.visibility=View.GONE

        textViewGroupNote.setOnClickListener(this)
        buttonScheduleLumper.setOnClickListener(this)
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
                data = savedInstanceState.getParcelable<ScheduleDetailData>(WORKSHEET_DETAIL) as ScheduleDetailData
                showWorkSheets(data)

                val allWorkItemLists = ScheduleUtils.createDifferentListData(data)
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
        allWorkItemLists: Quintuple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? = null,
        customerSheetData: CustomerSheetData? = null, selectedTime: Long? = null
    ) {
        adapter = if (allWorkItemLists != null) {
            WorkSchedulePagerAdapter(
                supportFragmentManager,
                resources,
                this.selectedTime,
                allWorkItemLists
            )
        } else {
            WorkSchedulePagerAdapter(supportFragmentManager, resources, this.selectedTime)
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
            (tabLayoutWorkSheet.getChildAt(0) as ViewGroup).getChildAt(3).isEnabled = false
            (tabLayoutWorkSheet.getChildAt(0) as ViewGroup).getChildAt(4).isEnabled = false
        }else if (!DateUtils.isCurrentDate(selectedTime) && !DateUtils.isFutureDate(selectedTime)) {
            viewPagerWorkSheet.pagingEnabled =false
            (tabLayoutWorkSheet.getChildAt(0) as ViewGroup).getChildAt(0).isEnabled = false
            (tabLayoutWorkSheet.getChildAt(0) as ViewGroup).getChildAt(2).isEnabled = false
            (tabLayoutWorkSheet.getChildAt(0) as ViewGroup).getChildAt(3).isEnabled = false
            (tabLayoutWorkSheet.getChildAt(0) as ViewGroup).getChildAt(4).isEnabled = false
            val tab: TabLayout.Tab? = tabLayoutWorkSheet.getTabAt(1)
            tab?.select()
        }else viewPagerWorkSheet.pagingEnabled =true
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
        textViewUnfinishedCount.text = ""
        adapter?.updateWorkItemsList(
            ArrayList(),
            ArrayList(),
            ArrayList(),
            ArrayList(),
            ArrayList(),
            selectedTime
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                    ConnectionDetector.createSnackBar(activity)
                    return
                }

                workSheetPresenter.fetchWorkSheetList(scheduleIdentity, Date(selectedTime))
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(this, mainConstraintLayout, message)
        swipe_pull_refresh?.isRefreshing = false
        resetUI()
        onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(false)
    }

    override fun showWorkSheets(data: ScheduleDetailData) {
        this.data = data
        swipe_pull_refresh?.isRefreshing = false
        customerGroupNote= ScheduleUtils.getGroupNoteListWorkSchedule(data)
        textViewGroupNote.isEnabled = (customerGroupNote!=null&& (customerGroupNote.first.first.size>0 ||customerGroupNote.second.size>0|| customerGroupNote.third.size>0||customerGroupNote.first.second.size>0 ))

        val allScheduleItem =ScheduleUtils.createDifferentListData(data)
        val allWorkItem:ArrayList<WorkItemDetail> = ArrayList()
        allWorkItem.addAll(allScheduleItem.first)
        allWorkItem.addAll(allScheduleItem.second)
        allWorkItem.addAll(allScheduleItem.third)
        allWorkItem.addAll(allScheduleItem.fourth)
        allWorkItem.addAll(allScheduleItem.fifth)

        val workItemTypeCounts = ScheduleUtils.getAllWorkItemTypeCounts(allWorkItem)
        if (workItemTypeCounts.first > 0) {
            textViewLiveLoadsCount.visibility = View.VISIBLE
            textViewLiveLoadsCount.text =
                String.format(getString(R.string.live_load_s), workItemTypeCounts.first)
        } else textViewLiveLoadsCount.visibility = View.GONE

        if (workItemTypeCounts.second > 0) {
            textViewDropsCount.visibility = View.VISIBLE
            textViewDropsCount.text =
                String.format(getString(R.string.drops_value), workItemTypeCounts.second)
        } else textViewDropsCount.visibility = View.GONE

        if (workItemTypeCounts.third > 0) {
            textViewOutBoundsCount.visibility = View.VISIBLE
            textViewOutBoundsCount.text =
                String.format(getString(R.string.out_bound_s), workItemTypeCounts.third)
        } else textViewOutBoundsCount.visibility = View.GONE

        if (workItemTypeCounts.fourth > 0) {
            textViewUnfinishedCount.visibility = View.VISIBLE
            textViewUnfinishedCount.text =
                String.format(getString(R.string.resume_header), workItemTypeCounts.fourth)
        } else textViewUnfinishedCount.visibility = View.GONE


        textViewTotalCount.text = UIUtils.getSpannableText(
            getString(R.string.total_container_bold),
            (workItemTypeCounts.fifth).toString()
        )


        adapter?.updateWorkItemsList(
            allScheduleItem.first, allScheduleItem.second,
                allScheduleItem.third
            , allScheduleItem.fourth, allScheduleItem.fifth, selectedTime
        )
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
        startIntent(
            LoginActivity::class.java, isFinish = true, flags = arrayOf(
                Intent.FLAG_ACTIVITY_CLEAR_TASK,
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )
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
            textViewGroupNote.id -> {
                if (!ConnectionDetector.isNetworkConnected(activity)) {
                    ConnectionDetector.createSnackBar(activity)
                    return
                }

                if (customerGroupNote != null && (customerGroupNote.first.first.size > 0 || customerGroupNote.second.size > 0 || customerGroupNote.third.size > 0 || customerGroupNote.first.second.size > 0))
                    CustomerDialog.showGroupNoteDialog(
                        activity,
                        "Customer Notes :",
                        customerGroupNote
                    )

            }
            buttonScheduleLumper.id -> {
                if (!ConnectionDetector.isNetworkConnected(activity)) {
                    ConnectionDetector.createSnackBar(activity)
                    return
                }

                val bundle = Bundle()
                bundle.putString(DashBoardActivity.ARG_SHOW_TAB_NAME, getString(R.string.scheduled_lumpers))
                bundle.putString(DashBoardActivity.ARG_SCHEDULE_TIME_SELECTED_DATE, getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER,Date(selectedTime)))
                bundle.putBoolean(DashBoardActivity.PRIVIOUS_TAB, true)
                startIntent(DashBoardActivity::class.java, bundle = bundle)
                finish()
            }
        }
    }
}
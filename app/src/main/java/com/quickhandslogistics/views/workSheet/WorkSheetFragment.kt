package com.quickhandslogistics.views.workSheet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.WorkSheetPagerAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.presenters.workSheet.WorkSheetPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.ScheduleUtils.getGroupNoteList
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.fragment_work_sheet.*

class WorkSheetFragment : BaseFragment(), WorkSheetContract.View, WorkSheetContract.View.OnFragmentInteractionListener,
    View.OnClickListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private lateinit var workSheetPresenter: WorkSheetPresenter
    private var adapter: WorkSheetPagerAdapter? = null
    private var data: WorkSheetListAPIResponse.Data = WorkSheetListAPIResponse.Data()
    private lateinit var date: String
    private lateinit var shift: String
    private lateinit var dept: String
    private lateinit var companyName: String
    private lateinit var customerGroupNote:Triple<Pair<ArrayList<String>,ArrayList<String>>, ArrayList<String>, ArrayList<String>>

    companion object {
        const val WORKSHEET_DETAIL = "WORKSHEET_DETAIL"
        const val WORKSHEET_DATE_SELECTED_HEADER = "WORKSHEET_DATE_SELECTED_HEADER"
        const val WORKSHEET_COMPANY_NAME = "WORKSHEET_COMPANY_NAME"
        const val WORKSHEET_SHIFT = "WORKSHEET_SHIFT"
        const val WORKSHEET_DEPT = "WORKSHEET_DEPT"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workSheetPresenter = WorkSheetPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewGroupNote.setOnClickListener(this)
        savedInstanceState?.also {
            if (savedInstanceState.containsKey(WORKSHEET_DATE_SELECTED_HEADER)) {
                date = savedInstanceState.getString(WORKSHEET_DATE_SELECTED_HEADER)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_SHIFT)) {
                shift = savedInstanceState.getString(WORKSHEET_SHIFT)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_DEPT)) {
                dept = savedInstanceState.getString(WORKSHEET_DEPT)!!
            }
            if (savedInstanceState.containsKey(WORKSHEET_COMPANY_NAME)) {
                companyName = savedInstanceState.getString(WORKSHEET_COMPANY_NAME)!!
                showHeaderInfo(companyName, date, shift, dept)
            }
            if (savedInstanceState.containsKey(WORKSHEET_DETAIL)) {
                data = savedInstanceState.getParcelable<WorkSheetListAPIResponse.Data>(WORKSHEET_DETAIL) as WorkSheetListAPIResponse.Data
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

            workSheetPresenter.fetchWorkSheetList()
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

            workSheetPresenter.fetchWorkSheetList()
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
        if (!dept.isNullOrEmpty())
            outState.putString(WORKSHEET_DEPT, dept)
        outState.putSerializable(WORKSHEET_COMPANY_NAME, companyName)
        super.onSaveInstanceState(outState)
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentInteractionListener = null
    }

    private fun initializeViewPager(
        allWorkItemLists: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? = null,
        customerSheetData: CustomerSheetData? = null, selectedTime: Long? = null
    ) {
        adapter = if (allWorkItemLists != null) {
            WorkSheetPagerAdapter(childFragmentManager, resources, allWorkItemLists)
        } else {
            WorkSheetPagerAdapter(childFragmentManager, resources)
        }
        viewPagerWorkSheet.offscreenPageLimit = adapter?.count!!
        viewPagerWorkSheet.adapter = adapter
        tabLayoutWorkSheet.setupWithViewPager(viewPagerWorkSheet)
    }

    private fun createDifferentListData(data: WorkSheetListAPIResponse.Data): Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>> {
        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(data.inProgress!!)
        onGoingWorkItems.addAll(data.onHold!!)
        onGoingWorkItems.addAll(data.scheduled!!)

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(onGoingWorkItems)
        allWorkItems.addAll(data.cancelled!!)
        allWorkItems.addAll(data.completed!!)
        textViewTotalCount.text = String.format(getString(R.string.total_containers_s), allWorkItems.size)

        val workItemTypeCounts = ScheduleUtils.getWorkItemTypeCounts(allWorkItems)

        textViewLiveLoadsCount.text = String.format(getString(R.string.live_loads_s), workItemTypeCounts.first)
        textViewDropsCount.text = String.format(getString(R.string.drops_s), workItemTypeCounts.second)
        textViewOutBoundsCount.text = String.format(getString(R.string.out_bounds_s), workItemTypeCounts.third)


        return Triple(getSortList(onGoingWorkItems), getSortList(data.cancelled!!), getSortList(data.completed!!))
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
        adapter?.updateWorkItemsList(ArrayList(), ArrayList(), ArrayList())
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
        swipe_pull_refresh?.isRefreshing = false
        resetUI()
        onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(false)
    }

    override fun showWorkSheets(data: WorkSheetListAPIResponse.Data) {
        this.data = data
        swipe_pull_refresh?.isRefreshing = false

        customerGroupNote=getGroupNoteList(data)
        textViewGroupNote.isEnabled = (customerGroupNote!=null&& (customerGroupNote.first.first.size>0 ||customerGroupNote.second.size>0|| customerGroupNote.third.size>0||customerGroupNote.first.second.size>0 ))


        // Change the visibility of Cancel All Schedule Option
        if (data.inProgress.isNullOrEmpty() && data.onHold.isNullOrEmpty() && data.cancelled.isNullOrEmpty() && data.completed.isNullOrEmpty() && !data.scheduled.isNullOrEmpty()) {
            onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(true)
        } else {
            onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(false)
        }

        val onGoingWorkItems = ArrayList<WorkItemDetail>()
        onGoingWorkItems.addAll(data.inProgress!!)
        onGoingWorkItems.addAll(data.onHold!!)
        onGoingWorkItems.addAll(data.scheduled!!)

        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(onGoingWorkItems)
        allWorkItems.addAll(data.cancelled!!)
        allWorkItems.addAll(data.completed!!)
        textViewTotalCount.text = String.format(getString(R.string.total_containers_s), allWorkItems.size)

        val workItemTypeCounts = ScheduleUtils.getWorkItemTypeCounts(allWorkItems)

        textViewLiveLoadsCount.text = String.format(getString(R.string.live_loads_s), workItemTypeCounts.first)
        textViewDropsCount.text = String.format(getString(R.string.drops_s), workItemTypeCounts.second)
        textViewOutBoundsCount.text = String.format(getString(R.string.out_bounds_s), workItemTypeCounts.third)

        adapter?.updateWorkItemsList(getSortList(onGoingWorkItems), getSortList(data.cancelled!!), getSortList(data.completed!!))
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
        workSheetPresenter.fetchWorkSheetList()
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            textViewGroupNote.id->{
                if (!ConnectionDetector.isNetworkConnected(activity)) {
                    ConnectionDetector.createSnackBar(activity)
                    return
                }

                if (customerGroupNote!=null&& (customerGroupNote.first.first.size>0 ||customerGroupNote.second.size>0|| customerGroupNote.third.size>0|| customerGroupNote.first.second.size>0))
                CustomeDialog.showGroupNoteDialog(activity, "Customer Notes", customerGroupNote)

            }
        }
    }
}

package com.quickhandslogistics.views.workSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.WorkSheetPagerAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.presenters.workSheet.WorkSheetPresenter
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.fragment_work_sheet.*
import java.util.*

class WorkSheetFragment : BaseFragment(), WorkSheetContract.View, WorkSheetContract.View.OnFragmentInteractionListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private lateinit var workSheetPresenter: WorkSheetPresenter
    private lateinit var adapter: WorkSheetPagerAdapter
    private lateinit var data: WorkSheetListAPIResponse.Data
    private lateinit var date: String
    private lateinit var companyName: String

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

        adapter = WorkSheetPagerAdapter(childFragmentManager, resources)
        viewPagerWorkSheet.offscreenPageLimit = adapter.count
        viewPagerWorkSheet.adapter = adapter
        tabLayoutWorkSheet.setupWithViewPager(viewPagerWorkSheet)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey("date")) {
                date = savedInstanceState.getString("date")!!
            }
            if (savedInstanceState.containsKey("data")) {
                companyName = savedInstanceState.getString("name") !!
                showHeaderInfo(companyName,date)
            }
            if (savedInstanceState.containsKey("data")) {
                data = savedInstanceState.getSerializable("data") as WorkSheetListAPIResponse.Data
                showWorkSheets(data!!)
            }
        } ?: run {
            workSheetPresenter.fetchWorkSheetList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        workSheetPresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("data", data)
        outState.putString("date", date)
        outState.putSerializable("name", companyName)
        super.onSaveInstanceState(outState)
    }

    override fun onDetach() {
        super.onDetach()
        onFragmentInteractionListener = null
    }

    private fun resetUI() {
        // Reset Whole Screen Data
        textViewCompanyName.text = ""
        textViewWorkItemsDate.text = ""
        textViewTotalCount.text = ""
        textViewLiveLoadsCount.text = ""
        textViewDropsCount.text = ""
        textViewOutBoundsCount.text = ""
        adapter.updateWorkItemsList(ArrayList(), ArrayList(), ArrayList())
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)

        resetUI()
        onFragmentInteractionListener?.invalidateCancelAllSchedulesOption(false)
    }

    override fun showWorkSheets(data: WorkSheetListAPIResponse.Data) {
        this.data=data
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

        adapter.updateWorkItemsList(onGoingWorkItems, data.cancelled!!, data.completed!!)
    }

    override fun showHeaderInfo(companyName: String, date: String) {
        this.companyName=companyName
        this.date=date

        textViewCompanyName.text = companyName.capitalize()
        textViewWorkItemsDate.text = date
    }

    /** Child Fragment Interaction Listeners */
    override fun fetchWorkSheetList() {
        workSheetPresenter.fetchWorkSheetList()
    }
}

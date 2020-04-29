package com.quickhandslogistics.modified.views.workSheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.workSheet.WorkSheetPagerAdapter
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.modified.controls.ScheduleUtils
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.workSheet.WorkSheetPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.fragment_work_sheet.*
import java.util.*

class WorkSheetFragment : BaseFragment(), WorkSheetContract.View {

    private lateinit var workSheetPresenter: WorkSheetPresenter

    private lateinit var adapter: WorkSheetPagerAdapter
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workSheetPresenter = WorkSheetPresenter(this, resources, sharedPref)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WorkSheetPagerAdapter(childFragmentManager, resources)
        viewPagerWorkSheet.offscreenPageLimit = adapter.count
        viewPagerWorkSheet.adapter = adapter
        tabLayoutWorkSheet.setupWithViewPager(viewPagerWorkSheet)

        workSheetPresenter.fetchWorkSheetList()
    }

    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(fragmentActivity!!).showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {

    }

    override fun showWorkSheets(
        onGoingWorkItems: ArrayList<WorkItemDetail>,
        cancelledWorkItems: ArrayList<WorkItemDetail>,
        completedWorkItems: ArrayList<WorkItemDetail>
    ) {
        val allWorkItems = ArrayList<WorkItemDetail>()
        allWorkItems.addAll(onGoingWorkItems)
        allWorkItems.addAll(cancelledWorkItems)
        allWorkItems.addAll(completedWorkItems)
        textViewTotalCount.text =
            String.format(getString(R.string.total_containers_s), allWorkItems.size)

        val workItemTypeCounts = ScheduleUtils.getWorkItemTypeCounts(allWorkItems)

        textViewLiveLoadsCount.text =
            String.format(getString(R.string.live_loads_s), workItemTypeCounts.first)
        textViewDropsCount.text =
            String.format(getString(R.string.drops_s), workItemTypeCounts.second)
        textViewOutBoundsCount.text =
            String.format(getString(R.string.out_bounds_s), workItemTypeCounts.third)

        adapter.updateWorkItemsList(onGoingWorkItems, cancelledWorkItems, completedWorkItems)
    }

    override fun cancellingWorkScheduleFinished() {
    }

    override fun showHeaderInfo(buildingName: String, date: String) {
        textViewBuildingName.text = buildingName
        textViewWorkItemsDate.text = date
    }
}

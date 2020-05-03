package com.quickhandslogistics.modified.views.workSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.workSheet.WorkSheetPagerAdapter
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.modified.controls.ScheduleUtils
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.modified.presenters.workSheet.WorkSheetPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.fragment_work_sheet.*
import java.util.*

class WorkSheetFragment : BaseFragment(), WorkSheetContract.View,
    WorkSheetContract.View.OnFragmentInteractionListener {

    private var listener: DashBoardContract.View.OnFragmentInteractionListener? = null
    private lateinit var workSheetPresenter: WorkSheetPresenter

    private lateinit var adapter: WorkSheetPagerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        workSheetPresenter = WorkSheetPresenter(this, resources, sharedPref)

        adapter = WorkSheetPagerAdapter(childFragmentManager, resources)
        viewPagerWorkSheet.offscreenPageLimit = adapter.count
        viewPagerWorkSheet.adapter = adapter
        tabLayoutWorkSheet.setupWithViewPager(viewPagerWorkSheet)

        workSheetPresenter.fetchWorkSheetList()
    }

    /* override fun hideProgressDialog() {
         progressDialog?.dismiss()
     }

     override fun showProgressDialog(message: String) {
         progressDialog =
             CustomProgressBar.getInstance(fragmentActivity!!).showProgressDialog(message)
     }*/

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)

        // Reset Whole Screen Data
        textViewBuildingName.text = ""
        textViewWorkItemsDate.text = ""
        textViewTotalCount.text = ""
        textViewLiveLoadsCount.text = ""
        textViewDropsCount.text = ""
        textViewOutBoundsCount.text = ""
        adapter.updateWorkItemsList(ArrayList(), ArrayList(), ArrayList())
        listener?.invalidateCancelAllSchedulesOption(false)
    }

    override fun showWorkSheets(data: WorkSheetListAPIResponse.Data) {
        // Change the visibility of Cancel All Schedule Option
        if (data.inProgress.isNullOrEmpty() && data.onHold.isNullOrEmpty()
            && data.cancelled.isNullOrEmpty() && data.completed.isNullOrEmpty()
            && !data.scheduled.isNullOrEmpty()
        ) {
            listener?.invalidateCancelAllSchedulesOption(true)
        } else {
            listener?.invalidateCancelAllSchedulesOption(false)
        }

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

        val workItemTypeCounts = ScheduleUtils.getWorkItemTypeCounts(allWorkItems)

        textViewLiveLoadsCount.text =
            String.format(getString(R.string.live_loads_s), workItemTypeCounts.first)
        textViewDropsCount.text =
            String.format(getString(R.string.drops_s), workItemTypeCounts.second)
        textViewOutBoundsCount.text =
            String.format(getString(R.string.out_bounds_s), workItemTypeCounts.third)

        adapter.updateWorkItemsList(onGoingWorkItems, data.cancelled!!, data.completed!!)
    }

    override fun showHeaderInfo(buildingName: String, date: String) {
        textViewBuildingName.text = buildingName.capitalize()
        textViewWorkItemsDate.text = date
    }

    override fun fetchWorkSheetList() {
        workSheetPresenter.fetchWorkSheetList()
    }

    override fun onDestroy() {
        super.onDestroy()
        workSheetPresenter.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}

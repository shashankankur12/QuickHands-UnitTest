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
import com.quickhandslogistics.adapters.workSheet.WorkSheetItemAdapter
import com.quickhandslogistics.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.common.DisplayLumpersListActivity
import com.quickhandslogistics.views.workSheet.WorkSheetItemDetailActivity
import kotlinx.android.synthetic.main.fragment_work_sheet_item.*

class WorkScheduleItemFragment : BaseFragment(), WorkSheetItemContract.View.OnAdapterItemClickListener {

    private var onFragmentInteractionListener: WorkSheetContract.View.OnFragmentInteractionListener? = null

    private var workItemType: String = ""
    private var selectedTime: Long = 0
    private var onGoingWorkItems = java.util.ArrayList<WorkItemDetail>()
    private var cancelledWorkItems = java.util.ArrayList<WorkItemDetail>()
    private var completedWorkItems = java.util.ArrayList<WorkItemDetail>()

    private lateinit var workSheetItemAdapter: WorkSheetItemAdapter

    companion object {
        private const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"
        private const val ARG_ONGOING_ITEMS = "ARG_ONGOING_ITEMS"
        private const val ARG_CANCELLED_ITEMS = "ARG_CANCELLED_ITEMS"
        private const val ARG_COMPLETED_ITEMS = "ARG_COMPLETED_ITEMS"
        private const val ARG_SELECTED_TIME = "ARG_SELECTED_TIME"

        @JvmStatic
        fun newInstance(
            workItemType: String,
            allWorkItemLists: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>?,
            selectedTime: Long?
        ) = WorkScheduleItemFragment()
            .apply {
                arguments = Bundle().apply {
                    putString(ARG_WORK_ITEM_TYPE, workItemType)
                    if(allWorkItemLists!=null){
                        putParcelableArrayList(ARG_ONGOING_ITEMS, allWorkItemLists.first)
                        putParcelableArrayList(ARG_CANCELLED_ITEMS, allWorkItemLists.second)
                        putParcelableArrayList(ARG_COMPLETED_ITEMS, allWorkItemLists.third)
                    }
                    if(selectedTime!= null){
                        putLong(ARG_SELECTED_TIME, selectedTime)
                    }
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is WorkSheetContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = parentFragment as WorkSheetContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            workItemType = it.getString(ARG_WORK_ITEM_TYPE, "")
            selectedTime = it.getLong(ARG_SELECTED_TIME)
            if (it.containsKey(ARG_ONGOING_ITEMS))
                onGoingWorkItems = it.getParcelableArrayList(ARG_ONGOING_ITEMS)!!
            if (it.containsKey(ARG_CANCELLED_ITEMS))
                cancelledWorkItems = it.getParcelableArrayList(ARG_CANCELLED_ITEMS)!!
            if (it.containsKey(ARG_COMPLETED_ITEMS))
                completedWorkItems = it.getParcelableArrayList(ARG_COMPLETED_ITEMS)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewWorkSheet.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            workSheetItemAdapter = WorkSheetItemAdapter(resources, sharedPref, this@WorkScheduleItemFragment)
            adapter = workSheetItemAdapter
        }

        workSheetItemAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (workSheetItemAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        textViewEmptyData.text = when (workItemType) {
            getString(R.string.ongoing) -> getString(R.string.empty_containers_list_ongoing_info_message)
            getString(R.string.cancelled) -> getString(R.string.empty_containers_list_cancelled_info_message)
            else -> getString(R.string.empty_containers_list_completed_info_message)
        }
        when(workItemType){
            getString(R.string.ongoing) ->  updateWorkItemsList(onGoingWorkItems, selectedTime)
            getString(R.string.cancelled) ->  updateWorkItemsList(cancelledWorkItems, selectedTime)
            else ->updateWorkItemsList(completedWorkItems, selectedTime)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            onFragmentInteractionListener?.fetchWorkSheetList()
        }
    }

    fun updateWorkItemsList(workItemsList: ArrayList<WorkItemDetail>, selectedTime: Long) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        this.selectedTime=selectedTime
        workSheetItemAdapter.updateList(workItemsList)
    }

    /** Adapter Listeners */
    override fun onItemClick(workItemId: String, workItemTypeDisplayName: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        if (!DateUtils.isFutureDate(selectedTime)) {
            val bundle = Bundle()
            bundle.putString(ScheduleFragment.ARG_WORK_ITEM_ID, workItemId)
            bundle.putString(ScheduleFragment.ARG_WORK_ITEM_TYPE_DISPLAY_NAME, workItemTypeDisplayName)
            startIntent(WorkSheetItemDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
        }
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

    override fun onNoteClick(workItemDetail: WorkItemDetail) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        workItemDetail.scheduleNote?.let {
            val title= ScheduleUtils.scheduleTypeNotePopupTitle(workItemDetail, resources)
            CustomProgressBar.getInstance().showInfoDialog(title, it, fragmentActivity!!)
        }

    }
}
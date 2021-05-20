package com.quickhandslogistics.views.workSheet

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
import com.quickhandslogistics.controls.Quintuple
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.ContainerGroupNote
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.ConnectionDetector
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.common.DisplayLumpersListActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ORIGIN
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import kotlinx.android.synthetic.main.content_work_sheet_item.*
import java.util.*
import kotlin.collections.ArrayList

class WorkSheetItemFragment : BaseFragment(), WorkSheetItemContract.View.OnAdapterItemClickListener, View.OnClickListener, View.OnLongClickListener {

    private var onFragmentInteractionListener: WorkSheetContract.View.OnFragmentInteractionListener? = null

    private var workItemType: String = ""
    private var onGoingWorkItems = java.util.ArrayList<WorkItemDetail>()
    private var cancelledWorkItems = java.util.ArrayList<WorkItemDetail>()
    private var completedWorkItems = java.util.ArrayList<WorkItemDetail>()
    private var unfinishedWorkItems = java.util.ArrayList<WorkItemDetail>()
    private var notDoneWorkItems = java.util.ArrayList<WorkItemDetail>()
    private var groupNotes: ContainerGroupNote? =null

    private lateinit var workSheetItemAdapter: WorkSheetItemAdapter

    companion object {
        private const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"
        private const val ARG_ONGOING_ITEMS = "ARG_ONGOING_ITEMS"
        private const val ARG_CANCELLED_ITEMS = "ARG_CANCELLED_ITEMS"
        private const val ARG_COMPLETED_ITEMS = "ARG_COMPLETED_ITEMS"
        private const val ARG_UNFINISHED_ITEMS = "ARG_UNFINISHED_ITEMS"
        private const val ARG_NOT_DONE_ITEMS = "ARG_NOT_DONE_ITEMS"
        private const val ARG_NOTE_DATA = "ARG_NOTE_DATA"

        @JvmStatic
        fun newInstance(
            workItemType: String,
            allWorkItemLists: Quintuple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>?,
            containerGroupNote: ContainerGroupNote?=null
        ) = WorkSheetItemFragment()
            .apply {
                arguments = Bundle().apply {
                    putString(ARG_WORK_ITEM_TYPE, workItemType)
                    if(allWorkItemLists!=null){
                        putParcelableArrayList(ARG_ONGOING_ITEMS, allWorkItemLists.first)
                        putParcelableArrayList(ARG_CANCELLED_ITEMS, allWorkItemLists.second)
                        putParcelableArrayList(ARG_COMPLETED_ITEMS, allWorkItemLists.third)
                        putParcelableArrayList(ARG_UNFINISHED_ITEMS, allWorkItemLists.fourth)
                        putParcelableArrayList(ARG_NOT_DONE_ITEMS, allWorkItemLists.fifth)
                    }
                   if (containerGroupNote!=null){
                      putParcelable(ARG_NOTE_DATA, containerGroupNote)
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
            if (it.containsKey(ARG_ONGOING_ITEMS))
                onGoingWorkItems = it.getParcelableArrayList(ARG_ONGOING_ITEMS)!!
            if (it.containsKey(ARG_CANCELLED_ITEMS))
                cancelledWorkItems = it.getParcelableArrayList(ARG_CANCELLED_ITEMS)!!
            if (it.containsKey(ARG_COMPLETED_ITEMS))
                completedWorkItems = it.getParcelableArrayList(ARG_COMPLETED_ITEMS)!!
            if (it.containsKey(ARG_UNFINISHED_ITEMS))
                unfinishedWorkItems = it.getParcelableArrayList(ARG_UNFINISHED_ITEMS)!!
            if (it.containsKey(ARG_NOT_DONE_ITEMS))
                notDoneWorkItems = it.getParcelableArrayList(ARG_NOT_DONE_ITEMS)!!

            if (it.containsKey(ARG_NOTE_DATA))
                groupNotes = it.getParcelable<ContainerGroupNote>(ARG_NOTE_DATA) as ContainerGroupNote
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
            workSheetItemAdapter = WorkSheetItemAdapter(resources, sharedPref, this@WorkSheetItemFragment)
            adapter = workSheetItemAdapter
        }

        workSheetItemAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility =
                    if (workSheetItemAdapter.itemCount == 0) View.VISIBLE else View.GONE
                setVisibility()
            }
        })

        textViewEmptyData.text = when (workItemType) {
            getString(R.string.ongoing) -> getString(R.string.empty_containers_list_ongoing_info_message)
            getString(R.string.cancel) -> getString(R.string.empty_containers_list_cancelled_info_message)
            getString(R.string.complete) -> getString(R.string.empty_containers_list_completed_info_message)
            getString(R.string.unfinished) -> getString(R.string.empty_containers_list_unfinished_info_message)
            else -> getString(R.string.empty_containers_list_not_done_info_message)
        }
        when (workItemType) {
            getString(R.string.ongoing) -> {
                updateWorkItemsList(onGoingWorkItems)
            }
            getString(R.string.cancel) -> {
                updateWorkItemsList(cancelledWorkItems,groupNotes)

            }
            getString(R.string.complete) -> {
                updateWorkItemsList(completedWorkItems)

            }
            getString(R.string.unfinished) -> {
                updateWorkItemsList(unfinishedWorkItems,groupNotes)
            }
            else -> updateWorkItemsList(notDoneWorkItems,groupNotes)
        }

        textViewAddGroupNote.setOnClickListener(this)
        textViewShowGroupNote.setOnClickListener(this)
        textViewEditGroupNote.setOnClickListener(this)
        textViewDeleteGroupNote.setOnClickListener(this)
        textViewShowGroupNote.setOnLongClickListener(this)
    }

    fun setVisibility() {
        if (workSheetItemAdapter.getItemList().size > 0 && (workItemType == getString(R.string.cancel))) {
            if (groupNotes != null) {
                textViewAddGroupNote.visibility = View.GONE
                textViewShowGroupNote.visibility = View.VISIBLE
                textViewEditGroupNote.visibility = View.VISIBLE
                textViewDeleteGroupNote.visibility = View.VISIBLE
            } else {
                textViewAddGroupNote.visibility = View.VISIBLE
                textViewShowGroupNote.visibility = View.GONE
                textViewEditGroupNote.visibility = View.GONE
                textViewDeleteGroupNote.visibility = View.GONE
            }
        } else if (workSheetItemAdapter.getItemList().size > 0 && (workItemType == getString(R.string.unfinished))) {
            if (groupNotes != null) {
                textViewAddGroupNote.visibility = View.GONE
                textViewShowGroupNote.visibility = View.VISIBLE
                textViewEditGroupNote.visibility = View.VISIBLE
                textViewDeleteGroupNote.visibility = View.VISIBLE
            } else {
                textViewAddGroupNote.visibility = View.VISIBLE
                textViewShowGroupNote.visibility = View.GONE
                textViewEditGroupNote.visibility = View.GONE
                textViewDeleteGroupNote.visibility = View.GONE
            }
        } else if (workSheetItemAdapter.getItemList().size > 0 && (workItemType == getString(R.string.not_open))) {
            if (groupNotes != null) {
                textViewAddGroupNote.visibility = View.GONE
                textViewShowGroupNote.visibility = View.VISIBLE
                textViewEditGroupNote.visibility = View.VISIBLE
                textViewDeleteGroupNote.visibility = View.VISIBLE
            } else {
                textViewAddGroupNote.visibility = View.VISIBLE
                textViewShowGroupNote.visibility = View.GONE
                textViewEditGroupNote.visibility = View.GONE
                textViewDeleteGroupNote.visibility = View.GONE
            }
        }else{
            textViewAddGroupNote.visibility = View.GONE
            textViewShowGroupNote.visibility = View.GONE
            textViewEditGroupNote.visibility = View.GONE
            textViewDeleteGroupNote.visibility = View.GONE
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            onFragmentInteractionListener?.fetchWorkSheetList()
        }
    }

    fun updateWorkItemsList(
        workItemsList: ArrayList<WorkItemDetail>, notOpenNotes: ContainerGroupNote?= null) {
        this.groupNotes=notOpenNotes
        workSheetItemAdapter.updateList(workItemsList)
    }


    /** Adapter Listeners */
    override fun onItemClick(workItemId: String, workItemTypeDisplayName: String, origin: String?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val bundle = Bundle()
        bundle.putString(ARG_WORK_ITEM_ID, workItemId)
        bundle.putString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, workItemTypeDisplayName)
        bundle.putString(ARG_WORK_ITEM_ORIGIN, origin)
        bundle.putLong(ScheduleFragment.ARG_SELECTED_DATE_MILLISECONDS, Date().time)
        startIntent(WorkSheetItemDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
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

        workItemDetail.schedule?.scheduleNote?.let {
            val title= ScheduleUtils.scheduleNotePopupTitle(workItemDetail.schedule, resources)
            CustomProgressBar.getInstance().showInfoDialog(title, it, fragmentActivity!!)
        }

    }

    override fun onClick(view: View?) {
        when(view!!.id){
            textViewAddGroupNote.id->{
                val containerIds:ArrayList<String> = ArrayList()
                var containerType:String =""
                when (workItemType) {
                    getString(R.string.cancel) -> {
                        containerType=AppConstant.WORK_ITEM_STATUS_CANCELLED
                    }
                    getString(R.string.unfinished) -> {
                        containerType= AppConstant.WORK_ITEM_STATUS_UNFINISHED
                    }
                    getString(R.string.not_open) -> {
                        containerType=AppConstant.WORK_ITEM_STATUS_NOT_OPEN
                    }
                }
                workSheetItemAdapter.getItemList().forEach {
                    it.id?.let { it1 -> containerIds.add(it1) }
                }

                if (!containerIds.isNullOrEmpty())
                onFragmentInteractionListener?.showBottomSheetGroupNote( null, containerIds, containerType)
            }
            textViewShowGroupNote.id->{
                groupNotes?.let {
                    val noteForCustomer= if(!it.noteForCustomer.isNullOrEmpty()) it.noteForCustomer?.capitalize()else "N/A"
                    val noteForQhl= if(!it.noteForQHL.isNullOrEmpty()) it.noteForQHL?.capitalize()else "N/A"
                    onFragmentInteractionListener?.showGroupNote(noteForCustomer!!, noteForQhl!!)
                }

            }
            textViewDeleteGroupNote.id->{
                groupNotes?.let {
                    onFragmentInteractionListener?.removeGroupNote(it.id)
                }
            }
            textViewEditGroupNote.id->{
                val containerIds:ArrayList<String> = ArrayList()
                var containerType:String =""
                when (workItemType) {
                    getString(R.string.cancel) -> {
                        containerType="CANCELLED"
                    }
                    getString(R.string.unfinished) -> {
                        containerType="UNFINISHED"
                    }
                    getString(R.string.not_open) -> {
                        containerType="NOTOPEN"
                    }
                }
                workSheetItemAdapter.getItemList().forEach {
                    it.id?.let { it1 -> containerIds.add(it1) }
                }
                onFragmentInteractionListener?.showBottomSheetGroupNote(groupNotes, containerIds, containerType)
            }
        }
    }

    override fun onLongClick(view: View?): Boolean {
        view?.let {
            return when (view.id) {
                textViewShowGroupNote.id -> {
                    groupNotes?.let {
                    onFragmentInteractionListener?.removeGroupNote(it.id)
                    }
                    return true
                }
                else -> {
                    false
                }
            }
        }
        return false
    }
}
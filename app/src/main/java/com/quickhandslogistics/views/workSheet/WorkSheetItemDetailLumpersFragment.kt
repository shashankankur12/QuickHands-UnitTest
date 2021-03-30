package com.quickhandslogistics.views.workSheet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.WorkSheetItemDetailLumpersAdapter
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailLumpersContract
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.ConnectionDetector
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.lumpers.LumperDetailActivity
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_PRESENT
import com.quickhandslogistics.views.schedule.AddWorkItemLumpersActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE
import kotlinx.android.synthetic.main.content_work_sheet_item_detail_lumpers.*

class WorkSheetItemDetailLumpersFragment : BaseFragment(), View.OnClickListener, WorkSheetItemDetailLumpersContract.View.OnAdapterItemClickListener {

    private var onFragmentInteractionListener: WorkSheetItemDetailContract.View.OnFragmentInteractionListener? = null

    private lateinit var workSheetItemDetailLumpersAdapter: WorkSheetItemDetailLumpersAdapter

    private var workItemDetail: WorkItemContainerDetails? = null
    private  var lumpersTimeSchedule: ArrayList<LumpersTimeSchedule> = ArrayList<LumpersTimeSchedule>()
    private var tempLumperIds: ArrayList<String> = ArrayList()

    companion object {
        private const val LUMPER_WORK_DETALS = "LUMPER_WORK_DETALS"
        private const val LUMPER_SCHEDULE = "LUMPER_SCHEDULE"
        internal const val TEMP_LUMPER_IDS = "TEMP_LUMPER_IDS"
        const val TOTAL_CASES = "TOTAL_CASES"
        @JvmStatic
        fun newInstance(
            allWorkItem: WorkItemContainerDetails?,
            lumperTimeSchedule: ArrayList<LumpersTimeSchedule>?,
            tempLumperIds: ArrayList<String>?
        ) = WorkSheetItemDetailLumpersFragment()
            .apply {
                arguments = Bundle().apply {
                    if(allWorkItem!=null){
                        putParcelable(LUMPER_WORK_DETALS, allWorkItem)
                        putParcelableArrayList(LUMPER_SCHEDULE, lumperTimeSchedule)
                        putStringArrayList(TEMP_LUMPER_IDS, tempLumperIds)
                    }
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is WorkSheetItemDetailContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = activity as WorkSheetItemDetailContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(LUMPER_WORK_DETALS))
            workItemDetail = it.getParcelable<WorkItemContainerDetails>(LUMPER_WORK_DETALS)
            if (it.containsKey(LUMPER_SCHEDULE))
                lumpersTimeSchedule = it.getParcelableArrayList(LUMPER_SCHEDULE)!!
            if (it.containsKey(LUMPER_WORK_DETALS))
                tempLumperIds = it.getStringArrayList(TEMP_LUMPER_IDS)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_work_sheet_item_detail_lumpers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            workSheetItemDetailLumpersAdapter = WorkSheetItemDetailLumpersAdapter(this@WorkSheetItemDetailLumpersFragment)
            adapter = workSheetItemDetailLumpersAdapter
        }

        workSheetItemDetailLumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (workSheetItemDetailLumpersAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        buttonAddLumpers.setOnClickListener(this)
        workItemDetail?.let { showLumpersData(it, lumpersTimeSchedule,tempLumperIds) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            onFragmentInteractionListener?.fetchWorkItemDetail(changeResultCode = true)
        }
    }

    fun showLumpersData(workItemDetail: WorkItemContainerDetails, lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?, tempLumperIds: ArrayList<String>) {
        this.workItemDetail = workItemDetail
        this.tempLumperIds=tempLumperIds

        val timingsData = LinkedHashMap<String, LumpersTimeSchedule>()
        workItemDetail.assignedLumpersList?.let { assignedLumpersList ->
            if (!lumpersTimeSchedule.isNullOrEmpty()) {
                for (lumper in assignedLumpersList) {
                    for (timing in lumpersTimeSchedule) {
                        if (lumper.id == timing.lumperId) {
                            timingsData[lumper.id!!] = timing
                            break
                        }
                    }
                }
            }
        }

        workSheetItemDetailLumpersAdapter.updateList(workItemDetail.assignedLumpersList, timingsData, workItemDetail.status, tempLumperIds, getTotalCases(workItemDetail?.buildingOps), workItemDetail.isCompleted)

//        if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
//            buttonAddLumpers.text = getString(R.string.add_lumpers)
//        } else {
//            buttonAddLumpers.text = getString(R.string.update_lumpers)
//        }

        workItemDetail.status?.let { status ->
            if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED || status == AppConstant.WORK_ITEM_STATUS_CANCELLED) {
                buttonAddLumpers.visibility =if (!workItemDetail.isCompleted!!) View.VISIBLE else View.GONE
                textViewEmptyData.text = getString(R.string.empty_work_item_lumpers_past_date_info_message)
            } else {
                buttonAddLumpers.visibility = View.VISIBLE
                textViewEmptyData.text = getString(R.string.empty_work_item_detail_page_lumpers_info_message)
            }
        }
    }

    fun showEmptyData() {
//        workSheetItemDetailLumpersAdapter.updateList(
//            ArrayList(),
//            LinkedHashMap(),
//            tempLumperIds = ArrayList(),
//            totalCases = getTotalCases(workItemDetail?.buildingOps),
//            isCompleted = workItemDetail!!.isCompleted
//        )
//        buttonAddLumpers.visibility = View.GONE
    }


    private fun showAddLumpersScreen() {
        workItemDetail?.let { workItemDetail ->
            val bundle = Bundle()
            bundle.putString(ARG_WORK_ITEM_ID, workItemDetail.id)
            bundle.putString(ARG_WORK_ITEM_TYPE, workItemDetail.type)
            if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
                bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, true)
            } else {
                bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, false)
                bundle.putParcelableArrayList(AddWorkItemLumpersActivity.ARG_ASSIGNED_LUMPERS_LIST, workItemDetail.assignedLumpersList)
            }
            startIntent(AddWorkItemLumpersActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
        }
    }

    private fun getTotalCases(permeters: HashMap<String, String>?): String? {
        var cases: String = ""
        if (!permeters.isNullOrEmpty() && permeters.size > 0) {
            cases = permeters.get("Cases").toString()
        }
        return cases
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                buttonAddLumpers.id -> showAddLumpersScreen()
            }
        }
    }

    /** Adapter Listeners */
    override fun onAddTimeClick(employeeData: LumperAttendanceData, timingData: LumpersTimeSchedule?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val bundle = Bundle()
        bundle.putString(ARG_WORK_ITEM_ID, workItemDetail?.id)
        bundle.putString(TOTAL_CASES, getTotalCases(workItemDetail?.buildingOps))
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        bundle.putBoolean(ARG_LUMPER_PRESENT, employeeData?.attendanceDetail?.isPresent!!)
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_TIMING_DATA, timingData)
        bundle.putStringArrayList(TEMP_LUMPER_IDS, tempLumperIds)
        startIntent(AddLumperTimeWorkSheetItemActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    override fun onRemoveLumperClick(employeeData: LumperAttendanceData, adapterPosition: Int) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
    }
}
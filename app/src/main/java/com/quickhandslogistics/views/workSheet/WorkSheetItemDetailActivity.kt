package com.quickhandslogistics.views.workSheet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.WorkSheetItemDetailPagerAdapter
import com.quickhandslogistics.adapters.workSheet.WorkSheetItemStatusAdapter
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.presenters.workSheet.WorkSheetItemDetailPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import kotlinx.android.synthetic.main.activity_work_sheet_item_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_select_status.*
import kotlinx.android.synthetic.main.content_work_sheet_item_detail.*
import kotlinx.android.synthetic.main.content_work_sheet_item_detail.textViewWorkSheetNote as textViewWorkSheetNote1

class WorkSheetItemDetailActivity : BaseActivity(), View.OnClickListener, WorkSheetItemDetailContract.View,
        WorkSheetItemDetailContract.View.OnAdapterItemClickListener, WorkSheetItemDetailContract.View.OnFragmentInteractionListener {

    private var workItemId: String = ""
    private var workItemTypeDisplayName: String = ""
    private var workItemDetail: WorkItemContainerDetails = WorkItemContainerDetails()
    private var lumpersTimeSchedule: ArrayList<LumpersTimeSchedule> = ArrayList()
    private var tempLumperIds: ArrayList<String> = ArrayList()

    private lateinit var workSheetItemDetailPresenter: WorkSheetItemDetailPresenter
    private var workSheetItemStatusAdapter: WorkSheetItemStatusAdapter? = null
    private var workSheetItemDetailPagerAdapter: WorkSheetItemDetailPagerAdapter? = null

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    companion object {
        const val WORK_DETAIL_LIST = "WORK_DETAIL_LIST"
        const val TEMP_LUMPER_ID_LIST = "TEMP_LUMPER_ID_LIST"
        const val LUMPER_TIME_SCHEDULE = "LUMPER_TIME_SCHEDULE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_sheet_item_detail)
        setupToolbar(getString(R.string.container_details))


        intent.extras?.let { it ->
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemTypeDisplayName = it.getString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, "")
        }

        workSheetItemDetailPresenter = WorkSheetItemDetailPresenter(this, resources)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LUMPER_TIME_SCHEDULE)) {
                lumpersTimeSchedule =
                        savedInstanceState.getParcelableArrayList(
                                LUMPER_TIME_SCHEDULE
                        )!!
            }
            if (savedInstanceState.containsKey(TEMP_LUMPER_ID_LIST)) tempLumperIds =
                    savedInstanceState.getStringArrayList(TEMP_LUMPER_ID_LIST)!!
            if (savedInstanceState.containsKey(WORK_DETAIL_LIST)) {
                workItemDetail =
                        savedInstanceState.getParcelable(WORK_DETAIL_LIST)!!
//                showWorkItemDetail(workItemDetail, lumpersTimeSchedule, tempLumperIds)
                val allWorkItemLists = createDifferentListData(workItemDetail)
                initializeUI(allWorkItemLists, tempLumperIds, lumpersTimeSchedule)
            }
        } ?: run {
            initializeUI()
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            workSheetItemDetailPresenter.fetchWorkItemDetail(workItemId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(WORK_DETAIL_LIST, workItemDetail)
        outState.putParcelableArrayList(LUMPER_TIME_SCHEDULE, lumpersTimeSchedule)
        outState.putStringArrayList(TEMP_LUMPER_ID_LIST, tempLumperIds)
        super.onSaveInstanceState(outState)
    }

    private fun createDifferentListData(workItemDetail: WorkItemContainerDetails): WorkItemContainerDetails {
        textViewStartTime.text = String.format(getString(R.string.start_time_s), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))

        when (workItemTypeDisplayName) {
            getString(R.string.drops) -> textViewDropItems.text = String.format(getString(R.string.no_of_drops_s), workItemDetail.quantity)
            getString(R.string.live_loads) -> textViewDropItems.text = String.format(getString(R.string.live_load_s), workItemDetail.quantity)
            else -> textViewDropItems.text = String.format(getString(R.string.out_bound_s), workItemDetail.quantity)
        }

        if (!workItemDetail.status.isNullOrEmpty()) {
            updateStatusBackground(workItemDetail.status!!)
        }
        return workItemDetail
    }

    private fun initializeUI(allWorkItem: WorkItemContainerDetails? = null, tampLumpId: ArrayList<String>? = null, lumperTimeSchedule: ArrayList<LumpersTimeSchedule>? = null) {

        workSheetItemDetailPagerAdapter = if (allWorkItem != null)
            WorkSheetItemDetailPagerAdapter(supportFragmentManager, resources, allWorkItem, tampLumpId, lumperTimeSchedule)
        else
            WorkSheetItemDetailPagerAdapter(supportFragmentManager, resources)
        viewPagerWorkSheetDetail.offscreenPageLimit = workSheetItemDetailPagerAdapter?.count!!
        viewPagerWorkSheetDetail.adapter = workSheetItemDetailPagerAdapter
        tabLayoutWorkSheetDetail.setupWithViewPager(viewPagerWorkSheetDetail)

        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetStatus)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        recyclerViewStatus.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            workSheetItemStatusAdapter = WorkSheetItemStatusAdapter(resources, this@WorkSheetItemDetailActivity)
            adapter = workSheetItemStatusAdapter
        }

        textViewStatus.setOnClickListener(this)
        textViewWorkSheetNote1.setOnClickListener(this)
        bottomSheetBackgroundStatus.setOnClickListener(this)
    }

    private fun updateStatusBackground(status: String) {
        //val statusList: LinkedHashMap<String, String> = LinkedHashMap()
        //statusList.putAll(ScheduleUtils.createStatusList(resources, status))

        ScheduleUtils.changeStatusUIByValue(resources, status, textViewStatus, isEditable = true)

        //workSheetItemStatusAdapter?.updateStatusList(statusList)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundStatus.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                bottomSheetBackgroundStatus.id -> closeBottomSheet()
                textViewStatus.id -> {
                    if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        workSheetItemStatusAdapter?.updateInitialStatus(textViewStatus.text.toString(), workItemDetail.isCompleted!!)
                        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        bottomSheetBackgroundStatus.visibility = View.VISIBLE
                    } else {
                        closeBottomSheet()
                    }

                }
                textViewWorkSheetNote1.id -> {
                    if (!workItemDetail.schedule?.scheduleNote.isNullOrEmpty() && !workItemDetail.schedule?.scheduleNote.equals("NA")) {
                        val title =
                                ScheduleUtils.scheduleNotePopupTitle(workItemDetail.schedule, resources)
                        CustomProgressBar.getInstance()
                                .showInfoDialog(title, workItemDetail.schedule?.scheduleNote!!, this)
                    }
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)

        workSheetItemDetailPagerAdapter?.showEmptyData()
    }

    override fun showWorkItemDetail(workItemDetail: WorkItemContainerDetails) {
        this.lumpersTimeSchedule = lumpersTimeSchedule!!

        this.workItemDetail = workItemDetail
        this.tempLumperIds = tempLumperIds
        textViewStartTime.text = String.format(getString(R.string.start_time_s), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))
        if (!workItemDetail.schedule?.scheduleNote.isNullOrEmpty() && !workItemDetail.schedule?.scheduleNote.equals("NA")) {
            textViewWorkSheetNote1.isEnabled = true
            textViewWorkSheetNote1.text = ScheduleUtils.scheduleTypeNote(workItemDetail.schedule, resources)
        } else {
            textViewWorkSheetNote1.isEnabled = false
        }

        when (workItemTypeDisplayName) {
            getString(R.string.drops) -> textViewDropItems.text = String.format(getString(R.string.no_of_drops_s), workItemDetail.quantity)
            getString(R.string.live_loads) -> textViewDropItems.text = String.format(getString(R.string.live_load_s), workItemDetail.quantity)
            else -> textViewDropItems.text = String.format(getString(R.string.out_bound_s), workItemDetail.quantity)
        }

        if (!workItemDetail.status.isNullOrEmpty()) {
            updateStatusBackground(workItemDetail.status!!)
        }

        workSheetItemDetailPagerAdapter?.showWorkItemData(workItemDetail, lumpersTimeSchedule, tempLumperIds)
    }

    override fun statusChangedSuccessfully() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        setResult(RESULT_OK)
    }

    override fun notesSavedSuccessfully() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        isDataSave(true)
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.notes_saved_success_alert_message), activity, object : CustomDialogListener {
            override fun onConfirmClick() {

            }
        })
//        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, getString(R.string.notes_saved_success_alert_message))
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */
    override fun onSelectStatus(status: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        if (status == AppConstant.WORK_ITEM_STATUS_COMPLETED) {
//            val filledParameterCount = ScheduleUtils.getFilledBuildingParametersCounts(workItemDetail)
//            val parameters = ScheduleUtils.getBuildingParametersList(workItemDetail.buildingDetailData)

//            if (workItemDetail.buildingOps.isNullOrEmpty() || filledParameterCount != parameters.size) {
//                CustomProgressBar.getInstance().showErrorDialog(getString(R.string.fill_building_parameters_message), activity)
//                closeBottomSheet()
//                return
//            } else if (workItemDetail.assignedLumpersList.isNullOrEmpty()) {
//                CustomProgressBar.getInstance().showErrorDialog(getString(R.string.assign_lumpers_message), activity)
//                closeBottomSheet()
//                return
//            } else {
//                if (lumpersTimeSchedule.isNullOrEmpty() || lumpersTimeSchedule.size < workItemDetail!!.assignedLumpersList!!.size) {
//                    val message = getString(R.string.assign_lumpers_endtime_starttime_message)
//                    CustomProgressBar.getInstance().showErrorDialog(message, this.activity)
//                    closeBottomSheet()
//                    return
//                } else if (!lumpersTimeSchedule.isNullOrEmpty()) {
//                    val message = getStartTimeCount(lumpersTimeSchedule)
//                    if (message.isNotEmpty()) {
//                        CustomProgressBar.getInstance().showErrorDialog(message, this.activity)
//                        closeBottomSheet()
//                        return
//                    }
//                }
//
//            }
        } else if (AppConstant.WORK_ITEM_STATUS_UNFINISHED == status) {
            closeBottomSheet()
            CustomBottomSheetDialog.unfinishedBottomSheetDialog(activity,  object : CustomBottomSheetDialog.IDialogOnClick {
                override fun onSendRequest(dialog: Dialog) {
                    Toast.makeText(applicationContext, "submit button clicked", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            })
            return
        }

        var message = getString(R.string.change_status_alert_message)
        if (status == AppConstant.WORK_ITEM_STATUS_CANCELLED || status == AppConstant.WORK_ITEM_STATUS_COMPLETED) {
            message = getString(R.string.change_status_permanently_alert_message)
        }
        closeBottomSheet()
        workSheetItemDetailPresenter.changeWorkItemStatus(workItemId, status)
    }

    private fun getStartTimeCount(lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>): String {
        var message = ""
        lumpersTimeSchedule.forEach {
            if (it.startTime.isNullOrEmpty() || it.endTime.isNullOrEmpty()) {
                message = getString(R.string.assign_lumpers_endtime_starttime_message)
            } else if (!it.breakTimeStart.isNullOrEmpty() && it.breakTimeEnd.isNullOrEmpty()) {
                message = getString(R.string.assign_lumpers_bracktime_message)
            } else if (it.partWorkDone.isNullOrEmpty() || it.partWorkDone!!.toInt() == 0) {
                message = getString(R.string.assign_work_done_message)
            }
        }
        return message
    }

    /** Child Fragment Interaction Listeners */
    override fun fetchWorkItemDetail(changeResultCode: Boolean) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        if (changeResultCode)
            setResult(RESULT_OK)
        isDataSave(true)
        workSheetItemDetailPresenter.fetchWorkItemDetail(workItemId)
    }

    override fun updateWorkItemNotes(notesQHLCustomer: String, notesQHL: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
        workSheetItemDetailPresenter.updateWorkItemNotes(workItemId, notesQHLCustomer, notesQHL)
    }

    override fun dataChanged(isChanged: Boolean) {
        if (isChanged) isDataSave(false) else isDataSave(true)
    }
}
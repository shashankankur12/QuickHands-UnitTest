package com.quickhandslogistics.modified.views.workSheet

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.workSheet.WorkSheetItemDetailPagerAdapter
import com.quickhandslogistics.modified.adapters.workSheet.WorkSheetItemStatusAdapter
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.modified.presenters.workSheet.WorkSheetItemDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.activity_work_sheet_item_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_select_status.*
import kotlinx.android.synthetic.main.content_work_sheet_item_detail.*

class WorkSheetItemDetailActivity : BaseActivity(), View.OnClickListener, WorkSheetItemDetailContract.View,
    WorkSheetItemDetailContract.View.OnAdapterItemClickListener, WorkSheetItemDetailContract.View.OnFragmentInteractionListener {

    private var workItemId: String = ""
    private var workItemTypeDisplayName: String = ""

    private lateinit var workSheetItemDetailPresenter: WorkSheetItemDetailPresenter
    private lateinit var workSheetItemStatusAdapter: WorkSheetItemStatusAdapter
    private lateinit var workSheetItemDetailPagerAdapter: WorkSheetItemDetailPagerAdapter

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_sheet_item_detail)
        setupToolbar(getString(R.string.work_sheet_detail))

        intent.extras?.let { it ->
            workItemId = it.getString(ARG_WORK_ITEM_ID, "")
            workItemTypeDisplayName = it.getString(ARG_WORK_ITEM_TYPE_DISPLAY_NAME, "")
        }

        initializeUI()

        workSheetItemDetailPresenter = WorkSheetItemDetailPresenter(this, resources)
        workSheetItemDetailPresenter.fetchWorkItemDetail(workItemId)
    }

    private fun initializeUI() {
        workSheetItemDetailPagerAdapter = WorkSheetItemDetailPagerAdapter(supportFragmentManager, resources)
        viewPagerWorkSheetDetail.offscreenPageLimit = workSheetItemDetailPagerAdapter.count
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
        bottomSheetBackgroundStatus.setOnClickListener(this)
    }

    private fun updateStatusBackground(status: String) {
        val statusList: LinkedHashMap<String, String> = LinkedHashMap()
        statusList.putAll(ScheduleUtils.createStatusList(resources, status))

        ScheduleUtils.changeStatusUIByValue(resources, status, textViewStatus, isEditable = true)

        workSheetItemStatusAdapter.updateStatusList(statusList)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundStatus.visibility = View.GONE
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                bottomSheetBackgroundStatus.id -> closeBottomSheet()
                textViewStatus.id -> {
                    if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        workSheetItemStatusAdapter.updateInitialStatus(textViewStatus.text.toString())
                        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        bottomSheetBackgroundStatus.visibility = View.VISIBLE
                    } else {
                        closeBottomSheet()
                    }
                }
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)

        workSheetItemDetailPagerAdapter.showEmptyData()
    }

    override fun showWorkItemDetail(workItemDetail: WorkItemDetail, lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?) {
        textViewStartTime.text = String.format(getString(R.string.start_time_s), DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime))
        textViewWorkItemType.text = workItemTypeDisplayName

        when (workItemTypeDisplayName) {
            getString(R.string.drops) -> textViewDropItems.text = String.format(getString(R.string.no_of_drops_s), workItemDetail.numberOfDrops)
            getString(R.string.live_loads) -> textViewDropItems.text = String.format(getString(R.string.live_load_s), workItemDetail.sequence)
            else -> textViewDropItems.text = String.format(getString(R.string.out_bound_s), workItemDetail.sequence)
        }

        if (!workItemDetail.status.isNullOrEmpty()) {
            updateStatusBackground(workItemDetail.status!!)
        }

        workSheetItemDetailPagerAdapter.showWorkItemData(workItemDetail, lumpersTimeSchedule)
    }

    override fun statusChangedSuccessfully() {
        setResult(RESULT_OK)
    }

    override fun notesSavedSuccessfully() {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, getString(R.string.notes_saved_success_alert_message))
    }

    /** Adapter Listeners */
    override fun onSelectStatus(status: String) {
        var message = getString(R.string.change_status_alert_message)
        if (status == AppConstant.WORK_ITEM_STATUS_CANCELLED || status == AppConstant.WORK_ITEM_STATUS_COMPLETED) {
            message = getString(R.string.change_status_permanently_alert_message)
        }
        CustomProgressBar.getInstance().showWarningDialog(message, activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                closeBottomSheet()
                workSheetItemDetailPresenter.changeWorkItemStatus(workItemId, status)
            }

            override fun onCancelClick() {
                workSheetItemStatusAdapter.updateInitialStatus(textViewStatus.text.toString())
            }
        })
    }

    /** Child Fragment Interaction Listeners */
    override fun fetchWorkItemDetail(changeResultCode: Boolean) {
        if (changeResultCode)
            setResult(RESULT_OK)
        workSheetItemDetailPresenter.fetchWorkItemDetail(workItemId)
    }

    override fun updateWorkItemNotes(notesQHLCustomer: String, notesQHL: String) {
        workSheetItemDetailPresenter.updateWorkItemNotes(workItemId, notesQHLCustomer, notesQHL)
    }
}
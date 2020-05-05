package com.quickhandslogistics.modified.views.workSheet

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.workSheet.WorkSheetItemDetailPagerAdapter
import com.quickhandslogistics.modified.adapters.workSheet.WorkSheetItemStatusAdapter
import com.quickhandslogistics.modified.contracts.common.InfoDialogWarningContract
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.modified.presenters.workSheet.WorkSheetItemDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.InfoWarningDialogFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.activity_work_sheet_item_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_select_status.*
import kotlinx.android.synthetic.main.content_work_sheet_item_detail.*


class WorkSheetItemDetailActivity : BaseActivity(), View.OnClickListener,
    WorkSheetItemDetailContract.View, WorkSheetItemDetailContract.View.OnAdapterItemClickListener,
    WorkSheetItemDetailContract.View.OnFragmentInteractionListener {

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
        workSheetItemDetailPagerAdapter =
            WorkSheetItemDetailPagerAdapter(supportFragmentManager, resources)
        viewPagerWorkSheetDetail.offscreenPageLimit = workSheetItemDetailPagerAdapter.count
        viewPagerWorkSheetDetail.adapter = workSheetItemDetailPagerAdapter
        tabLayoutWorkSheetDetail.setupWithViewPager(viewPagerWorkSheetDetail)

        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetStatus)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        recyclerViewStatus.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            workSheetItemStatusAdapter = WorkSheetItemStatusAdapter(
                resources, this@WorkSheetItemDetailActivity
            )
            adapter = workSheetItemStatusAdapter
        }

        textViewStatus.setOnClickListener(this)
        bottomSheetBackgroundStatus.setOnClickListener(this)
    }

    private fun updateStatusBackground(status: String) {
        val statusList: LinkedHashMap<String, String> = LinkedHashMap()

        when (status) {
            AppConstant.WORK_ITEM_STATUS_SCHEDULED -> {
                textViewStatus.text = resources.getString(R.string.scheduled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
                textViewStatus.isClickable = true
                textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0)

                statusList[resources.getString(R.string.scheduled)] =
                    AppConstant.WORK_ITEM_STATUS_SCHEDULED
                statusList[resources.getString(R.string.in_progress)] =
                    AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] =
                    AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] =
                    AppConstant.WORK_ITEM_STATUS_CANCELLED
            }
            AppConstant.WORK_ITEM_STATUS_ON_HOLD -> {
                textViewStatus.text = resources.getString(R.string.on_hold)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
                textViewStatus.isClickable = true
                textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0)

                statusList[resources.getString(R.string.in_progress)] =
                    AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] =
                    AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] =
                    AppConstant.WORK_ITEM_STATUS_CANCELLED
                statusList[resources.getString(R.string.completed)] =
                    AppConstant.WORK_ITEM_STATUS_COMPLETED
            }
            AppConstant.WORK_ITEM_STATUS_IN_PROGRESS -> {
                textViewStatus.text = resources.getString(R.string.in_progress)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
                textViewStatus.isClickable = true
                textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0)

                statusList[resources.getString(R.string.in_progress)] =
                    AppConstant.WORK_ITEM_STATUS_IN_PROGRESS
                statusList[resources.getString(R.string.on_hold)] =
                    AppConstant.WORK_ITEM_STATUS_ON_HOLD
                statusList[resources.getString(R.string.cancelled)] =
                    AppConstant.WORK_ITEM_STATUS_CANCELLED
                statusList[resources.getString(R.string.completed)] =
                    AppConstant.WORK_ITEM_STATUS_COMPLETED
            }
            AppConstant.WORK_ITEM_STATUS_CANCELLED -> {
                textViewStatus.text = resources.getString(R.string.cancelled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
                textViewStatus.isClickable = false
                textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
            AppConstant.WORK_ITEM_STATUS_COMPLETED -> {
                textViewStatus.text = resources.getString(R.string.completed)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
                textViewStatus.isClickable = false
                textViewStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        workSheetItemStatusAdapter.updateStatusList(statusList)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundStatus.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionCancelWorkItem -> {
                val dialog = InfoWarningDialogFragment.newInstance(
                    getString(R.string.string_ask_to_cancel_work_item),
                    positiveButtonText = getString(R.string.string_yes),
                    negativeButtonText = getString(R.string.string_no),
                    onClickListener = object : InfoDialogWarningContract.View.OnClickListener {
                        override fun onPositiveButtonClick() {
                            onBackPressed()
                        }

                        override fun onNegativeButtonClick() {
                        }
                    })
                dialog.show(supportFragmentManager, InfoWarningDialogFragment::class.simpleName)
            }
        }
        return super.onOptionsItemSelected(item)
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

    override fun onSelectStatus(status: String) {
        val dialog = InfoWarningDialogFragment.newInstance(
            getString(R.string.string_ask_to_change_status),
            positiveButtonText = getString(R.string.string_yes),
            negativeButtonText = getString(R.string.string_no),
            onClickListener = object : InfoDialogWarningContract.View.OnClickListener {
                override fun onPositiveButtonClick() {
                    closeBottomSheet()
                    workSheetItemDetailPresenter.changeWorkItemStatus(workItemId, status)
                }

                override fun onNegativeButtonClick() {
                    workSheetItemStatusAdapter.updateInitialStatus(textViewStatus.text.toString())
                }
            })
        dialog.show(supportFragmentManager, InfoWarningDialogFragment::class.simpleName)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)

        workSheetItemDetailPagerAdapter.showEmptyData()
    }

    override fun showWorkItemDetail(
        workItemDetail: WorkItemDetail, lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?
    ) {
        textViewStartTime.text = String.format(
            getString(R.string.start_time_container),
            DateUtils.convertMillisecondsToUTCTimeString(workItemDetail.startTime)
        )

        textViewWorkItemType.text = workItemTypeDisplayName

        when (workItemTypeDisplayName) {
            getString(R.string.string_drops) -> {
                textViewDropItems.text = String.format(
                    getString(R.string.no_of_drops),
                    workItemDetail.numberOfDrops
                )
            }
            getString(R.string.string_live_loads) -> {
                textViewDropItems.text = String.format(
                    getString(R.string.live_load_sequence),
                    workItemDetail.sequence
                )
            }
            else -> {
                textViewDropItems.text = String.format(
                    getString(R.string.outbound_sequence),
                    workItemDetail.sequence
                )
            }
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
        SnackBarFactory.createSnackBar(
            activity, mainConstraintLayout, getString(R.string.notes_saved_successfully)
        )
    }

    override fun fetchWorkItemDetail(changeResultCode: Boolean) {
        if (changeResultCode)
            setResult(RESULT_OK)
        workSheetItemDetailPresenter.fetchWorkItemDetail(workItemId)
    }

    override fun updateWorkItemNotes(notesQHLCustomer: String, notesQHL: String) {
        workSheetItemDetailPresenter.updateWorkItemNotes(workItemId, notesQHLCustomer, notesQHL)
    }
}

package com.quickhandslogistics.modified.views.workSheet

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
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
import com.quickhandslogistics.modified.presenters.workSheet.WorkSheetItemDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.InfoWarningDialogFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_ID
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_WORK_ITEM_TYPE_DISPLAY_NAME
import com.quickhandslogistics.utils.CustomProgressBar
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

    private var progressDialog: Dialog? = null

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
        when (status) {
            resources.getString(R.string.scheduled).toUpperCase() -> {
                textViewStatus.text = resources.getString(R.string.scheduled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
            }
            resources.getString(R.string.on_hold).toUpperCase() -> {
                textViewStatus.text = resources.getString(R.string.on_hold)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
            }
            resources.getString(R.string.cancelled).toUpperCase() -> {
                textViewStatus.text = resources.getString(R.string.cancelled)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
            }
            resources.getString(R.string.in_progress).toUpperCase() -> {
                textViewStatus.text = resources.getString(R.string.in_progress)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
            }
            resources.getString(R.string.completed).toUpperCase() -> {
                textViewStatus.text = resources.getString(R.string.completed)
                textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
            }
        }

        invalidateOptionsMenu()
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundStatus.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val menuItem = menu.findItem(R.id.actionCancelWorkItem)
            menuItem.isVisible = true
        }
        return super.onPrepareOptionsMenu(menu)
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

    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(activity).showProgressDialog(message)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showWorkItemDetail(workItemDetail: WorkItemDetail) {
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

        workSheetItemDetailPagerAdapter.showWorkItemData(workItemDetail)
    }

    override fun fetchWorkItemDetail() {
        workSheetItemDetailPresenter.fetchWorkItemDetail(workItemId)
    }
}

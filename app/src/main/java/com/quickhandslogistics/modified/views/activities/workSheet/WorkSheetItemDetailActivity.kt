package com.quickhandslogistics.modified.views.activities.workSheet

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.InfoDialogWarningContract
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.workSheet.WorkItemStatusAdapter
import com.quickhandslogistics.modified.views.adapters.workSheet.WorkSheetItemDetailPagerAdapter
import com.quickhandslogistics.modified.views.fragments.InfoWarningDialogFragment
import kotlinx.android.synthetic.main.activity_work_sheet_item_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_select_status.*
import kotlinx.android.synthetic.main.content_work_sheet_item_detail.*

class WorkSheetItemDetailActivity : BaseActivity(), View.OnClickListener,
    WorkSheetItemDetailContract.View.OnAdapterItemClickListener {

    private lateinit var workItemStatusAdapter: WorkItemStatusAdapter
    private lateinit var adapter: WorkSheetItemDetailPagerAdapter

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_sheet_item_detail)
        setupToolbar(getString(R.string.work_sheet_detail))

        initializeUI()
    }

    private fun initializeUI() {
        adapter = WorkSheetItemDetailPagerAdapter(supportFragmentManager, resources)
        viewPagerWorkSheetDetail.adapter = adapter
        tabLayoutWorkSheetDetail.setupWithViewPager(viewPagerWorkSheetDetail)

        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetStatus)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        recyclerViewStatus.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            workItemStatusAdapter = WorkItemStatusAdapter(
                resources, this@WorkSheetItemDetailActivity
            )
            adapter = workItemStatusAdapter
        }

        textViewStatus.setOnClickListener(this)
        bottomSheetBackgroundStatus.setOnClickListener(this)

        updateStatusBackground()
    }

    private fun updateStatusBackground() {
        when (textViewStatus.text.toString()) {
            resources.getString(R.string.in_progress) ->
                textViewStatus.setBackgroundResource(R.drawable.chip_background_in_progress)
            resources.getString(R.string.on_hold) ->
                textViewStatus.setBackgroundResource(R.drawable.chip_background_on_hold)
            resources.getString(R.string.scheduled) ->
                textViewStatus.setBackgroundResource(R.drawable.chip_background_scheduled)
            resources.getString(R.string.cancelled) ->
                textViewStatus.setBackgroundResource(R.drawable.chip_background_cancelled)
            resources.getString(R.string.completed) ->
                textViewStatus.setBackgroundResource(R.drawable.chip_background_completed)
        }
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
                        workItemStatusAdapter.updateInitialStatus(textViewStatus.text.toString())
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
                    textViewStatus.text = status
                    updateStatusBackground()
                    closeBottomSheet()
                }

                override fun onNegativeButtonClick() {
                    workItemStatusAdapter.updateInitialStatus(textViewStatus.text.toString())
                }
            })
        dialog.show(supportFragmentManager, InfoWarningDialogFragment::class.simpleName)
    }
}

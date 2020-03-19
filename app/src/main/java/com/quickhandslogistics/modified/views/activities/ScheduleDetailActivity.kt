package com.quickhandslogistics.modified.views.activities

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.ScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleFragment
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.view.activities.BuildingOperationsActivity
import com.quickhandslogistics.view.activities.WorkItemLumpersActivity
import com.quickhandslogistics.view.adapter.LumperAttendanceAdapter
import kotlinx.android.synthetic.main.activity_schedule_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_lumpers_attendance.*
import kotlinx.android.synthetic.main.container_schedule_detail.*

class ScheduleDetailActivity : BaseActivity(), SpeedDialView.OnActionSelectedListener,
    ScheduleDetailContract.View.OnAdapterItemClickListener, View.OnClickListener {

    private var isCurrentDate: Boolean = false
    private var sheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)
        setupToolbar()

        intent.extras?.let {
            if (it.containsKey(ScheduleFragment.ARG_SELECTED_TIME)) {
                val selectedTime = it.getLong(ScheduleFragment.ARG_SELECTED_TIME)
                isCurrentDate = DateUtils.isCurrentDate(selectedTime)
            }
        }

        initializeUI()
    }

    private fun initializeUI() {
        // Initialize Floating Action Menu
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.actionBuildingOperations, R.drawable.ic_business)
                .setLabel(getString(R.string.building_operations))
                .setLabelColor(ResourcesCompat.getColor(resources, R.color.textBlack, theme))
                .create()
        )

        if (isCurrentDate) {
            speedDialView.addActionItem(
                SpeedDialActionItem.Builder(
                        R.id.actionMarkAttendance,
                        R.drawable.ic_event_available
                    )
                    .setLabel(getString(R.string.mark_attendance))
                    .setLabelColor(ResourcesCompat.getColor(resources, R.color.textBlack, theme))
                    .create()
            )
        }
        speedDialView.setOnActionSelectedListener(this)

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        imageViewClose.setOnClickListener(this)
        button_submit.setOnClickListener(this)

        recycler_work_items.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            adapter = ScheduledWorkItemAdapter(activity, this@ScheduleDetailActivity, isCurrentDate)
        }

        recycler_lumpers.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = LumperAttendanceAdapter(activity)
        }
    }

    /*
    * Native Views Listeners
    */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewClose.id -> {
                    sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
                    bottomSheetBackground.visibility = View.GONE
                }
                button_submit.id -> {
                    sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
                    bottomSheetBackground.visibility = View.GONE
                }
            }
        }
    }

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        actionItem?.let {
            return when (actionItem.id) {
                R.id.actionBuildingOperations -> {
                    speedDialView.close(true)
                    startIntent(BuildingOperationsActivity::class.java)
                    true
                }
                R.id.actionMarkAttendance -> {
                    speedDialView.close(true)
                    if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED;
                        bottomSheetBackground.visibility = View.VISIBLE
                    } else {
                        sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
                        bottomSheetBackground.visibility = View.GONE
                    }
                    true
                }
                else -> false
            }
        }
        return false
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onWorkItemClick(sameDay: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(WorkItemLumpersActivity.ARG_CAN_REPLACE, sameDay)
        startIntent(WorkItemLumpersActivity::class.java, bundle = bundle)
    }
}

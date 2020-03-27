package com.quickhandslogistics.modified.views.activities.schedule

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.adapters.ScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleFragment
import com.quickhandslogistics.utils.DateUtils
import kotlinx.android.synthetic.main.content_schedule_detail.*

class ScheduleDetailActivity : BaseActivity(), SpeedDialView.OnActionSelectedListener,
    LumperImagesAdapter.OnAdapterItemClickListener,
    ScheduleDetailContract.View.OnAdapterItemClickListener {

    private var isCurrentDate: Boolean = false

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

        recyclerViewLumpersImagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val lumperImages = ArrayList<ImageData>()

            for (i in 1..7) {
                lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
            }
            addItemDecoration(OverlapDecoration())
            val scheduleLumperImageAdapter =
                LumperImagesAdapter(lumperImages, this@ScheduleDetailActivity)
            adapter = scheduleLumperImageAdapter
        }

        recycler_work_items.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            adapter = ScheduledWorkItemAdapter(activity, this@ScheduleDetailActivity, isCurrentDate)
        }
    }

    /*
    * Native Views Listeners
    */
    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        actionItem?.let {
            return when (actionItem.id) {
                R.id.actionBuildingOperations -> {
                    speedDialView.close(true)
                    val bundle = Bundle()
                    bundle.putBoolean(BuildingOperationsActivity.ARG_ALLOW_UPDATE, isCurrentDate)
                    startIntent(BuildingOperationsActivity::class.java, bundle = bundle)
                    true
                }
                R.id.actionMarkAttendance -> {
                    speedDialView.close(true)
                    startIntent(MarkAttendanceActivity::class.java)
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
        bundle.putBoolean(WorkItemDetailActivity.ARG_ALLOW_UPDATE, sameDay)
        startIntent(WorkItemDetailActivity::class.java, bundle = bundle)
    }

    override fun onLumperImagesClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    override fun onLumperItemClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }
}

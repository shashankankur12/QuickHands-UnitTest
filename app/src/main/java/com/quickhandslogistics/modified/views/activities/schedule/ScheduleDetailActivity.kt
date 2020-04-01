package com.quickhandslogistics.modified.views.activities.schedule

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.adapters.schedule.ScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment
import kotlinx.android.synthetic.main.content_schedule_detail.*

class ScheduleDetailActivity : BaseActivity(),
    ScheduleDetailContract.View.OnAdapterItemClickListener,
    LumperImagesContract.OnItemClickListener {

    private var allowUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)
        setupToolbar()

        intent.extras?.let {
            if (it.containsKey(ScheduleMainFragment.ARG_ALLOW_UPDATE)) {
                allowUpdate = it.getBoolean(ScheduleMainFragment.ARG_ALLOW_UPDATE)
            }
        }

        initializeUI()
    }

    private fun initializeUI() {
        recyclerViewLumpersImagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val lumperImages = ArrayList<ImageData>()

            for (i in 1..7) {
                lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
            }
            addItemDecoration(OverlapDecoration())
            adapter = LumperImagesAdapter(lumperImages, this@ScheduleDetailActivity)
        }

        recyclerViewLiveLoad.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            adapter = ScheduledWorkItemAdapter(resources, this@ScheduleDetailActivity)
        }

        recyclerViewDrops.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            adapter = ScheduledWorkItemAdapter(resources, this@ScheduleDetailActivity)
        }

        recyclerViewOutBonds.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            adapter = ScheduledWorkItemAdapter(resources, this@ScheduleDetailActivity)
        }
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onLumperImageItemClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    override fun onLumperImagesClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    override fun onWorkItemClick() {
        val bundle = Bundle()
        bundle.putBoolean(ScheduleMainFragment.ARG_ALLOW_UPDATE, allowUpdate)
        startIntent(ScheduledWorkItemDetailActivity::class.java, bundle = bundle)
    }
}
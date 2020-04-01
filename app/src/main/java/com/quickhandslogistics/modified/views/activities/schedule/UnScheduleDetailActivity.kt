package com.quickhandslogistics.modified.views.activities.schedule

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LumperImagesContract
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.adapters.schedule.UnScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import kotlinx.android.synthetic.main.content_unschedule_detail.*

class UnScheduleDetailActivity : BaseActivity(), LumperImagesContract.OnItemClickListener,
    UnScheduleDetailContract.View.OnAdapterItemClickListener, View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unschedule_detail)
        setupToolbar()

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
            adapter = LumperImagesAdapter(lumperImages, this@UnScheduleDetailActivity)
        }

        recyclerViewLiveLoad.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            adapter = UnScheduledWorkItemAdapter(resources, this@UnScheduleDetailActivity)
        }

        recyclerViewDrops.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            adapter = UnScheduledWorkItemAdapter(resources, this@UnScheduleDetailActivity)
        }

        recyclerViewOutBonds.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            adapter = UnScheduledWorkItemAdapter(resources, this@UnScheduleDetailActivity)
        }

        buttonScheduleNow.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
//            val position = data?.getIntExtra("position", 0)!!
//            val count = data.getIntExtra("count", 0)
//
//            lumpersCountList[position] = count
//
//            unScheduledWorkItemAdapter.updateCount(lumpersCountList)
//        }
    }

    override fun onLumperImagesClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    override fun onWorkItemClick() {
        startIntent(UnscheduledWorkItemDetailActivity::class.java)
    }

    override fun onAddLumpersItemClick() {
        val bundle = Bundle()
        bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, true)
        startIntent(AddWorkItemLumpersActivity::class.java, bundle = bundle)
    }

    override fun onLumperImageItemClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonScheduleNow.id -> onBackPressed()
            }
        }
    }
}
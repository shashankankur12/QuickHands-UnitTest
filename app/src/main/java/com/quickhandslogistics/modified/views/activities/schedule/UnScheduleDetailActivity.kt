package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.adapters.LumperImagesAdapter
import com.quickhandslogistics.modified.views.adapters.UnScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleFragment
import com.quickhandslogistics.utils.DateUtils
import kotlinx.android.synthetic.main.container_unschedule_detail.*

class UnScheduleDetailActivity : BaseActivity(), LumperImagesAdapter.OnAdapterItemClickListener,
    UnScheduledWorkItemAdapter.OnAdapterItemClickListener {

    private lateinit var scheduleLumperImageAdapter: LumperImagesAdapter
    private lateinit var unScheduleLumperImageAdapter: UnScheduledWorkItemAdapter
    private var isCurrentDate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unschedule_detail)
        setupToolbar()

        intent.extras?.let {
            if (it.containsKey(ScheduleFragment.ARG_SELECTED_TIME)) {
                val selectedTime = it.getLong(ScheduleFragment.ARG_SELECTED_TIME)
                isCurrentDate = DateUtils.isCurrentDate(selectedTime)
            }
        }

        if (isCurrentDate)
            buttonSchduleNow.visibility = View.VISIBLE
        else
            buttonSchduleNow.visibility = View.GONE

        createDummyAddedLumpersList()

        recyclerViewLumpersImagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val lumperImages = ArrayList<ImageData>()

            for (i in 1..7) {
                lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
            }
            addItemDecoration(OverlapDecoration())
            scheduleLumperImageAdapter =
                LumperImagesAdapter(lumperImages, this@UnScheduleDetailActivity)
            adapter = scheduleLumperImageAdapter
        }

        recycler_work_items.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            unScheduleLumperImageAdapter =
                UnScheduledWorkItemAdapter(
                    this@UnScheduleDetailActivity,
                    activity,
                    isCurrentDate,
                    lumpersCountList
                )
            adapter = unScheduleLumperImageAdapter
        }
    }

    private lateinit var adapter: UnScheduledWorkItemAdapter
    var lumpersCountList: ArrayList<Int> = ArrayList()

    private fun createDummyAddedLumpersList() {
        lumpersCountList.add(0)
        lumpersCountList.add(0)
        lumpersCountList.add(0)
        lumpersCountList.add(0)
        lumpersCountList.add(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val position = data?.getIntExtra("position", 0)!!
            val count = data.getIntExtra("count", 0)

            lumpersCountList[position] = count

            unScheduleLumperImageAdapter.updateCount(lumpersCountList)
        }
    }

    override fun onLumperItemClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    override fun onItemClick() {
    }
}

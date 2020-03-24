package com.quickhandslogistics.modified.views.activities.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.schedule.ImageData
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.ScheduleLumperImagesAdapter
import com.quickhandslogistics.modified.views.controls.OverlapDecoration
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleFragment
import com.quickhandslogistics.view.adapter.UnScheduledWorkItemAdapter
import kotlinx.android.synthetic.main.container_unschedule_detail.*
import java.util.*
import kotlin.collections.ArrayList

class UnScheduleDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unschedule_detail)
        setupToolbar()

        var time: Long = 0
        intent.extras?.let {
            if (it.containsKey(ScheduleFragment.ARG_SELECTED_TIME)) {
                time = it.getLong(ScheduleFragment.ARG_SELECTED_TIME)
            }
        }

        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = time
        val sameDay =
            cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR] && cal1[Calendar.YEAR] == cal2[Calendar.YEAR]

        createDummyAddedLumpersList()

        recyclerViewLumpersImagesList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val lumperImages = ArrayList<ImageData>()

            for (i in 1..7) {
                lumperImages.add(ImageData(R.drawable.ic_basic_info_placeholder))
            }
            addItemDecoration(OverlapDecoration())
            val scheduleLumperImageAdapter = ScheduleLumperImagesAdapter(lumperImages, context)
            adapter = scheduleLumperImageAdapter
        }

        recycler_work_items.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            adapter = UnScheduledWorkItemAdapter(this@UnScheduleDetailActivity, sameDay, lumpersCountList)
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

            adapter.updateCount(lumpersCountList)
        }
    }

}

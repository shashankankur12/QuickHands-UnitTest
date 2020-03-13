package com.quickhandslogistics.modified.views.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleFragment
import com.quickhandslogistics.view.activities.BusinessOperationsActivity
import com.quickhandslogistics.view.adapter.LumperAttendanceAdapter
import com.quickhandslogistics.view.adapter.ScheduledWorkItemAdapter
import kotlinx.android.synthetic.main.activity_schedule_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_lumpers_attendance.*
import kotlinx.android.synthetic.main.container_schedule_detail.*
import java.util.*

class ScheduleDetailActivity : BaseActivity(), SpeedDialView.OnActionSelectedListener {

    private var sheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)
        setupToolbar(title = getString(R.string.schedule_detail))

        var time: Long = 0
        intent.extras?.let {
            if (it.containsKey(ScheduleFragment.ARG_SELECTED_TIME)) {
                time = it.getLong(ScheduleFragment.ARG_SELECTED_TIME)
            }
        }

        speedDialView.inflate(R.menu.menu_schedule)
        speedDialView.setOnActionSelectedListener(this)

        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = time
        val sameDay =
            cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR] && cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
        if (!sameDay) {
            speedDialView.removeActionItemById(R.id.actionMarkAttendance)
        }

        imageViewClose.setOnClickListener {
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
            bg.visibility = View.GONE
        }
        bg.setOnClickListener {}

        button_submit.setOnClickListener {
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
            bg.visibility = View.GONE
        }

        setData()

        recycler_work_items.layoutManager = LinearLayoutManager(this)
        recycler_work_items.adapter = ScheduledWorkItemAdapter(this, sameDay)

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        recycler_lumpers.layoutManager = LinearLayoutManager(this)
        recycler_lumpers.adapter = LumperAttendanceAdapter(this)
    }

    private fun setData() {
        text_location.text = Html.fromHtml("<b>Building : One97 Communications Private Limited</b>")
        text_lumper_count.text = Html.fromHtml("<b>Door : </b>03")
        text_date.text = Html.fromHtml("<b>Work Items : </b>05")
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

    override fun onActionSelected(actionItem: SpeedDialActionItem?): Boolean {
        actionItem?.let {
            when (actionItem.id) {
                R.id.actionBusinessOperations -> {
                    speedDialView.close(true)
                    startActivity(Intent(this, BusinessOperationsActivity::class.java))
                    overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
                    return true
                }
                R.id.actionMarkAttendance -> {
                    speedDialView.close(true)
                    if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                        sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED;
                        bg.visibility = View.VISIBLE
                    } else {
                        sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
                        bg.visibility = View.GONE
                    }
                    return true
                }
                else -> return false
            }
        }
        return false
    }
}

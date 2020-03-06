package com.quickhandslogistics.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.LumperAttendanceAdapter
import com.quickhandslogistics.view.adapter.ScheduledWorkItemAdapter
import kotlinx.android.synthetic.main.activity_schedule_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_lumpers_attendance.*
import kotlinx.android.synthetic.main.container_schedule_detail.*
import java.util.*


class ScheduleDetailActivity : AppCompatActivity() {

    private var sheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_detail)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val time = intent.getLongExtra("time", 0)
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = time
        val sameDay =
            cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR] && cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
        if (sameDay) {
            fab_mark_attendance.visibility = View.VISIBLE
            textView_mark_attendance.visibility = View.VISIBLE
        } else {
            fab_mark_attendance.visibility = View.GONE
            textView_mark_attendance.visibility = View.GONE
        }

        fab_mark_attendance.setOnClickListener {
            if (sheetBehavior?.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED;
                bg.visibility = View.VISIBLE
            } else {
                sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED;
                bg.visibility = View.GONE
            }
        }

        fab_business_operations.setOnClickListener {
            startActivity(Intent(this, BusinessOperationsActivity::class.java))
            overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
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
}

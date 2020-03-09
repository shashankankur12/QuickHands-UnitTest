package com.quickhandslogistics.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.UnScheduledWorkItemAdapter
import kotlinx.android.synthetic.main.activity_unschedule_detail.*
import kotlinx.android.synthetic.main.container_unschedule_detail.*
import java.util.*
import kotlin.collections.ArrayList

class UnScheduleDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unschedule_detail)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val time = intent.getLongExtra("time", 0)
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.timeInMillis = time
        val sameDay =
            cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR] && cal1[Calendar.YEAR] == cal2[Calendar.YEAR]

        fab_save.visibility = View.GONE

        fab_save.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }

        setData()

        createDummyAddedLumpersList()

        recycler_work_items.layoutManager = LinearLayoutManager(this)
        adapter = UnScheduledWorkItemAdapter(this, sameDay, lumpersCountList)
        recycler_work_items.adapter = adapter
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

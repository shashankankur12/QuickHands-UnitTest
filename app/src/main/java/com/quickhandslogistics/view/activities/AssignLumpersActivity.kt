package com.quickhandslogistics.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.AssignedLumperAdapter
import com.quickhandslogistics.view.adapter.LumperAdapter
import kotlinx.android.synthetic.main.activity_assign_lumpers.*
import kotlinx.android.synthetic.main.fragment_lumper.*
import kotlinx.android.synthetic.main.layout_header.*

class AssignLumpersActivity : AppCompatActivity() {

    val lumperList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_lumpers)

        text_title.text = getString(R.string.string_event_details)

        image_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_prev_slide_in, R.anim.anim_prev_slide_out)
        }

        setData()

        recycler_assigned_lumpers.layoutManager = LinearLayoutManager(this)
        recycler_assigned_lumpers.adapter = let { AssignedLumperAdapter(lumperList, it) }
    }

    private fun setData() {
        text_location.text = Html.fromHtml("<b>Location</b>  :  FlipKart, Gurgaon")
        text_lumper_count.text = Html.fromHtml("<b>Lumpers Required</b>  :  5")
        text_date.text = Html.fromHtml("<b>Date</b>  :  Wed Jan 15, 2020, 3:00 AM")
    }
}

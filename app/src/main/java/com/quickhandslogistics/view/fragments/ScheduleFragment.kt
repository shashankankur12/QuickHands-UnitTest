package com.quickhandslogistics.view.fragments

import android.content.Intent
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import kotlinx.android.synthetic.main.fragment_schedule.*
import com.alamkanak.weekview.WeekViewEvent
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekView
import com.quickhandslogistics.R
import com.quickhandslogistics.view.activities.AssignLumpersActivity
import java.time.Month
import java.util.*
import kotlin.collections.ArrayList

class ScheduleFragment : Fragment(), MonthLoader.MonthChangeListener, WeekView.EventClickListener {

    override fun onMonthChange(newYear: Int, newMonth: Int): MutableList<out WeekViewEvent> {
        var list : MutableList<WeekViewEvent> = ArrayList< WeekViewEvent>(10)

        if (newMonth == 1) {
            val startTime = Calendar.getInstance()
            startTime.set(Calendar.HOUR_OF_DAY, 3)
            startTime.set(Calendar.MINUTE, 0)
            startTime.set(Calendar.MONTH, newMonth - 1)
            startTime.set(Calendar.YEAR, newYear)

            val endTime = startTime.clone() as Calendar
            endTime.add(Calendar.HOUR, 2)
            endTime.set(Calendar.MONTH, newMonth - 1)

            val event = WeekViewEvent(1, "Location :  FlipKart, Gurgaon\nLumpers Required : 5", startTime, endTime)
            event.color = resources.getColor(R.color.colorBlue)

            list.add(event)
        }

        return list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        week_view.monthChangeListener = this
        week_view.setOnEventClickListener(this)
    }

    override fun onEventClick(event: WeekViewEvent?, eventRect: RectF?) {
        startActivity(Intent(this.requireActivity(), AssignLumpersActivity::class.java))
        this.activity?.overridePendingTransition(R.anim.anim_next_slide_in, R.anim.anim_next_slide_out)
    }
}

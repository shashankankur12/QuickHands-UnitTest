package com.quickhandslogistics.modified.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.schedule.ScheduleMainResponse
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleFragment
import java.util.*

class ScheduleMainPagerAdapter(
    private val fragmentActivity: FragmentActivity,
    time: Long
) :
    FragmentStatePagerAdapter(
        fragmentActivity.supportFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private var scheduleFragment =
        ScheduleFragment.newInstance(isScheduled = true, selectedTime = time)
    private var unScheduleFragment =
        ScheduleFragment.newInstance(isScheduled = false, selectedTime = time)

    private val tabTitles = arrayOf(
        R.string.scheduled,
        R.string.unscheduled
    )

    override fun getItem(position: Int): Fragment {
        return if (position == 0) scheduleFragment else unScheduleFragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentActivity.getString(tabTitles[position])
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    fun updateScheduleList(
        selectedDate: Date,
        scheduleMainResponse: ScheduleMainResponse
    ) {
        scheduleFragment.updateList(selectedDate, scheduleMainResponse.scheduledData)
        unScheduleFragment.updateList(selectedDate, scheduleMainResponse.unScheduledData)
    }
}
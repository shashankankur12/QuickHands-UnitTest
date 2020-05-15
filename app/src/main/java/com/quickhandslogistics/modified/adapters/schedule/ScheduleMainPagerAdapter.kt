package com.quickhandslogistics.modified.adapters.schedule

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment
import com.quickhandslogistics.modified.views.schedule.UnScheduleFragment

class ScheduleMainPagerAdapter(childFragmentManager: FragmentManager, private val resources: Resources) :
    FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var scheduleFragment = ScheduleFragment.newInstance()
    private var unScheduleFragment = UnScheduleFragment.newInstance()

    private val tabTitles = arrayOf(R.string.scheduled_work, R.string.unscheduled_work)

    override fun getItem(position: Int): Fragment {
        return if (position == 0) scheduleFragment else unScheduleFragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return resources.getString(tabTitles[position])
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun saveState(): Parcelable? {
        return null
    }

    fun fetchUnScheduledWorkItems() {
        unScheduleFragment.fetchUnScheduledWorkItems()
    }
}
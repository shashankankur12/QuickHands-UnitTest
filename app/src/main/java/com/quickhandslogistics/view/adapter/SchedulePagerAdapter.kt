package com.example.sampleproject.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.view.fragments.schedule.ScheduledFragment
import com.quickhandslogistics.view.fragments.schedule.UnScheduledFragment

private val TAB_TITLES = arrayOf(
    R.string.scheduled,
    R.string.unscheduled
)

class SchedulePagerAdapter(
    private val activity: FragmentActivity,
    private var time: Long
) :
    FragmentStatePagerAdapter(
        activity.supportFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private var fragment1: ScheduledFragment = ScheduledFragment(time)
    private var fragment2: UnScheduledFragment = UnScheduledFragment(time)

    override fun getItem(position: Int): Fragment {
        return if (position == 0) fragment1 else fragment2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return activity.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

    fun changeTabData(position: Int, time: Long) {
        if (position == 0) fragment1.updateData(time) else fragment2.updateData(time)
    }
}
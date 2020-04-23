package com.quickhandslogistics.modified.views.adapters.workSheet

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.fragments.workSheet.WorkSheetItemFragment

class WorkSheetPagerAdapter(
    childFragmentManager: FragmentManager, private val resources: Resources
) :
    FragmentStatePagerAdapter(
        childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private val tabTitles = arrayOf(R.string.ongoing, R.string.cancelled, R.string.completed)

    private var ongoingFragment =
        WorkSheetItemFragment.newInstance(resources.getString(tabTitles[0]))
    private var cancelledFragment =
        WorkSheetItemFragment.newInstance(resources.getString(tabTitles[1]))
    private var completedFragment =
        WorkSheetItemFragment.newInstance(resources.getString(tabTitles[2]))

    override fun getItem(position: Int): Fragment {
        return if (position == 0) ongoingFragment else if (position == 1) cancelledFragment else completedFragment
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

    /* fun fetchUnScheduledWorkItems() {
         unScheduleFragment.fetchUnScheduledWorkItems()
     }

     fun fetchScheduledWorkItems() {
         scheduleFragment.fetchScheduledWorkItems()
     }*/
}
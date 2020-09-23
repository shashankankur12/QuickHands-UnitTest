package com.quickhandslogistics.adapters.workSheet

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.views.workSheet.WorkSheetItemFragment
import kotlin.collections.ArrayList

class WorkSheetPagerAdapter(
    childFragmentManager: FragmentManager,
    private val resources: Resources,
    allWorkItemLists: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? =null
) :
    FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf(R.string.ongoing, R.string.completed , R.string.cancelled)
    private var onGoingWorkItemsCount = if (!allWorkItemLists?.first.isNullOrEmpty())allWorkItemLists?.first?.size else 0
    private var cancelledWorkItemsCount = if (!allWorkItemLists?.second.isNullOrEmpty())allWorkItemLists?.second?.size else 0
    private var completedWorkItemsCount = if (!allWorkItemLists?.third.isNullOrEmpty())allWorkItemLists?.third?.size else 0

    private var ongoingFragment = WorkSheetItemFragment.newInstance(resources.getString(tabTitles[0]), allWorkItemLists)
    private var cancelledFragment = WorkSheetItemFragment.newInstance(resources.getString(tabTitles[1]), allWorkItemLists)
    private var completedFragment = WorkSheetItemFragment.newInstance(resources.getString(tabTitles[2]), allWorkItemLists)

    override fun getItem(position: Int): Fragment {
        return if (position == 0) ongoingFragment else if (position == 1) completedFragment  else cancelledFragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val count = if (position == 0) onGoingWorkItemsCount else if (position == 1)completedWorkItemsCount else cancelledWorkItemsCount
        return "${resources.getString(tabTitles[position])} ($count)"
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun saveState(): Parcelable? {
        return null
    }

    fun updateWorkItemsList(onGoingWorkItems: ArrayList<WorkItemDetail>, cancelledWorkItems: ArrayList<WorkItemDetail>, completedWorkItems: ArrayList<WorkItemDetail>) {
        onGoingWorkItemsCount = onGoingWorkItems.size
        cancelledWorkItemsCount = cancelledWorkItems.size
        completedWorkItemsCount = completedWorkItems.size

        ongoingFragment.updateWorkItemsList(onGoingWorkItems)
        cancelledFragment.updateWorkItemsList(cancelledWorkItems)
        completedFragment.updateWorkItemsList(completedWorkItems)

        notifyDataSetChanged()
    }
}
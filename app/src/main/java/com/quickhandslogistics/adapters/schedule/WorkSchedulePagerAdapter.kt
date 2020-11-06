package com.quickhandslogistics.adapters.schedule

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.views.schedule.WorkScheduleItemFragment

class WorkSchedulePagerAdapter(
    childFragmentManager: FragmentManager,
    private val resources: Resources,
    selectedTime: Long?,
    allWorkItemLists: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? = null
) :
    FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf(R.string.ongoing, R.string.complete , R.string.cancel)
    private var onGoingWorkItemsCount = if (!allWorkItemLists?.first.isNullOrEmpty())allWorkItemLists?.first?.size else 0
    private var cancelledWorkItemsCount = if (!allWorkItemLists?.second.isNullOrEmpty())allWorkItemLists?.second?.size else 0
    private var completedWorkItemsCount = if (!allWorkItemLists?.third.isNullOrEmpty())allWorkItemLists?.third?.size else 0

    private var ongoingFragment = WorkScheduleItemFragment.newInstance(resources.getString(tabTitles[0]), allWorkItemLists, selectedTime)
    private var completedFragment = WorkScheduleItemFragment.newInstance(resources.getString(tabTitles[1]), allWorkItemLists, selectedTime)
    private var cancelledFragment = WorkScheduleItemFragment.newInstance(resources.getString(tabTitles[2]), allWorkItemLists, selectedTime)


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


    fun updateWorkItemsList(
        onGoingWorkItems: ArrayList<WorkItemDetail>,
        cancelledWorkItems: ArrayList<WorkItemDetail>,
        completedWorkItems: ArrayList<WorkItemDetail>,
        selectedTime: Long

    ) {
        onGoingWorkItemsCount = onGoingWorkItems.size
        cancelledWorkItemsCount = cancelledWorkItems.size
        completedWorkItemsCount = completedWorkItems.size


        ongoingFragment.updateWorkItemsList(onGoingWorkItems, selectedTime)
        cancelledFragment.updateWorkItemsList(cancelledWorkItems, selectedTime)
        completedFragment.updateWorkItemsList(completedWorkItems, selectedTime)

        notifyDataSetChanged()
    }
}
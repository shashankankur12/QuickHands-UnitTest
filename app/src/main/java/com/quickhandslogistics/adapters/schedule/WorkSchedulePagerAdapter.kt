package com.quickhandslogistics.adapters.schedule

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.controls.Quintuple
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.views.schedule.WorkScheduleItemFragment
import com.quickhandslogistics.views.workSheet.WorkSheetItemFragment

class WorkSchedulePagerAdapter(
    childFragmentManager: FragmentManager,
    private val resources: Resources,
    selectedTime: Long?,
    allWorkItemLists: Quintuple<ArrayList<WorkItemContainerDetails>, ArrayList<WorkItemContainerDetails>, ArrayList<WorkItemContainerDetails>, ArrayList<WorkItemContainerDetails>, ArrayList<WorkItemContainerDetails>>? = null
) :
    FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf(R.string.ongoing, R.string.complete , R.string.cancel, R.string.unfinished, R.string.not_open)
    private var onGoingWorkItemsCount = if (!allWorkItemLists?.first.isNullOrEmpty())allWorkItemLists?.first?.size else 0
    private var cancelledWorkItemsCount = if (!allWorkItemLists?.second.isNullOrEmpty())allWorkItemLists?.second?.size else 0
    private var completedWorkItemsCount = if (!allWorkItemLists?.third.isNullOrEmpty())allWorkItemLists?.third?.size else 0
    private var unfinishedWorkItemsCount = if (!allWorkItemLists?.fourth.isNullOrEmpty())allWorkItemLists?.fourth?.size else 0
    private var notOpenWorkItemsCount = if (!allWorkItemLists?.fifth.isNullOrEmpty())allWorkItemLists?.fifth?.size else 0

    private var ongoingFragment = WorkScheduleItemFragment.newInstance(resources.getString(tabTitles[0]), allWorkItemLists, selectedTime)
    private var completedFragment = WorkScheduleItemFragment.newInstance(resources.getString(tabTitles[1]), allWorkItemLists, selectedTime)
    private var cancelledFragment = WorkScheduleItemFragment.newInstance(resources.getString(tabTitles[2]), allWorkItemLists, selectedTime)
    private var unfinishedFragment = WorkScheduleItemFragment.newInstance(resources.getString(tabTitles[3]), allWorkItemLists, selectedTime)
    private var notOpenFragment = WorkScheduleItemFragment.newInstance(resources.getString(tabTitles[4]), allWorkItemLists, selectedTime)


    override fun getItem(position: Int): Fragment {
        return if (position == 0) ongoingFragment else if (position == 1) completedFragment  else if (position == 2) cancelledFragment else if (position == 3) unfinishedFragment else notOpenFragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val count = if (position == 0) onGoingWorkItemsCount else if (position == 1)completedWorkItemsCount else if (position == 2) cancelledWorkItemsCount else if (position == 3) unfinishedWorkItemsCount else notOpenWorkItemsCount
        return "${resources.getString(tabTitles[position])} ($count)"
    }

    override fun getCount(): Int {
        return tabTitles.size
    }

    override fun saveState(): Parcelable? {
        return null
    }


    fun updateWorkItemsList(
        onGoingWorkItems: ArrayList<WorkItemContainerDetails>,
        cancelledWorkItems: ArrayList<WorkItemContainerDetails>,
        completedWorkItems: ArrayList<WorkItemContainerDetails>,
        unfinishedWorkItems: ArrayList<WorkItemContainerDetails>,
        notOpenWorkItems: ArrayList<WorkItemContainerDetails>,
        selectedTime: Long

    ) {
        onGoingWorkItemsCount = onGoingWorkItems.size
        cancelledWorkItemsCount = cancelledWorkItems.size
        completedWorkItemsCount = completedWorkItems.size
        unfinishedWorkItemsCount = unfinishedWorkItems.size
        notOpenWorkItemsCount = notOpenWorkItems.size


        ongoingFragment.updateWorkItemsList(onGoingWorkItems, selectedTime)
        cancelledFragment.updateWorkItemsList(cancelledWorkItems, selectedTime)
        completedFragment.updateWorkItemsList(completedWorkItems, selectedTime)
        unfinishedFragment.updateWorkItemsList(unfinishedWorkItems, selectedTime)
        notOpenFragment.updateWorkItemsList(notOpenWorkItems, selectedTime)

        notifyDataSetChanged()
    }
}
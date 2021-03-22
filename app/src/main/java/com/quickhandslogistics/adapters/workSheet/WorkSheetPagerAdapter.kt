package com.quickhandslogistics.adapters.workSheet

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.controls.Quintuple
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.ContainerGroupNote
import com.quickhandslogistics.views.workSheet.WorkSheetItemFragment
import kotlin.collections.ArrayList

class WorkSheetPagerAdapter(
    childFragmentManager: FragmentManager,
    private val resources: Resources,
    allWorkItemLists: Quintuple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? = null,
    containerGroupNote: ContainerGroupNote?=null,
    unfinishedNotes: ContainerGroupNote?=null,
    notOpenNotes: ContainerGroupNote? =null
) :
    FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf(R.string.ongoing, R.string.complete , R.string.cancel, R.string.unfinished, R.string.not_open)
    private var onGoingWorkItemsCount = if (!allWorkItemLists?.first.isNullOrEmpty())allWorkItemLists?.first?.size else 0
    private var cancelledWorkItemsCount = if (!allWorkItemLists?.second.isNullOrEmpty())allWorkItemLists?.second?.size else 0
    private var completedWorkItemsCount = if (!allWorkItemLists?.third.isNullOrEmpty())allWorkItemLists?.third?.size else 0
    private var unfinishedWorkItemsCount = if (!allWorkItemLists?.third.isNullOrEmpty())allWorkItemLists?.fourth?.size else 0
    private var notOpenWorkItemsCount = if (!allWorkItemLists?.third.isNullOrEmpty())allWorkItemLists?.fifth?.size else 0

    private var ongoingFragment = WorkSheetItemFragment.newInstance(resources.getString(tabTitles[0]), allWorkItemLists)
    private var completedFragment = WorkSheetItemFragment.newInstance(resources.getString(tabTitles[1]), allWorkItemLists)
    private var cancelledFragment = WorkSheetItemFragment.newInstance(resources.getString(tabTitles[2]), allWorkItemLists,containerGroupNote)
    private var unfinishedFragment = WorkSheetItemFragment.newInstance(resources.getString(tabTitles[3]), allWorkItemLists, unfinishedNotes)
    private var notOpenFragment = WorkSheetItemFragment.newInstance(resources.getString(tabTitles[4]), allWorkItemLists, notOpenNotes)


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
        onGoingWorkItems: ArrayList<WorkItemDetail>,
        cancelledWorkItems: ArrayList<WorkItemDetail>,
        completedWorkItems: ArrayList<WorkItemDetail>,
        unfinishedWorkItems: ArrayList<WorkItemDetail>,
        cancelNote: ContainerGroupNote?,
        unfinishedNotes: ContainerGroupNote?,
        notOpenNotes: ContainerGroupNote?
    ) {
        onGoingWorkItemsCount = onGoingWorkItems.size
        cancelledWorkItemsCount = cancelledWorkItems.size
        completedWorkItemsCount = completedWorkItems.size
        unfinishedWorkItemsCount = unfinishedWorkItems.size
        notOpenWorkItemsCount = 0

        ongoingFragment.updateWorkItemsList(onGoingWorkItems)
        cancelledFragment.updateWorkItemsList(cancelledWorkItems, cancelNote)
        completedFragment.updateWorkItemsList(completedWorkItems)
        unfinishedFragment.updateWorkItemsList(unfinishedWorkItems,  unfinishedNotes)
        notOpenFragment.updateWorkItemsList(ArrayList(), notOpenNotes)

        notifyDataSetChanged()
    }
}
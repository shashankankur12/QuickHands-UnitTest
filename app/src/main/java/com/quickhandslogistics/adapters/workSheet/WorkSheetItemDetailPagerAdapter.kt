package com.quickhandslogistics.adapters.workSheet

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.data.schedule.ScheduleWorkItem
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.views.workSheet.WorkSheetItemDetailBOFragment
import com.quickhandslogistics.views.workSheet.WorkSheetItemDetailLumpersFragment
import com.quickhandslogistics.views.workSheet.WorkSheetItemDetailNotesFragment

class WorkSheetItemDetailPagerAdapter(
    fragmentManager: FragmentManager, private val resources: Resources, allWorkItem: WorkItemContainerDetails?=null,
    tempLumperIds: ArrayList<String>?= null, lumperTimeSchedule: ArrayList<LumpersTimeSchedule>?= null
) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf(R.string.parameters, R.string.lumpers, R.string.notes)

    private var buildingOperationsFragment = WorkSheetItemDetailBOFragment.newInstance(allWorkItem)
    private var lumpersFragment = WorkSheetItemDetailLumpersFragment.newInstance(allWorkItem, lumperTimeSchedule,tempLumperIds)
    private var notesFragment = WorkSheetItemDetailNotesFragment.newInstance(allWorkItem)

    override fun getItem(position: Int): Fragment {
        return if (position == 0) buildingOperationsFragment else if (position == 1) lumpersFragment else notesFragment
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

    fun showWorkItemData(workItemDetail: WorkItemContainerDetails, lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>?, tempLumperIds: ArrayList<String>) {
        buildingOperationsFragment.showBuildingOperationsData(workItemDetail)
        lumpersFragment.showLumpersData(workItemDetail, lumpersTimeSchedule, tempLumperIds)
        notesFragment.showNotesData(workItemDetail)
    }

    fun showEmptyData() {
        buildingOperationsFragment.showEmptyData()
        lumpersFragment.showEmptyData()
    }

    fun updateUploadedImage(imageUrl: String) {
        notesFragment.updateImageList(imageUrl)
    }
}
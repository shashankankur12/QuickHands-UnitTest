package com.quickhandslogistics.modified.adapters.customerSheet

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.views.customerSheet.CustomerSheetContainersFragment
import com.quickhandslogistics.modified.views.customerSheet.CustomerSheetCustomerFragment
import java.util.*

class CustomerSheetPagerAdapter(
    childFragmentManager: FragmentManager, private val resources: Resources
) :
    FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf(R.string.containers, R.string.string_customer)

    private var containersFragment = CustomerSheetContainersFragment.newInstance()
    private var customerFragment = CustomerSheetCustomerFragment.newInstance()

    override fun getItem(position: Int): Fragment {
        return if (position == 0) containersFragment else customerFragment
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

    fun updateCustomerSheetList(
        onGoingWorkItems: ArrayList<WorkItemDetail>,
        cancelledWorkItems: ArrayList<WorkItemDetail>,
        completedWorkItems: ArrayList<WorkItemDetail>,
        customerSheet: CustomerSheetListAPIResponse.CustomerSheetData?, selectedTime: Long
    ) {
        containersFragment.updateWorkItemsList(onGoingWorkItems, cancelledWorkItems, completedWorkItems)
        customerFragment.updateCustomerDetails(customerSheet, selectedTime, onGoingWorkItems.size)

    }
}
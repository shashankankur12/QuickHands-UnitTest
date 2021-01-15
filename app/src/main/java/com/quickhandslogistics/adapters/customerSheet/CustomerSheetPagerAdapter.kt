package com.quickhandslogistics.adapters.customerSheet

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.data.customerSheet.CustomerSheetData
import com.quickhandslogistics.data.customerSheet.LocalCustomerSheetData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.views.customerSheet.CustomerSheetContainersFragment
import com.quickhandslogistics.views.customerSheet.CustomerSheetCustomerFragment
import java.util.*

class CustomerSheetPagerAdapter(
    childFragmentManager: FragmentManager, private val resources: Resources,
    allWorkItemLists: Triple<ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>, ArrayList<WorkItemDetail>>? = null,
    customerSheetData: CustomerSheetData? = null, selectedTime: Long? = null, localCustomerSheetData: LocalCustomerSheetData?= null
) :
    FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val tabTitles = arrayOf(R.string.containers, R.string.customer_details)

    private var containersFragment = CustomerSheetContainersFragment.newInstance(allWorkItemLists)
    private var customerFragment = CustomerSheetCustomerFragment.newInstance(customerSheetData, selectedTime, allWorkItemLists, localCustomerSheetData)

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
        onGoingWorkItems: ArrayList<WorkItemDetail>, cancelledWorkItems: ArrayList<WorkItemDetail>,
        completedWorkItems: ArrayList<WorkItemDetail>, customerSheet: CustomerSheetData?, selectedTime: Long
    ) {
        containersFragment.updateWorkItemsList(onGoingWorkItems, cancelledWorkItems, completedWorkItems)
        customerFragment.updateCustomerDetails(customerSheet, selectedTime, onGoingWorkItems.size)

    }
}
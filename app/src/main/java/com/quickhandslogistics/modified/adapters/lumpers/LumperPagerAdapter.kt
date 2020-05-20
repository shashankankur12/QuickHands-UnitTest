package com.quickhandslogistics.modified.adapters.lumpers

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.lumpers.LumperJobDetailFragment
import com.quickhandslogistics.modified.views.lumpers.LumperPersonalDetailFragment
import com.quickhandslogistics.modified.views.lumpers.LumperWorkDetailFragment

class LumperPagerAdapter(fragmentManager: FragmentManager, private val resources: Resources, employeeData: EmployeeData) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var lumperPersonalDetailFragment = LumperPersonalDetailFragment.newInstance(employeeData)
    private var lumperWorkDetailFragment = LumperWorkDetailFragment.newInstance(employeeData)
    private var lumperJobDetailFragment = LumperJobDetailFragment.newInstance(employeeData)

    private val tabTitles = arrayOf(R.string.contact_info, R.string.shift_details, R.string.string_lumper_details)

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> lumperPersonalDetailFragment
            1 -> lumperWorkDetailFragment
            else -> lumperJobDetailFragment
        }
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
}
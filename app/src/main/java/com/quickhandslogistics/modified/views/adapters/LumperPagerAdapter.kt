package com.quickhandslogistics.modified.views.adapters

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.views.fragments.lumpers.LumperJobDetailFragment
import com.quickhandslogistics.modified.views.fragments.lumpers.LumperPersonalDetailFragment
import com.quickhandslogistics.modified.views.fragments.lumpers.LumperWorkDetailFragment

class LumperPagerAdapter(
    fragmentManager: FragmentManager,
    private val resources: Resources,
    employeeData: EmployeeData
) :
    FragmentStatePagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private var lumperPersonalDetailFragment =
        LumperPersonalDetailFragment.newInstance(employeeData)
    private var lumperWorkDetailFragment =
        LumperWorkDetailFragment.newInstance(employeeData)
    private var lumperJobDetailFragment =
        LumperJobDetailFragment.newInstance(employeeData)

    private val tabTitles = arrayOf(
        R.string.personal_detail,
        R.string.work_detail,
        R.string.job_detail
    )

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
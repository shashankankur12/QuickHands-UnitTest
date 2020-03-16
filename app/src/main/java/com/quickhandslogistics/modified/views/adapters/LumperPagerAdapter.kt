package com.quickhandslogistics.modified.views.adapters

import android.content.res.Resources
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.data.lumpers.LumperData
import com.quickhandslogistics.modified.views.fragments.lumpers.LumperJobDetailFragment
import com.quickhandslogistics.modified.views.fragments.lumpers.LumperPersonalDetailFragment
import com.quickhandslogistics.modified.views.fragments.lumpers.LumperWorkDetailFragment

class LumperPagerAdapter(
    fragmentManager: FragmentManager,
    private val resources: Resources,
    lumperData: LumperData
) :
    FragmentStatePagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    private var lumperPersonalDetailFragment =
        LumperPersonalDetailFragment.newInstance(lumperData)
    private var lumperWorkDetailFragment =
        LumperWorkDetailFragment.newInstance(lumperData)
    private var lumperJobDetailFragment =
        LumperJobDetailFragment.newInstance(lumperData)

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
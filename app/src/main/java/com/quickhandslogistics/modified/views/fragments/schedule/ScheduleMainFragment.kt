package com.quickhandslogistics.modified.views.fragments.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.ScheduleMainPagerAdapter
import kotlinx.android.synthetic.main.fragment_schedule_main.*

class ScheduleMainFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerSchedule.adapter = ScheduleMainPagerAdapter(childFragmentManager, resources)
        tabLayoutSchedule.setupWithViewPager(viewPagerSchedule)
    }
}

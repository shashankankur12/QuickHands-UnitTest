package com.quickhandslogistics.modified.views.fragments.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.ScheduleMainPagerAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.fragment_schedule_main.*

class ScheduleMainFragment : BaseFragment(),
    ScheduleMainContract.View.OnScheduleFragmentInteractionListener {

    private lateinit var adapter: ScheduleMainPagerAdapter
    private var progressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ScheduleMainPagerAdapter(childFragmentManager, resources)
        viewPagerSchedule.adapter = adapter
        tabLayoutSchedule.setupWithViewPager(viewPagerSchedule)
    }

    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(fragmentActivity!!).showProgressDialog(message)
    }

    override fun onScheduleAPICallFinished() {
        TODO("Not yet implemented")
    }

    override fun fetchUnsScheduledWorkItems() {
        adapter.fetchUnsScheduledWorkItems()
    }
}

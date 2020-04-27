package com.quickhandslogistics.modified.views.fragments.schedule

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.schedule.ScheduleMainPagerAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.fragment_schedule_main.*

class ScheduleMainFragment : BaseFragment(),
    ScheduleMainContract.View.OnScheduleFragmentInteractionListener {

    private lateinit var adapter: ScheduleMainPagerAdapter
    private var progressDialog: Dialog? = null

    companion object {
        const val ARG_ALLOW_UPDATE = "ARG_ALLOW_UPDATE"
        const val ARG_BUILDING_PARAMETERS = "ARG_BUILDING_PARAMETERS"
        const val ARG_IS_SCHEDULED_STATUS_CHANGED = "ARG_IS_SCHEDULED_STATUS_CHANGED"
        const val ARG_SELECTED_DATE_MILLISECONDS = "ARG_SELECTED_DATE_MILLISECONDS"
        const val ARG_SCHEDULE_IDENTITY = "ARG_SCHEDULE_IDENTITY"
        const val ARG_SCHEDULE_DETAIL = "ARG_SCHEDULE_DETAIL"
        const val ARG_SCHEDULE_FROM_DATE = "ARG_SCHEDULE_FROM_DATE"
        const val ARG_SCHEDULE_TIME_NOTES = "ARG_SCHEDULE_TIME_NOTES"
        const val ARG_WORK_ITEM_ID = "ARG_WORK_ITEM_ID"
        const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"
        const val ARG_WORK_ITEM_TYPE_DISPLAY_NAME = "ARG_WORK_ITEM_TYPE_DISPLAY_NAME"
        const val ARG_SCHEDULED_TIME_LIST = "ARG_SCHEDULED_TIME_LIST"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

    override fun fetchUnScheduledWorkItems() {
        adapter.fetchUnScheduledWorkItems()
    }

    override fun updateAllSchedules() {
        adapter.fetchScheduledWorkItems()
    }
}

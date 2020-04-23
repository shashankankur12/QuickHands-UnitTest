package com.quickhandslogistics.modified.views.fragments.workSheet

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.workSheet.WorkSheetPagerAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import kotlinx.android.synthetic.main.fragment_work_sheet.*

class WorkSheetFragment : BaseFragment(),
    ScheduleMainContract.View.OnScheduleFragmentInteractionListener {

    private lateinit var adapter: WorkSheetPagerAdapter
    private var progressDialog: Dialog? = null

    companion object {
        const val ARG_ALLOW_UPDATE = "ARG_ALLOW_UPDATE"
        const val ARG_BUILDING_PARAMETERS = "ARG_BUILDING_PARAMETERS"
        const val ARG_IS_SCHEDULED_STATUS_CHANGED = "ARG_IS_SCHEDULED_STATUS_CHANGED"
        const val ARG_SELECTED_DATE_MILLISECONDS = "ARG_SELECTED_DATE_MILLISECONDS"
        const val ARG_SCHEDULE_IDENTITY = "ARG_SCHEDULEN_IDENTITY"
        const val ARG_SCHEDULE_DETAIL = "ARG_SCHEDULE_DETAIL"
        const val ARG_SCHEDULE_FROM_DATE = "ARG_SCHEDULE_FROM_DATE"
        const val ARG_WORK_ITEM_ID = "ARG_WORK_ITEM_ID"
        const val ARG_WORK_ITEM_TYPE = "ARG_WORK_ITEM_TYPE"
        const val ARG_WORK_ITEM_TYPE_DISPLAY_NAME = "ARG_WORK_ITEM_TYPE_DISPLAY_NAME"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WorkSheetPagerAdapter(childFragmentManager, resources)
        viewPagerWorkSheet.adapter = adapter
        tabLayoutWorkSheet.setupWithViewPager(viewPagerWorkSheet)
    }

    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(fragmentActivity!!).showProgressDialog(message)
    }

    override fun fetchUnScheduledWorkItems() {
        //adapter.fetchUnScheduledWorkItems()
    }

    override fun updateAllSchedules() {
        //adapter.fetchScheduledWorkItems()
    }
}

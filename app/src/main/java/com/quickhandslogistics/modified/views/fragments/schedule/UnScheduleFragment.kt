package com.quickhandslogistics.modified.views.fragments.schedule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.presenters.schedule.UnSchedulePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.activities.schedule.UnScheduleDetailActivity
import com.quickhandslogistics.modified.views.adapters.schedule.UnScheduleAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_IS_SCHEDULED_STATUS_CHANGED
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_DETAIL
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_IDENTITY
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.fragment_unschedule.*

class UnScheduleFragment : BaseFragment(), UnScheduleContract.View.OnAdapterItemClickListener,
    UnScheduleContract.View, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var unSchedulePresenter: UnSchedulePresenter
    private lateinit var unScheduleAdapter: UnScheduleAdapter
    private var onScheduleFragmentInteractionListener: ScheduleMainContract.View.OnScheduleFragmentInteractionListener? =
        null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is ScheduleMainContract.View.OnScheduleFragmentInteractionListener) {
            onScheduleFragmentInteractionListener =
                parentFragment as ScheduleMainContract.View.OnScheduleFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unSchedulePresenter = UnSchedulePresenter(this, resources, sharedPref)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_unschedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewSchedule.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            unScheduleAdapter =
                UnScheduleAdapter(resources, this@UnScheduleFragment)
            adapter = unScheduleAdapter
        }

        swipeRefreshLayoutUnSchedule.setColorSchemeColors(
            ContextCompat.getColor(fragmentActivity!!, R.color.buttonRed)
        )
        swipeRefreshLayoutUnSchedule.setOnRefreshListener(this)
    }

    /*
    * Native Views Listeners
    */
    override fun onRefresh() {
        swipeRefreshLayoutUnSchedule.isRefreshing = false
        unSchedulePresenter.getUnScheduledWorkItems(showProgressDialog = true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            data?.also {
                val isScheduleStatusChanged =
                    data.getBooleanExtra(ARG_IS_SCHEDULED_STATUS_CHANGED, false)
                if (isScheduleStatusChanged) {
                    onScheduleFragmentInteractionListener?.updateAllSchedules()
                }
            } ?: run {
                unSchedulePresenter.getUnScheduledWorkItems(showProgressDialog = true)
            }
        }
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onUnScheduleItemClick(scheduleDetail: ScheduleDetail) {
        val bundle = Bundle()
//        bundle.putString(ARG_SCHEDULE_IDENTITY, scheduleDetail.scheduleIdentity)
        bundle.putParcelable(ARG_SCHEDULE_DETAIL, scheduleDetail)
        startIntent(
            UnScheduleDetailActivity::class.java, bundle = bundle,
            requestCode = AppConstant.REQUEST_CODE_CHANGED
        )
    }

    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(DisplayLumpersListActivity.ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }

    /*
    * Presenter Listeners
    */
    override fun showUnScheduleData(workItemsList: ArrayList<ScheduleDetail>) {
        unScheduleAdapter.updateList(workItemsList)

        textViewEmptyData.visibility = View.GONE
        recyclerViewSchedule.visibility = View.VISIBLE
    }

    override fun hideProgressDialog() {
        onScheduleFragmentInteractionListener?.hideProgressDialog()
    }

    override fun showProgressDialog(message: String) {
        onScheduleFragmentInteractionListener?.showProgressDialog(message)
    }

    override fun showEmptyData() {
        textViewEmptyData.visibility = View.VISIBLE
        recyclerViewSchedule.visibility = View.GONE
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewSchedule.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    fun fetchUnScheduledWorkItems() {
        unSchedulePresenter.getUnScheduledWorkItems(showProgressDialog = false)
    }

    companion object {

        @JvmStatic
        fun newInstance() = UnScheduleFragment()
    }
}

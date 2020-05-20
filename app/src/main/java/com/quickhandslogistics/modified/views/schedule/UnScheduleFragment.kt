package com.quickhandslogistics.modified.views.schedule

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.schedule.UnScheduleAdapter
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.presenters.schedule.UnSchedulePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.common.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_DETAIL
import com.quickhandslogistics.utils.SnackBarFactory
import kotlinx.android.synthetic.main.fragment_unschedule.*

class UnScheduleFragment : BaseFragment(), UnScheduleContract.View.OnAdapterItemClickListener, UnScheduleContract.View, SwipeRefreshLayout.OnRefreshListener {

    private var onFragmentInteractionListener: ScheduleMainContract.View.OnFragmentInteractionListener? = null

    private lateinit var unSchedulePresenter: UnSchedulePresenter
    private lateinit var unScheduleAdapter: UnScheduleAdapter

    companion object {
        @JvmStatic
        fun newInstance() = UnScheduleFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is ScheduleMainContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = parentFragment as ScheduleMainContract.View.OnFragmentInteractionListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unSchedulePresenter = UnSchedulePresenter(this, resources)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_unschedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewSchedule.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            unScheduleAdapter = UnScheduleAdapter(resources, this@UnScheduleFragment)
            adapter = unScheduleAdapter
        }

        swipeRefreshLayoutUnSchedule.setColorSchemeColors(ContextCompat.getColor(fragmentActivity!!, R.color.buttonRed))
        swipeRefreshLayoutUnSchedule.setOnRefreshListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unSchedulePresenter.onDestroy()
    }

    fun fetchUnScheduledWorkItems() {
        unSchedulePresenter.getUnScheduledWorkItems(showProgressDialog = false)
    }

    /** Native Views Listeners */
    override fun onRefresh() {
        swipeRefreshLayoutUnSchedule.isRefreshing = false
        unSchedulePresenter.getUnScheduledWorkItems(showProgressDialog = true)
    }

    /** Presenter Listeners */
    override fun showUnScheduleData(workItemsList: ArrayList<ScheduleDetail>) {
        unScheduleAdapter.updateList(workItemsList)

        textViewEmptyData.visibility = View.GONE
        recyclerViewSchedule.visibility = View.VISIBLE
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

    /** Adapter Listeners */
    override fun onUnScheduleItemClick(scheduleDetail: ScheduleDetail) {
        val bundle = Bundle()
        bundle.putParcelable(ARG_SCHEDULE_DETAIL, scheduleDetail)
        startIntent(UnScheduleDetailActivity::class.java, bundle = bundle)
    }

    override fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>) {
        val bundle = Bundle()
        bundle.putParcelableArrayList(DisplayLumpersListActivity.ARG_LUMPERS_LIST, lumpersList)
        startIntent(DisplayLumpersListActivity::class.java, bundle = bundle)
    }
}
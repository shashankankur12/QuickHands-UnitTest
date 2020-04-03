package com.quickhandslogistics.modified.views.fragments.schedule

import android.content.Context
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
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.presenters.schedule.UnSchedulePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.activities.schedule.UnScheduleDetailActivity
import com.quickhandslogistics.modified.views.adapters.schedule.UnScheduleAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULE_DETAIL
import kotlinx.android.synthetic.main.fragment_unschedule.*
import java.util.*

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
            ContextCompat.getColor(fragmentActivity!!, R.color.colorAccent)
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

    /*
    * Adapter Item Click Listeners
    */
    override fun onUnScheduleItemClick(scheduleDetail: ScheduleDetail) {
        val bundle = Bundle()
        bundle.putSerializable(ARG_SCHEDULE_DETAIL, scheduleDetail)
        startIntent(UnScheduleDetailActivity::class.java, bundle = bundle)
    }

    override fun onLumperImagesClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    /*
    * Presenter Listeners
    */
    override fun showUnScheduleData(workItemsList: ArrayList<ScheduleDetail>) {
        val lumpersCountList = ArrayList<Int>()
        for (i in 0..workItemsList.size) {
            lumpersCountList.add(i)
        }

        unScheduleAdapter.updateList(workItemsList, lumpersCountList)
    }

    override fun hideProgressDialog() {
        onScheduleFragmentInteractionListener?.hideProgressDialog()
    }

    override fun showProgressDialog(message: String) {
        onScheduleFragmentInteractionListener?.showProgressDialog(message)
    }

    fun fetchUnsScheduledWorkItems() {
        unSchedulePresenter.getUnScheduledWorkItems(showProgressDialog = false)
    }

    companion object {

        @JvmStatic
        fun newInstance() = UnScheduleFragment()
    }
}

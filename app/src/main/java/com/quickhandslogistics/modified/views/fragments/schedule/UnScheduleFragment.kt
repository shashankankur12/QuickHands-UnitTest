package com.quickhandslogistics.modified.views.fragments.schedule

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.schedule.UnSchedulePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.activities.DisplayLumpersListActivity
import com.quickhandslogistics.modified.views.activities.schedule.AddWorkItemLumpersActivity
import com.quickhandslogistics.modified.views.activities.schedule.UnScheduleDetailActivity
import com.quickhandslogistics.modified.views.activities.schedule.UnscheduledWorkItemDetailActivity
import com.quickhandslogistics.modified.views.adapters.schedule.UnScheduleAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import kotlinx.android.synthetic.main.fragment_unschedule.*
import java.util.*

class UnScheduleFragment : BaseFragment(), UnScheduleContract.View.OnAdapterItemClickListener,
    UnScheduleContract.View {

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
        unSchedulePresenter = UnSchedulePresenter(this, sharedPref)
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
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onUnScheduleItemClick() {
        startIntent(UnScheduleDetailActivity::class.java)
    }

    override fun onLumperImagesClick() {
        startIntent(DisplayLumpersListActivity::class.java)
    }

    /*
    * Presenter Listeners
    */
    override fun showUnScheduleData(
        selectedDate: Date,
        workItemsList: ArrayList<WorkItemDetail>
    ) {
        val lumpersCountList = ArrayList<Int>()
        for (i in 0..workItemsList.size) {
            lumpersCountList.add(i)
        }

        //unScheduleAdapter.updateList(workItemsList, lumpersCountList)
    }

    override fun hideProgressDialog() {
        onScheduleFragmentInteractionListener?.hideProgressDialog()
    }

    fun fetchUnsScheduledWorkItems() {
        unSchedulePresenter.getUnScheduledWorkItems(Date())
    }

    companion object {

        @JvmStatic
        fun newInstance() = UnScheduleFragment()
    }
}

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
import com.quickhandslogistics.modified.views.activities.schedule.UnscheduledWorkItemDetailActivity
import com.quickhandslogistics.modified.views.adapters.UnScheduledWorkItemAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import kotlinx.android.synthetic.main.fragment_unschedule.*
import java.util.*

class UnScheduleFragment : BaseFragment(), UnScheduleContract.View.OnAdapterItemClickListener,
    UnScheduleContract.View {

    private lateinit var unSchedulePresenter: UnSchedulePresenter
    private lateinit var unScheduledWorkItemAdapter: UnScheduledWorkItemAdapter
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
            unScheduledWorkItemAdapter =
                UnScheduledWorkItemAdapter(resources, this@UnScheduleFragment)
            adapter = unScheduledWorkItemAdapter
        }
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onAddLumperItemClick() {
        val bundle = Bundle()
        bundle.putBoolean(AddWorkItemLumpersActivity.ARG_IS_ADD_LUMPER, true)
        startIntent(AddWorkItemLumpersActivity::class.java, bundle = bundle)
/*        val intent = Intent(activity, AddWorkItemLumpersActivity::class.java)
        intent.putExtra("position", position)
        activity.startActivityForResult(intent, 101)
        activity.overridePendingTransition(
            R.anim.anim_next_slide_in,
            R.anim.anim_next_slide_out
        )*/
    }

    override fun onWorkItemClick() {
        startIntent(UnscheduledWorkItemDetailActivity::class.java)
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

        unScheduledWorkItemAdapter.updateList(workItemsList, lumpersCountList)
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

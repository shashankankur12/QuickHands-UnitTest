package com.quickhandslogistics.modified.views.fragments.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleData
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.activities.schedule.ScheduleDetailActivity
import com.quickhandslogistics.modified.views.adapters.ScheduleAdapter
import com.quickhandslogistics.modified.views.adapters.ScheduleLumperImagesAdapter
import com.quickhandslogistics.modified.views.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.views.activities.schedule.UnScheduleDetailActivity
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.util.*


class ScheduleFragment : BaseFragment(), ScheduleContract.View.OnAdapterItemClickListener {

    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var scheduleLumperImageAdapter: ScheduleLumperImagesAdapter

    private var isScheduled: Boolean = true
    private var selectedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isScheduled = it.getBoolean(ARG_IS_SCHEDULED)
            selectedTime = it.getLong(ARG_SELECTED_TIME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewSchedule.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            scheduleAdapter = ScheduleAdapter(this@ScheduleFragment)
            adapter = scheduleAdapter
        }
    }

    fun updateList(
        selectedDate: Date,
        scheduledData: ArrayList<ScheduleData>
    ) {
        selectedTime = selectedDate.time
        scheduleAdapter.updateList(scheduledData)
    }

    companion object {
        private const val ARG_IS_SCHEDULED = "ARG_IS_SCHEDULED"
        const val ARG_SELECTED_TIME = "ARG_SELECTED_TIME"

        @JvmStatic
        fun newInstance(isScheduled: Boolean, selectedTime: Long) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_SCHEDULED, isScheduled)
                    putLong(ARG_SELECTED_TIME, selectedTime)
                }
            }
    }

    override fun onItemClick() {
        val bundle = Bundle()
        bundle.putLong(ARG_SELECTED_TIME, selectedTime)
        if (isScheduled) {
            startIntent(ScheduleDetailActivity::class.java, bundle = bundle)
        } else {
            startIntent(UnScheduleDetailActivity::class.java, bundle = bundle)
        }
    }
}

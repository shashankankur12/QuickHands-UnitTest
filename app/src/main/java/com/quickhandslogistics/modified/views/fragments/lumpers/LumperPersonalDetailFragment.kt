package com.quickhandslogistics.modified.views.fragments.lumpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseFragment

class LumperPersonalDetailFragment : BaseFragment() {

//    private var isScheduled: Boolean = true
//    private var selectedTime: Long = 0

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            isScheduled = it.getBoolean(ARG_IS_SCHEDULED)
//            selectedTime = it.getLong(ARG_SELECTED_TIME)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lumper_personal_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

//    fun updateList(
//        selectedDate: Date,
//        scheduledData: ArrayList<ScheduleData>
//    ) {
//        selectedTime = selectedDate.time
//        scheduleAdapter.updateList(scheduledData)
//    }

    companion object {
        private const val ARG_IS_SCHEDULED = "ARG_IS_SCHEDULED"
        const val ARG_SELECTED_TIME = "ARG_SELECTED_TIME"

        @JvmStatic
        fun newInstance(/*isScheduled: Boolean, selectedTime: Long*/) =
            LumperPersonalDetailFragment()/*.apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_SCHEDULED, isScheduled)
                    putLong(ARG_SELECTED_TIME, selectedTime)
                }
            }*/
    }
}

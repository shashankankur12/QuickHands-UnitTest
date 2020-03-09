package com.quickhandslogistics.view.fragments.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.quickhandslogistics.R
import com.quickhandslogistics.view.adapter.EventsSchduledAdapter
import kotlinx.android.synthetic.main.fragment_scheduled.*

class ScheduledFragment(private var time: Long) : Fragment() {

    private lateinit var eventsSchduledAdapter: EventsSchduledAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scheduled, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mLinearLayoutManager = LinearLayoutManager(activity)
        recycler_events.layoutManager = mLinearLayoutManager

        eventsSchduledAdapter =
            EventsSchduledAdapter(
                activity,
                time
            )
        recycler_events.adapter = eventsSchduledAdapter
        recycler_events.scheduleLayoutAnimation()
    }

    fun updateData(time: Long) {
        eventsSchduledAdapter.updateData(time)
    }
}

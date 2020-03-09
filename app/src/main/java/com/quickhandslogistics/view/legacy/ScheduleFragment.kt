package com.quickhandslogistics.view.legacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.model.DatesModel
import com.quickhandslogistics.model.ScheduledEvents
import com.quickhandslogistics.view.adapter.DatesAdapter
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList


class ScheduleFragment : Fragment() {

    private lateinit var datesAdapter: DatesAdapter
    public lateinit var eventsList: ArrayList<ScheduledEvents>
    private var numberOfDays: Int = 0
    lateinit var datesList: java.util.ArrayList<DatesModel>

    var year: Int = 0
    var month: Int = 0
    var day: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        eventsList = ArrayList()
        numberOfDays = getNumberOfDays()
        prepareDatesList(numberOfDays, eventsList)

        getCurrentMonthAndYear()
        setUpEventsRecyclerView(recycler_events, eventsList)
    }

    private fun setUpDatesRecyclerView(recyclerView: RecyclerView) {
        val mLinearLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setLayoutManager(mLinearLayoutManager)

        datesAdapter =
            DatesAdapter(this, datesList, this.requireActivity(), month, year, eventsList)
        recyclerView.adapter = datesAdapter
        recyclerView.scheduleLayoutAnimation()
    }

    fun setUpEventsRecyclerView(
        recyclerView: RecyclerView,
        eventsList: java.util.ArrayList<ScheduledEvents>
    ) {

       /* val mLinearLayoutManager = LinearLayoutManager(activity)
        mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setLayoutManager(mLinearLayoutManager)

        val eventsAdapter = EventsAdapter(activity, eventsList)
        recyclerView.setAdapter(eventsAdapter)
        recyclerView.scheduleLayoutAnimation()*/
    }


    private fun getCurrentMonthAndYear() {
        val mycal = Calendar.getInstance()

        day = mycal.get(Calendar.DAY_OF_MONTH)
        year = mycal.get(Calendar.YEAR)
        month = mycal.get(Calendar.MONTH)

        text_month_year.text = day.toString() + " " + getMonth(month) + " "
    }

    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month]
    }

    private fun getNumberOfDays(): Int {
        val mycal = Calendar.getInstance()
        mycal.set(Calendar.YEAR, year)
        mycal.set(Calendar.MONTH, month)
        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun prepareDatesList(num: Int, events: java.util.ArrayList<ScheduledEvents>?) {

        datesList = ArrayList()
        datesList.clear()
        for (i in 1..num) {

            val date = DatesModel()
            date.date = i
            date.selected = false
            date.eventAvailable = false

            if (i == 2 || i == 7) date.eventAvailable = true

            datesList.add(date)
        }

        setUpDatesRecyclerView(recycler_monthView)
    }
}

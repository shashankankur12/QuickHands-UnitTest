package com.quickhandslogistics.modified.views.fragments.schedule

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleMainContract
import com.quickhandslogistics.modified.data.schedule.ScheduleMainResponse
import com.quickhandslogistics.modified.presenters.schedule.ScheduleMainPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.adapters.ScheduleMainPagerAdapter
import kotlinx.android.synthetic.main.fragment_schedule_main.*
import java.util.*

class ScheduleMainFragment : BaseFragment(), ScheduleMainContract.View, View.OnClickListener {

    private var maxCalendarDate: Long = 0
    private var minCalendarDate: Long = 0

    private lateinit var scheduleMainPresenter: ScheduleMainPresenter
    private lateinit var sectionsMainPagerAdapter: ScheduleMainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleMainPresenter = ScheduleMainPresenter(this)

        // Setup DatePicker Dates
        val calendar = Calendar.getInstance()
        maxCalendarDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, -1)
        minCalendarDate = calendar.timeInMillis
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutDate.setOnClickListener(this)

        val currentDate = Date()

        sectionsMainPagerAdapter =
            ScheduleMainPagerAdapter(
                childFragmentManager,
                resources,
                currentDate.time
            )
        viewPagerSchedule.adapter = sectionsMainPagerAdapter
        tabLayoutSchedule.setupWithViewPager(viewPagerSchedule)

        Handler().postDelayed({
            scheduleMainPresenter.showSchedulesByDate(currentDate)
        }, 500)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            layoutDate.id -> {
                val lastTimeSelected = layoutDate.getTag(R.id.timeInMills) as Long
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = lastTimeSelected

                val picker = DatePickerDialog(
                    fragmentActivity!!,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        scheduleMainPresenter.showSchedulesByDate(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                picker.datePicker.maxDate = maxCalendarDate
                picker.datePicker.minDate = minCalendarDate
                picker.show()
            }
        }
    }

    override fun showDateString(dateString: String, timeInMills: Long) {
        textViewDate.text = dateString
        layoutDate.setTag(R.id.timeInMills, timeInMills)
    }

    override fun showScheduleData(
        selectedDate: Date,
        scheduleMainResponse: ScheduleMainResponse
    ) {
        tabLayoutSchedule.getTabAt(0)?.select()
        sectionsMainPagerAdapter.updateScheduleList(selectedDate, scheduleMainResponse)
    }
}

package com.quickhandslogistics.view.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sampleproject.ui.main.SchedulePagerAdapter
import com.quickhandslogistics.R
import com.quickhandslogistics.utils.DateUtils
import kotlinx.android.synthetic.main.fragment_schedule_main.*
import java.util.*

class ScheduleMainFragment : Fragment(), View.OnClickListener {

    private lateinit var sectionsPagerAdapter: SchedulePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        text_date.text = DateUtils.getDateString(DateUtils.PATTERN_NORMAL, Date())
        layoutDate.tag = Date().time
        layoutDate.setOnClickListener(this)

        sectionsPagerAdapter = SchedulePagerAdapter(activity!!, layoutDate.tag!! as Long)
        view_pager.adapter = sectionsPagerAdapter
        tabLayout.setupWithViewPager(view_pager)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            layoutDate.id -> {
                val mainCalendar = Calendar.getInstance()
                val calendar = Calendar.getInstance()

                val maxDate = calendar.timeInMillis
                calendar.add(Calendar.MONTH, -1)
                val minDate = calendar.timeInMillis

                val lastTimeSelected = layoutDate.tag as Long?
                lastTimeSelected?.let {
                    mainCalendar.timeInMillis = lastTimeSelected
                }

                val picker = DatePickerDialog(
                    context!!,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        mainCalendar.set(year, month, dayOfMonth)
                        layoutDate.tag = mainCalendar.timeInMillis
                        text_date.text =
                            DateUtils.getDateString(DateUtils.PATTERN_NORMAL, mainCalendar.time)

                        tabLayout.getTabAt(0)?.select()
                        sectionsPagerAdapter.changeTabData(0, mainCalendar.timeInMillis)
                        sectionsPagerAdapter.changeTabData(1, mainCalendar.timeInMillis)
                    },
                    mainCalendar.get(Calendar.YEAR),
                    mainCalendar.get(Calendar.MONTH),
                    mainCalendar.get(Calendar.DAY_OF_MONTH)

                )
                picker.datePicker.minDate = minDate
                picker.datePicker.maxDate = maxDate
                picker.show()
            }
        }
    }
}

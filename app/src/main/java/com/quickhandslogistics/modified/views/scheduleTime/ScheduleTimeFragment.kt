package com.quickhandslogistics.modified.views.scheduleTime

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.scheduleTime.ScheduleTimeAdapter
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.modified.presenters.scheduleTime.ScheduleTimePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_LUMPERS_COUNT
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_NOTES
import com.quickhandslogistics.modified.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.fragment_schedule_time.*
import java.util.*

class ScheduleTimeFragment : BaseFragment(), TextWatcher, View.OnClickListener, ScheduleTimeContract.View, CalendarUtils.CalendarSelectionListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private var selectedTime: Long = 0
    private var currentDatePosition: Int = 0
    private var isFutureDate: Boolean = false

    private lateinit var availableDates: List<Date>
    private var scheduleTimeDetailList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var scheduleTimeNotes: String? = null

    private lateinit var scheduleTimeAdapter: ScheduleTimeAdapter
    private lateinit var scheduleTimePresenter: ScheduleTimePresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleTimePresenter = ScheduleTimePresenter(this, resources)

        // Setup DatePicker Dates
        selectedTime = Date().time
        val pair = CalendarUtils.getPastFutureCalendarDates()
        availableDates = pair.first
        currentDatePosition = pair.second
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewScheduleTime.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            scheduleTimeAdapter = ScheduleTimeAdapter()
            adapter = scheduleTimeAdapter
        }

        scheduleTimeAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        invalidateScheduleButton()

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonScheduleLumpers.setOnClickListener(this)
        buttonRequestHelp.setOnClickListener(this)

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarScheduleTime, availableDates, this)
        singleRowCalendarScheduleTime.select(if (currentDatePosition != 0) currentDatePosition else availableDates.size - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        scheduleTimePresenter.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            if (singleRowCalendarScheduleTime.getSelectedDates().isNotEmpty()) {
                scheduleTimePresenter.getSchedulesTimeByDate(singleRowCalendarScheduleTime.getSelectedDates()[0])
            }
        }
    }

    private fun invalidateScheduleButton() {
        buttonScheduleLumpers.visibility = if (isFutureDate) View.VISIBLE else View.GONE
        buttonRequestHelp.visibility = if (isFutureDate) View.VISIBLE else View.GONE
    }

    private fun invalidateEmptyView() {
        if (scheduleTimeAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (scheduleTimeAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.string_no_record_found)
            } else {
                textViewEmptyData.text = if (isFutureDate) {
                    getString(R.string.empty_schedule_time_list)
                } else {
                    getString(R.string.empty_schedule_time_list_past)
                }
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = if (isFutureDate) {
                getString(R.string.empty_schedule_time_list)
            } else {
                getString(R.string.empty_schedule_time_list_past)
            }
        }
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(fragmentActivity!!)
                }
                buttonScheduleLumpers.id -> {
                    val bundle = Bundle()
                    bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
                    bundle.putParcelableArrayList(ARG_SCHEDULED_TIME_LIST, scheduleTimeDetailList)
                    bundle.putString(ARG_SCHEDULED_TIME_NOTES, scheduleTimeNotes)
                    startIntent(EditScheduleTimeActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                }
                buttonRequestHelp.id -> {
                    val bundle = Bundle()
                    bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
                    bundle.putInt(ARG_SCHEDULED_LUMPERS_COUNT, scheduleTimeDetailList.size)
                    startIntent(RequestLumpersActivity::class.java, bundle = bundle)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            scheduleTimeAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showDateString(dateString: String) {
        textViewDate.text = dateString
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewScheduleTime.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showScheduleTimeData(selectedDate: Date, scheduleTimeDetailList: ArrayList<ScheduleTimeDetail>) {
        this.scheduleTimeDetailList = scheduleTimeDetailList
        selectedTime = selectedDate.time
        isFutureDate = DateUtils.isFutureDate(selectedDate.time)
        invalidateScheduleButton()

        scheduleTimeAdapter.updateLumpersData(scheduleTimeDetailList)
    }

    override fun showNotesData(notes: String?) {
        scheduleTimeNotes = notes
        if (!scheduleTimeNotes.isNullOrEmpty()) {
            onFragmentInteractionListener?.invalidateScheduleTimeNotes(scheduleTimeNotes!!)
        } else {
            onFragmentInteractionListener?.invalidateScheduleTimeNotes("")
        }
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(date: Date) {
        scheduleTimePresenter.getSchedulesTimeByDate(date)
    }
}

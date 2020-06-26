package com.quickhandslogistics.views.scheduleTime

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
import com.quickhandslogistics.adapters.scheduleTime.ScheduleTimeAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.presenters.scheduleTime.ScheduleTimePresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.DashBoardActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_LUMPERS_COUNT
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_NOTES
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.fragment_schedule_time.*
import kotlinx.android.synthetic.main.fragment_schedule_time.editTextSearch
import kotlinx.android.synthetic.main.fragment_schedule_time.imageViewCancel
import kotlinx.android.synthetic.main.fragment_schedule_time.mainConstraintLayout
import kotlinx.android.synthetic.main.fragment_schedule_time.textViewDate
import kotlinx.android.synthetic.main.fragment_schedule_time.textViewEmptyData
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimeFragment : BaseFragment(), TextWatcher, View.OnClickListener, ScheduleTimeContract.View, CalendarUtils.CalendarSelectionListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private var scheduleTimeSelectedDate: String? = null

    private var selectedTime: Long = 0
    private var selectedDatePosition: Int = 0
    private var datePosition: Int = 0
    private var isPastDate: Boolean = false

    private lateinit var availableDates: List<Date>
    private var scheduleTimeDetailList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var scheduleTimeNotes: String? = null
    private var dateString: String? = null
    private var isSavedState: Boolean = false
    private lateinit var selectedDate: Date
    private lateinit var tempLumperIds: ArrayList<String>

    private lateinit var scheduleTimeAdapter: ScheduleTimeAdapter
    private lateinit var scheduleTimePresenter: ScheduleTimePresenter

    companion object {
        const val SCHEDULE_TIME_DETAIL = "SCHEDULE_TIME_DETAIL"
        const val DATE = "DATE"
        const val TEMP_LUMPER = "TEMP_LUMPER"
        const val NOTE = "NOTE"
        const val DATE_SELECTED = "DATE_SELECTED"
        const val SELECTED_DATE = "SELECTED_DATE"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleTimePresenter = ScheduleTimePresenter(this, resources)

        arguments?.let { bundle ->
            scheduleTimeSelectedDate = bundle.getString(DashBoardActivity.ARG_SCHEDULE_TIME_SELECTED_DATE)
        }

        // Setup Calendar Dates
        selectedTime = Date().time
        val pair = CalendarUtils.getPastFutureCalendarDates()
        availableDates = pair.first
        val currentDatePosition = pair.second

        selectedDatePosition = CalendarUtils.getSelectedDatePosition(availableDates, scheduleTimeSelectedDate)
        // Check if we didn't get the date position
        if (selectedDatePosition == -1) {

            // Check if we get the current date position
            selectedDatePosition = if (currentDatePosition == 0) {
                availableDates.size - 1
            } else {
                currentDatePosition
            }
        }

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
        buttonRequestLumpers.setOnClickListener(this)

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarScheduleTime, availableDates, this)

        savedInstanceState?.also {
            isSavedState=true
            if (savedInstanceState.containsKey(TEMP_LUMPER)) {
                tempLumperIds = savedInstanceState.getStringArrayList(TEMP_LUMPER)!!

            }
            if (savedInstanceState.containsKey(SELECTED_DATE)) {
                datePosition = savedInstanceState.getInt(SELECTED_DATE)!!
                singleRowCalendarScheduleTime.select(datePosition)
            }
            if(savedInstanceState.containsKey(DATE)) {
                selectedDate = savedInstanceState.getSerializable(DATE) as Date
            }
            if (savedInstanceState.containsKey(SCHEDULE_TIME_DETAIL)) {
                scheduleTimeDetailList = savedInstanceState.getParcelableArrayList(SCHEDULE_TIME_DETAIL)!!
                showScheduleTimeData(selectedDate,scheduleTimeDetailList,tempLumperIds)
            }
            if (savedInstanceState.containsKey(DATE_SELECTED)) {
                dateString = savedInstanceState.getString(DATE_SELECTED)!!
                showDateString(dateString!!)
            }
            if (savedInstanceState.containsKey(NOTE)) {
                scheduleTimeNotes = savedInstanceState.getString(NOTE)!!
                showNotesData(scheduleTimeNotes!!)
            }
        } ?: run {
          isSavedState=false
            singleRowCalendarScheduleTime.select(selectedDatePosition)
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        if(scheduleTimeDetailList != null)
        outState.putParcelableArrayList(SCHEDULE_TIME_DETAIL, scheduleTimeDetailList)
        if(selectedDate != null)
        outState.putSerializable(DATE, selectedDate)
        if(tempLumperIds != null)
        outState.putStringArrayList(TEMP_LUMPER,tempLumperIds)
        if(scheduleTimeNotes != null)
        outState.putString(NOTE,scheduleTimeNotes)
        if(dateString != null)
        outState.putString(DATE_SELECTED,dateString)
        if(datePosition != null)
        outState.putInt(SELECTED_DATE,datePosition)
        super.onSaveInstanceState(outState)
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
        buttonScheduleLumpers.visibility = if (isPastDate) View.GONE else View.VISIBLE
    }

    private fun invalidateEmptyView() {
        if (scheduleTimeAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (scheduleTimeAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = if (isPastDate) {
                    getString(R.string.empty_schedule_time_list_past_info_message)
                } else {
                    getString(R.string.empty_schedule_time_list_info_message)
                }
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = if (isPastDate) {
                getString(R.string.empty_schedule_time_list_past_info_message)
            } else {
                getString(R.string.empty_schedule_time_list_info_message)
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
                buttonRequestLumpers.id -> {
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
        this.dateString=dateString
        textViewDate.text = dateString
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewScheduleTime.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showScheduleTimeData(mSelectedDate: Date, mScheduleTimeDetailList: ArrayList<ScheduleTimeDetail>, mTempLumperIds: ArrayList<String>) {
        this.scheduleTimeDetailList = mScheduleTimeDetailList
        this.selectedDate=mSelectedDate
        this.tempLumperIds=mTempLumperIds
        selectedTime = selectedDate.time
        isPastDate = !DateUtils.isFutureDate(selectedTime) && !DateUtils.isCurrentDate(selectedTime)
        invalidateScheduleButton()

        scheduleTimeAdapter.updateLumpersData(scheduleTimeDetailList, tempLumperIds)

        if (!scheduleTimeSelectedDate.isNullOrEmpty()) {
            scheduleTimeSelectedDate = ""
            buttonRequestLumpers.performClick()
        }
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
    override fun onSelectCalendarDate(
        date: Date,
        selected: Boolean,
        position: Int
    ) {
        if (!isSavedState)
            scheduleTimePresenter.getSchedulesTimeByDate(date)
        isSavedState=false
        datePosition=position
    }
}

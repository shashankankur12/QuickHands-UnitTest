package com.quickhandslogistics.modified.views.scheduleTime

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeNotes
import com.quickhandslogistics.modified.presenters.scheduleTime.ScheduleTimePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.adapters.scheduleTime.ScheduleTimeAdapter
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULED_TIME_NOTES
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.item_calendar_view.view.*
import kotlinx.android.synthetic.main.fragment_schedule_time.*
import java.util.*

class ScheduleTimeFragment : BaseFragment(), TextWatcher, View.OnClickListener,
    ScheduleTimeContract.View {

    private var listener: DashBoardContract.View.OnFragmentInteractionListener? = null
    private lateinit var scheduleTimeAdapter: ScheduleTimeAdapter
    private lateinit var scheduleTimePresenter: ScheduleTimePresenter

    private var selectedTime: Long = 0

    private var currentDatePosition: Int = 0
    private var isFutureDate: Boolean = false

    private lateinit var availableDates: List<Date>
    private var scheduleTimeDetailList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var scheduleTimeNotes: ScheduleTimeNotes? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleTimePresenter = ScheduleTimePresenter(this, resources)

        // Setup DatePicker Dates
        selectedTime = Date().time
        availableDates = getAvailableDates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewScheduleTime.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            scheduleTimeAdapter = ScheduleTimeAdapter()
            adapter = scheduleTimeAdapter
        }

        scheduleTimeAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility =
                    if (scheduleTimeAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        invalidateScheduleButton()

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonScheduleLumpers.setOnClickListener(this)

        initializeCalendar()

        singleRowCalendarScheduleTime.select(currentDatePosition)
    }

    private fun invalidateScheduleButton() {
        buttonScheduleLumpers.visibility = if (isFutureDate) View.VISIBLE else View.GONE
    }

    private fun initializeCalendar() {
        val myCalendarViewManager = object : CalendarViewManager {
            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date, position: Int, isSelected: Boolean
            ) {
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

                if (isSelected) {
                    holder.itemView.tv_date_calendar_item.setTextColor(Color.WHITE)
                    holder.itemView.tv_date_calendar_item.setBackgroundResource(R.drawable.selected_calendar_item_background)
                } else {
                    holder.itemView.tv_date_calendar_item.setTextColor(
                        ContextCompat.getColor(fragmentActivity!!, R.color.detailHeader)
                    )
                    holder.itemView.tv_date_calendar_item.setBackgroundColor(Color.TRANSPARENT)
                }
            }

            override fun setCalendarViewResourceId(
                position: Int, date: Date, isSelected: Boolean
            ): Int {
                return R.layout.item_calendar_view
            }
        }

        val myCalendarChangesObserver = object : CalendarChangesObserver {
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                if (isSelected) {
                    scheduleTimePresenter.getSchedulesTimeByDate(date)
                }
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                return true
            }
        }

        singleRowCalendarScheduleTime.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(availableDates)
            init()
            scrollToPosition(availableDates.size - 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            if (singleRowCalendarScheduleTime.getSelectedDates().isNotEmpty()) {
                scheduleTimePresenter.getSchedulesTimeByDate(singleRowCalendarScheduleTime.getSelectedDates()[0])
            }
        }
    }

    /*
    * Presenter Listeners
    */
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
        isFutureDate = com.quickhandslogistics.utils.DateUtils.isFutureDate(selectedDate.time)
        invalidateScheduleButton()

        scheduleTimeAdapter.updateLumpersData(scheduleTimeDetailList)
        if (scheduleTimeDetailList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewScheduleTime.visibility = View.VISIBLE
        } else {
            recyclerViewScheduleTime.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun showNotesData(notes: ScheduleTimeNotes?) {
        scheduleTimeNotes = notes
        scheduleTimeNotes?.also { scheduleTimeNotes ->
            listener?.invalidateScheduleTimeNotes(if (!scheduleTimeNotes.notesForLead.isNullOrEmpty()) scheduleTimeNotes.notesForLead!! else "")
        } ?: run {
            listener?.invalidateScheduleTimeNotes("")
        }
    }

    /*
    * Native Views Listeners
    */
    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            scheduleTimeAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(fragmentActivity!!)
                }
                buttonScheduleLumpers.id -> {
                    val bundle = Bundle()
                    bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
                    bundle.putParcelableArrayList(ARG_SCHEDULED_TIME_LIST, scheduleTimeDetailList)
                    scheduleTimeNotes?.let {
                        bundle.putParcelable(ARG_SCHEDULED_TIME_NOTES, scheduleTimeNotes)
                    }
                    startIntent(
                        EditScheduleTimeActivity::class.java,
                        bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scheduleTimePresenter.onDestroy()
    }

    private fun getAvailableDates(): List<Date> {
        val list: MutableList<Date> = mutableListOf()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 5)
        val lastDate = calendar[Calendar.DATE]
        calendar.add(Calendar.DAY_OF_YEAR, -5)
        val currentDate = calendar[Calendar.DATE]
        calendar.add(Calendar.WEEK_OF_YEAR, -2)

        while (lastDate != calendar[Calendar.DATE]) {
            calendar.add(Calendar.DATE, 1)
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
            ) {
                list.add(calendar.time)
                if (currentDate == calendar[Calendar.DATE]) {
                    currentDatePosition = list.size - 1
                }
            }
        }
        return list
    }
}

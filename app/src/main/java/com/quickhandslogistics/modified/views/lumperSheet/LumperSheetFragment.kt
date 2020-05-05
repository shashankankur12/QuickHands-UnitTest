package com.quickhandslogistics.modified.views.lumperSheet

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
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.common.AddSignatureActivity
import com.quickhandslogistics.modified.adapters.lumperSheet.LumperSheetAdapter
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.lumperSheet.LumperSheetPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.lumpers.LumperDetailActivity
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.fragment_lumper_sheet.*
import kotlinx.android.synthetic.main.item_calendar_view.view.*
import java.util.*

class LumperSheetFragment : BaseFragment(), LumperSheetContract.View, TextWatcher,
    View.OnClickListener, LumperSheetContract.View.OnAdapterItemClickListener {

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>

    private lateinit var lumperSheetAdapter: LumperSheetAdapter
    private lateinit var lumperSheetPresenter: LumperSheetPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumperSheetPresenter = LumperSheetPresenter(this, resources)

        // Setup DatePicker Dates
        selectedTime = Date().time
        availableDates = getAvailableDates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lumper_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpersSheet.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumperSheetAdapter = LumperSheetAdapter(this@LumperSheetFragment)
            adapter = lumperSheetAdapter
        }

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)

        initializeCalendar()
        singleRowCalendarScheduleTime.select(availableDates.size - 1)
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
                    lumperSheetPresenter.getLumpersSheetByDate(date)
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

    private fun getAvailableDates(): List<Date> {
        val list: MutableList<Date> = mutableListOf()

        val calendar = Calendar.getInstance()
        val currentDate = calendar[Calendar.DATE]
        calendar.add(Calendar.WEEK_OF_YEAR, -2)

        while (currentDate != calendar[Calendar.DATE]) {
            calendar.add(Calendar.DATE, 1)
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
            ) {
                list.add(calendar.time)
            }
        }
        return list
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumperSheetAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
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
                buttonSubmit.id -> {

                }
            }
        }
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showDateString(dateString: String) {
        textViewDate.text = dateString
    }

    override fun showLumperSheetData(employeeDataList: ArrayList<EmployeeData>) {
        lumperSheetAdapter.updateLumpersData(employeeDataList)
        if (employeeDataList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpersSheet.visibility = View.VISIBLE
        } else {
            recyclerViewLumpersSheet.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(employeeData: EmployeeData) {
        val bundle = Bundle()
        bundle.putParcelable(LumperDetailActivity.ARG_LUMPER_DATA, employeeData)
        startIntent(LumperWorkDetailActivity::class.java, bundle = bundle)
    }

    override fun onAddSignatureItemClick(position: Int) {
        startIntent(AddSignatureActivity::class.java)
    }
}

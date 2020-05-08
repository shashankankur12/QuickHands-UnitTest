package com.quickhandslogistics.modified.views.lumperSheet

import android.app.Activity
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
import com.quickhandslogistics.modified.adapters.lumperSheet.LumperSheetAdapter
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.modified.presenters.lumperSheet.LumperSheetPresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.fragment_lumper_sheet.*
import kotlinx.android.synthetic.main.item_calendar_view.view.*
import java.util.*

class LumperSheetFragment : BaseFragment(), LumperSheetContract.View, TextWatcher,
    View.OnClickListener, LumperSheetContract.View.OnAdapterItemClickListener {

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>

    private lateinit var lumperSheetAdapter: LumperSheetAdapter
    private lateinit var lumperSheetPresenter: LumperSheetPresenter

    companion object {
        const val ARG_LUMPER_INFO = "ARG_LUMPER_INFO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumperSheetPresenter = LumperSheetPresenter(this, resources)

        // Setup DatePicker Dates
        selectedTime = Date().time
        availableDates = getAvailableDates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lumper_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLumpersSheet.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumperSheetAdapter = LumperSheetAdapter(resources, this@LumperSheetFragment)
            adapter = lumperSheetAdapter
        }

        lumperSheetAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility = if (lumperSheetAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

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

            override fun setCalendarViewResourceId(position: Int, date: Date, isSelected: Boolean): Int {
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
                    CustomProgressBar.getInstance().showWarningDialog(
                        getString(R.string.string_ask_to_submit_lumper_sheet), fragmentActivity!!, object : CustomDialogWarningListener {
                            override fun onConfirmClick() {
                                lumperSheetPresenter.initiateSheetSubmission(Date(selectedTime))
                            }

                            override fun onCancelClick() {
                            }
                        })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lumperSheetPresenter.onDestroy()
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showDateString(dateString: String) {
        textViewDate.text = dateString
    }

    override fun showLumperSheetData(lumperInfoList: ArrayList<LumpersInfo>, sheetSubmitted: Boolean, selectedDate: Date) {
        selectedTime = selectedDate.time

        var isSignatureLeft = 0
        for (lumperInfo in lumperInfoList) {
            if (!ValueUtils.getDefaultOrValue(lumperInfo.sheetSigned)) {
                isSignatureLeft++
            }
        }

        if (com.quickhandslogistics.utils.DateUtils.isCurrentDate(selectedTime) && lumperInfoList.size > 0) {
            buttonSubmit.visibility = if (!sheetSubmitted) View.VISIBLE else View.GONE
            buttonSubmit.isEnabled = !sheetSubmitted && isSignatureLeft == 0
        } else {
            buttonSubmit.visibility = View.GONE
        }

        lumperSheetAdapter.updateLumperSheetData(lumperInfoList)
        if (lumperInfoList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpersSheet.visibility = View.VISIBLE
        } else {
            recyclerViewLumpersSheet.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun sheetSubmittedSuccessfully() {
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.lumper_sheet_submitted_successfully),
            fragmentActivity!!, object : CustomDialogListener {
                override fun onConfirmClick() {
                    lumperSheetPresenter.getLumpersSheetByDate(Date(selectedTime))
                }
            })
    }

    override fun onItemClick(lumperInfo: LumpersInfo) {
        val bundle = Bundle()
        bundle.putParcelable(ARG_LUMPER_INFO, lumperInfo)
        bundle.putLong(ScheduleMainFragment.ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(LumperWorkDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            lumperSheetPresenter.getLumpersSheetByDate(Date(selectedTime))
        }
    }
}

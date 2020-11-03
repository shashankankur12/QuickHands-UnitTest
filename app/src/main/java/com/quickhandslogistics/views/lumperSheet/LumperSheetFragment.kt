package com.quickhandslogistics.views.lumperSheet

import android.app.Activity
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
import com.quickhandslogistics.adapters.lumperSheet.LumperSheetAdapter
import com.quickhandslogistics.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.presenters.lumperSheet.LumperSheetPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment
import kotlinx.android.synthetic.main.fragment_lumper_sheet.*
import java.util.*
import kotlin.collections.ArrayList

class LumperSheetFragment : BaseFragment(), LumperSheetContract.View, TextWatcher, View.OnClickListener,
    LumperSheetContract.View.OnAdapterItemClickListener, CalendarUtils.CalendarSelectionListener {

    private var selectedTime: Long = 0
    private lateinit var availableDates: List<Date>
    private var lumperInfoList: ArrayList<LumpersInfo> = ArrayList()
    private var sheetSubmitted: Boolean= false
    private var selectedDate: Date = Date()
    private var tempLumperIds: ArrayList<String> =ArrayList()
    private var dateString: String ?=null
    private var shift: String =""
    private var dept: String =""
    private var datePosition: Int = 0
    private var isSavedState: Boolean = false

    private lateinit var lumperSheetAdapter: LumperSheetAdapter
    private lateinit var lumperSheetPresenter: LumperSheetPresenter

    companion object {
        const val ARG_LUMPER_INFO = "ARG_LUMPER_INFO"
        const val LUMPER_INFO_LIST = "LUMPER_INFO_LIST"
        const val DATE_LUMPER_SHEET = "DATE_LUMPER_SHEET"
        const val DATE_STRING_HEADER = "DATE_STRING_HEADER"
        const val SHEET_SUBMITTED = "SHEET_SUBMITTED"
        const val TEMP_LUMPER_SHEET = "TEMP_LUMPER_SHEET"
        const val SELECTED_DATE_POSITION = "SELECTED_DATE_POSITION"
        const val HEADER_DEPT = "HEADER_DEPT"
        const val HEADER_SHIFT = "HEADER_SHIFT"
        const val TEMP_LUMPER = "TEMP_LUMPER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lumperSheetPresenter = LumperSheetPresenter(this, resources, sharedPref)

        // Setup Calendar Dates
        selectedTime = Date().time
        availableDates = CalendarUtils.getPastCalendarDates()
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
                invalidateEmptyView()
            }
        })

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarLumperSheet, availableDates, this)
        savedInstanceState?.also {
            isSavedState=true
            if (savedInstanceState.containsKey(SELECTED_DATE_POSITION)) {
                datePosition = savedInstanceState.getInt(SELECTED_DATE_POSITION)!!
                singleRowCalendarLumperSheet.select(datePosition)
            }
            if (savedInstanceState.containsKey(SHEET_SUBMITTED)) {
                sheetSubmitted = savedInstanceState.getBoolean(SHEET_SUBMITTED)!!
            }
            if (savedInstanceState.containsKey(TEMP_LUMPER_SHEET)) {
                tempLumperIds = savedInstanceState.getStringArrayList(TEMP_LUMPER_SHEET)!!

            }
            if(savedInstanceState.containsKey(DATE_LUMPER_SHEET)) {
                selectedDate = savedInstanceState.getSerializable(DATE_LUMPER_SHEET) as Date
            }
            if (savedInstanceState.containsKey(LUMPER_INFO_LIST)) {
                lumperInfoList = savedInstanceState.getParcelableArrayList(LUMPER_INFO_LIST)!!
                showLumperSheetData(lumperInfoList, sheetSubmitted, selectedDate, tempLumperIds)
            }
            if (savedInstanceState.containsKey(HEADER_SHIFT)) {
                shift = savedInstanceState.getString(HEADER_SHIFT)!!
            }
            if (savedInstanceState.containsKey(HEADER_DEPT)) {
                dept = savedInstanceState.getString(HEADER_DEPT)!!
            }
            if (savedInstanceState.containsKey(DATE_STRING_HEADER)) {
                dateString = savedInstanceState.getString(DATE_STRING_HEADER)!!
                showDateString(dateString!!, shift,dept)
            }

        } ?: run {
            isSavedState=false
            singleRowCalendarLumperSheet.select(availableDates.size - 1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lumperSheetPresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (lumperInfoList != null)
            outState.putParcelableArrayList(LUMPER_INFO_LIST, lumperInfoList)
        if (sheetSubmitted != null)
            outState.putBoolean(SHEET_SUBMITTED, sheetSubmitted)
        if (tempLumperIds != null)
            outState.putStringArrayList(TEMP_LUMPER_SHEET, tempLumperIds)
        if (selectedDate != null)
            outState.putSerializable(DATE_LUMPER_SHEET, selectedDate)
        if (dateString != null)
            outState.putString(DATE_STRING_HEADER, dateString)
        if (datePosition != null)
            outState.putInt(SELECTED_DATE_POSITION, datePosition)
        if (dept != null)
            outState.putString(HEADER_DEPT, dept)
        if (shift != null)
            outState.putString(HEADER_SHIFT, shift)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            lumperSheetPresenter.getLumpersSheetByDate(Date(selectedTime))
        }
    }

    private fun invalidateEmptyView() {
        if (lumperSheetAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (lumperSheetAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_lumper_sheet_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumper_sheet_info_message)
        }
    }

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(
            getString(R.string.submit_lumper_sheet_alert_message), fragmentActivity!!, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    lumperSheetPresenter.initiateSheetSubmission(Date(selectedTime))
                }

                override fun onCancelClick() {
                }
            })
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(fragmentActivity!!)
                }
                buttonSubmit.id -> showConfirmationDialog()
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumperSheetAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showDateString(dateString: String, shift: String , dept : String) {
        this.dateString=dateString
        this.shift=shift
        this.dept=dept

        textViewDate.text = UIUtils.getSpannedText(dateString)
        textViewDate.visibility = View.GONE
        textViewHeaderShift.visibility = View.GONE
        textViewHeaderDept.visibility = View.GONE
        textViewHeaderShift.text = UIUtils.getSpannedText(shift)
        textViewHeaderDept.text = UIUtils.getSpannedText(dept)
    }

    override fun showLumperSheetData(mlumperInfoList: ArrayList<LumpersInfo>, msheetSubmitted: Boolean, mselectedDate: Date, mtempLumperIds: ArrayList<String>) {
        lumperInfoList = mlumperInfoList
        sheetSubmitted = msheetSubmitted
        selectedDate = mselectedDate
        tempLumperIds = mtempLumperIds

        selectedTime = selectedDate.time

        var isSignatureLeft = 0
        for (lumperInfo in lumperInfoList) {
            if (!ValueUtils.getDefaultOrValue(lumperInfo.sheetSigned)) {
                isSignatureLeft++
            }
        }

        if ((DateUtils.isCurrentDate(selectedTime) && lumperInfoList.size > 0)) {
            buttonSubmit.visibility = View.VISIBLE
            buttonSubmit.isEnabled = !sheetSubmitted && isSignatureLeft == 0
            if (sheetSubmitted) {
                buttonSubmit.text = getText(R.string.sheet_submitted)
            } else {
                buttonSubmit.text = getText(R.string.submit)
            }
        } else {
            buttonSubmit.visibility = View.GONE
        }

        lumperSheetAdapter.updateLumperSheetData(lumperInfoList, tempLumperIds)
        if (lumperInfoList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpersSheet.visibility = View.VISIBLE
        } else {
            recyclerViewLumpersSheet.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun sheetSubmittedSuccessfully() {
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.lumper_sheet_success_message), fragmentActivity!!, object : CustomDialogListener {
            override fun onConfirmClick() {
                lumperSheetPresenter.getLumpersSheetByDate(Date(selectedTime))
            }
        })
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */
    override fun onItemClick(lumperInfo: LumpersInfo) {
        val bundle = Bundle()
        bundle.putParcelable(ARG_LUMPER_INFO, lumperInfo)
        bundle.putStringArrayList(TEMP_LUMPER, tempLumperIds)
        bundle.putLong(ScheduleFragment.ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        startIntent(LumperWorkDetailActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(date: Date, selected: Boolean, position: Int) {
        if (!isSavedState)
            lumperSheetPresenter.getLumpersSheetByDate(date)
        isSavedState = false
        datePosition = position
    }
}
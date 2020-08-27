package com.quickhandslogistics.views.reports

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RadioGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.reports.LumperJobReportAdapter
import com.quickhandslogistics.contracts.reports.LumperJobReportContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.reports.LumperJobReportPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.content_lumper_job_report.*
import kotlinx.android.synthetic.main.layout_date_filter.*
import kotlinx.android.synthetic.main.layout_report_type.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LumperJobReportActivity : BaseActivity(), View.OnClickListener, LumperJobReportContract.View,
    LumperJobReportContract.View.OnAdapterItemClickListener, TextWatcher, RadioGroup.OnCheckedChangeListener {

    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null
    private var startDate: String = ""
    private var endDate: String = ""
    private var mCheckedId: Int = 0


    private  var lumperJobReportPresenter: LumperJobReportPresenter ? =null
    private lateinit var lumperJobReportAdapter: LumperJobReportAdapter
    private  var employeeDataList: ArrayList<EmployeeData> = ArrayList()

    companion object {
        const val LUMPER_JOB_REPORT_LIST = "LUMPER_JOB_REPORT_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_job_report)
        setupToolbar(getString(R.string.lumper_sheet_report))

        initializeUI()
        lumperJobReportPresenter = LumperJobReportPresenter(this, resources)


        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LUMPER_JOB_REPORT_LIST)) {
                employeeDataList = savedInstanceState.getParcelableArrayList(LUMPER_JOB_REPORT_LIST)!!
                showLumpersData(employeeDataList)
            }
        } ?: run {
            val dateString = DateUtils.getCurrentDateStringByEmployeeShift()
            lumperJobReportPresenter!!.fetchLumpersList(dateString, dateString)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (employeeDataList != null)
            outState.putParcelableArrayList(LUMPER_JOB_REPORT_LIST, employeeDataList)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        lumperJobReportPresenter!!.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            PermissionUtil.PERMISSION_REQUEST_CODE -> {
                if (PermissionUtil.granted(grantResults)) {
                    showConfirmationDialog()
                }
            }
        }
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            lumperJobReportAdapter = LumperJobReportAdapter(this@LumperJobReportActivity)
            adapter = lumperJobReportAdapter
        }

        lumperJobReportAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        updateTimeByRangeOptionSelected()

        linearLayoutSelectAll.setOnClickListener(this)
        radioGroupDateRange.setOnCheckedChangeListener(this)
        textViewStartDate.setOnClickListener(this)
        textViewEndDate.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonGenerateReport.setOnClickListener(this)
    }

    private fun resetAllData() {
        lumperJobReportAdapter.clearAllSelection()
        radioGroupDateRange.check(radioButtonDaily.id)
        radioGroupReportType.check(radioButtonPdf.id)
    }

    private fun updateTimeByRangeOptionSelected() {
        textViewStartDate.isEnabled = radioGroupDateRange.checkedRadioButtonId == radioButtonCustom.id
        textViewEndDate.isEnabled = radioGroupDateRange.checkedRadioButtonId == radioButtonCustom.id
        mCheckedId=radioGroupDateRange.checkedRadioButtonId

        val calendar = Calendar.getInstance()
        when (radioGroupDateRange.checkedRadioButtonId) {
            radioButtonDaily.id -> {
                selectedEndDate = calendar.time
                selectedStartDate = calendar.time

            }
            radioButtonWeekly.id -> {
                selectedEndDate = calendar.time

                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                selectedStartDate = calendar.time
            }
            radioButtonMonthly.id -> {
                selectedEndDate = calendar.time
                calendar.set(Calendar.DATE, 1)
                selectedStartDate = calendar.time
            }
            radioButtonCustom.id -> {
                customeClick()
            }
        }
        if(selectedStartDate!=null && selectedEndDate!= null){
            getLumperListData()
        }
        updateSelectedDateText()
    }

    private fun customeClick() {
        selectedStartDate = null
        selectedEndDate = null
        lumperJobReportAdapter.clearAllSelection()
        employeeDataList.clear()
        lumperJobReportAdapter.updateLumpersData(employeeDataList)
    }

    private fun getLumperListData() {
        startDate =DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedStartDate!!)
        endDate =DateUtils.getDateString(DateUtils.PATTERN_API_REQUEST_PARAMETER, selectedEndDate!!)
        if (lumperJobReportPresenter!=null)
            lumperJobReportPresenter!!.fetchLumpersList(startDate, endDate)

    }


    private fun updateSelectedDateText() {
        selectedStartDate?.also { date ->
            textViewStartDate.text = DateUtils.getDateString(DateUtils.PATTERN_MONTH_DAY_DISPLAY, date)
        } ?: run {
            textViewStartDate.text = ""
        }

        selectedEndDate?.also { date ->
            textViewEndDate.text = DateUtils.getDateString(DateUtils.PATTERN_MONTH_DAY_DISPLAY, date)
        } ?: run {
            textViewEndDate.text = ""
        }

        updateGenerateButtonUI()
    }

    private fun updateSelectAllSectionUI() {
        val selectedCount = lumperJobReportAdapter.getSelectedLumperIdsList().size
        if (selectedCount == lumperJobReportAdapter.itemCount && lumperJobReportAdapter.itemCount>0) {
            imageViewSelectAll.setImageResource(R.drawable.ic_add_lumer_tick)
        //    textViewSelectAll.text = getString(R.string.unselect_all)
        } else {
            imageViewSelectAll.setImageResource(R.drawable.ic_add_lumer_tick_blank)
            textViewSelectAll.text = getString(R.string.select_all)
        }
    }

    private fun updateGenerateButtonUI() {
        buttonGenerateReport.isEnabled = selectedStartDate != null && selectedEndDate != null && lumperJobReportAdapter.getSelectedLumperIdsList().size > 0
    }

    private fun invalidateEmptyView() {
        if (lumperJobReportAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (lumperJobReportAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else if (selectedStartDate == null || selectedEndDate == null) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            }else {
                textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
        }
    }

    private fun showStartDatePicker() {
        ReportUtils.showStartDatePicker(selectedStartDate, selectedEndDate, activity, object : ReportUtils.OnDateSetListener {
            override fun onDateSet(selected: Date) {
                selectedStartDate = selected
                updateSelectedDateText()
                if (selectedEndDate != null)
                    getLumperListData()
            }
        })
    }

    private fun showEndDatePicker() {
        ReportUtils.showEndDatePicker(selectedStartDate, selectedEndDate, activity, object : ReportUtils.OnDateSetListener {
            override fun onDateSet(selected: Date) {
                selectedEndDate = selected
                updateSelectedDateText()
                if (selectedStartDate != null)
                    getLumperListData()
            }
        })
    }

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.generate_report_alert_message), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                val reportType = if (radioGroupReportType.checkedRadioButtonId == radioButtonPdf.id) "pdf" else "excel"
                if(lumperJobReportPresenter!=null)
                lumperJobReportPresenter!!.createTimeClockReport(selectedStartDate!!, selectedEndDate!!, reportType, lumperJobReportAdapter.getSelectedLumperIdsList())
            }

            override fun onCancelClick() {
            }
        })
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(activity)
                }
                textViewStartDate.id -> showStartDatePicker()
                textViewEndDate.id -> showEndDatePicker()
                buttonGenerateReport.id -> {
                    if (PermissionUtil.checkStorage(activity)) {
                        showConfirmationDialog()
                    } else {
                        PermissionUtil.requestStorage(activity)
                    }
                }
                linearLayoutSelectAll.id -> lumperJobReportAdapter.invokeSelectAll()
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumperJobReportAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (!mCheckedId.equals(checkedId)) {
            updateTimeByRangeOptionSelected()
            lumperJobReportAdapter.clearAllSelection()
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.employeeDataList=employeeDataList
        lumperJobReportAdapter.updateLumpersData(employeeDataList)
    }

    override fun showReportDownloadDialog(reportUrl: String, mimeType: String) {
        DownloadUtils.downloadFile(reportUrl, mimeType, activity)

        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.reports_generate_success_message),
            activity, object : CustomDialogListener {
                override fun onConfirmClick() {
                    resetAllData()
                }
            })
    }

    /** Adapter Listeners */
    override fun onLumperSelectionChanged() {
        updateSelectAllSectionUI()
        updateGenerateButtonUI()
    }
}


/*private fun showRangeDatePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val constraintsBuilder = CalendarConstraints.Builder()

        val startCalendar = Calendar.getInstance()
        startCalendar.set(Calendar.YEAR, 2017)
        startCalendar.set(Calendar.MONTH, Calendar.JANUARY)
        constraintsBuilder.setStart(startCalendar.timeInMillis)

        val endCalendar = Calendar.getInstance()
        constraintsBuilder.setEnd(endCalendar.timeInMillis)

        constraintsBuilder.setValidator(object : CalendarConstraints.DateValidator {
            override fun writeToParcel(dest: Parcel?, flags: Int) {
            }

            override fun isValid(date: Long): Boolean {
                return date <= endCalendar.timeInMillis
            }

            override fun describeContents(): Int {
                return 0
            }

        })
        builder.setCalendarConstraints(constraintsBuilder.build())
        builder.setTheme(R.style.rangeCalendarTheme)

        if (selectedStartDate != null && selectedEndDate != null) {
            builder.setSelection(androidx.core.util.Pair(selectedStartDate!!.time, selectedEndDate!!.time))
        }

        val datePicker = builder.build()
        datePicker.isCancelable = false
        datePicker.addOnPositiveButtonClickListener { pair ->
            pair.first?.let { milliseconds ->
                selectedStartDate = Date(milliseconds)
                textViewStartDate.text = DateUtils.getDateString(DateUtils.PATTERN_DATE_DISPLAY, selectedStartDate!!)
            }
            pair.second?.let { milliseconds ->
                selectedEndDate = Date(milliseconds)
                textViewEndDate.text = DateUtils.getDateString(DateUtils.PATTERN_DATE_DISPLAY, selectedEndDate!!)
            }
            updateGenerateButtonUI()
        }
        datePicker.show(supportFragmentManager, datePicker.toString())
    }*/
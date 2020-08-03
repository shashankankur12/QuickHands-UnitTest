package com.quickhandslogistics.views.reports

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.reports.TimeClockReportAdapter
import com.quickhandslogistics.contracts.reports.TimeClockReportContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.reports.TimeClockReportPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.content_lumper_job_report.*
import kotlinx.android.synthetic.main.layout_date_filter.*
import kotlinx.android.synthetic.main.layout_report_type.*
import java.util.*
import kotlin.collections.ArrayList

class TimeClockReportActivity : BaseActivity(), View.OnClickListener, TimeClockReportContract.View,
    TimeClockReportContract.View.OnAdapterItemClickListener, TextWatcher, RadioGroup.OnCheckedChangeListener {

    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null

    private lateinit var timeClockReportPresenter: TimeClockReportPresenter
    private lateinit var timeClockReportAdapter: TimeClockReportAdapter
    private var employeeDataList: ArrayList<EmployeeData> = ArrayList()

    companion object {
        const val LUMPER_REPORT_LIST = "LUMPER_REPORT_LIST"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_job_report)
        setupToolbar(getString(R.string.time_clock_report))

        initializeUI()

        timeClockReportPresenter = TimeClockReportPresenter(this, resources)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LUMPER_REPORT_LIST)) {
                employeeDataList = savedInstanceState.getParcelableArrayList(LUMPER_REPORT_LIST)!!
                showLumpersData(employeeDataList)
            }
        } ?: run {
            timeClockReportPresenter.fetchLumpersList()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (employeeDataList != null)
            outState.putParcelableArrayList(LUMPER_REPORT_LIST, employeeDataList)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        timeClockReportPresenter.onDestroy()
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
            timeClockReportAdapter = TimeClockReportAdapter(this@TimeClockReportActivity)
            adapter = timeClockReportAdapter
        }

        timeClockReportAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
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
        timeClockReportAdapter.clearAllSelection()
        radioGroupDateRange.check(radioButtonDaily.id)
        radioGroupReportType.check(radioButtonPdf.id)
    }

    private fun updateTimeByRangeOptionSelected() {
        textViewStartDate.isEnabled = radioGroupDateRange.checkedRadioButtonId == radioButtonCustom.id
        textViewEndDate.isEnabled = radioGroupDateRange.checkedRadioButtonId == radioButtonCustom.id

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
                selectedEndDate = null
                selectedStartDate = null
            }
        }
        updateSelectedDateText()
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
        val selectedCount = timeClockReportAdapter.getSelectedLumperIdsList().size
        if (selectedCount == timeClockReportAdapter.itemCount) {
            imageViewSelectAll.setImageResource(R.drawable.ic_add_lumer_tick)
          //  textViewSelectAll.text = getString(R.string.unselect_all)
        } else {
            imageViewSelectAll.setImageResource(R.drawable.ic_add_lumer_tick_blank)
            textViewSelectAll.text = getString(R.string.select_all)
        }
    }

    private fun updateGenerateButtonUI() {
        buttonGenerateReport.isEnabled = selectedStartDate != null && selectedEndDate != null && timeClockReportAdapter.getSelectedLumperIdsList().size > 0
    }

    private fun invalidateEmptyView() {
        if (timeClockReportAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (timeClockReportAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
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
            }
        })
    }

    private fun showEndDatePicker() {
        ReportUtils.showEndDatePicker(selectedStartDate, selectedEndDate, activity, object : ReportUtils.OnDateSetListener {
            override fun onDateSet(selected: Date) {
                selectedEndDate = selected
                updateSelectedDateText()
            }
        })
    }

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.generate_report_alert_message), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                val reportType = if (radioGroupReportType.checkedRadioButtonId == radioButtonPdf.id) "pdf" else "excel"
                timeClockReportPresenter.createTimeClockReport(selectedStartDate!!, selectedEndDate!!, reportType, timeClockReportAdapter.getSelectedLumperIdsList())
            }

            override fun onCancelClick() {
            }
        })
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
                linearLayoutSelectAll.id -> timeClockReportAdapter.invokeSelectAll()
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            timeClockReportAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        updateTimeByRangeOptionSelected()
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.employeeDataList=employeeDataList
        timeClockReportAdapter.updateLumpersData(employeeDataList)
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
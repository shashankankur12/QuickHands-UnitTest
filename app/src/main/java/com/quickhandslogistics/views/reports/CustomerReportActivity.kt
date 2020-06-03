package com.quickhandslogistics.views.reports

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.reports.CustomerReportContract
import com.quickhandslogistics.presenters.reports.CustomerReportPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import kotlinx.android.synthetic.main.content_customer_report.*
import kotlinx.android.synthetic.main.layout_date_filter.*
import kotlinx.android.synthetic.main.layout_report_type.*
import java.util.*

class CustomerReportActivity : BaseActivity(), View.OnClickListener, CustomerReportContract.View, RadioGroup.OnCheckedChangeListener {

    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null

    private lateinit var customerReportPresenter: CustomerReportPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_report)
        setupToolbar(getString(R.string.customer_report))

        initializeUI()

        customerReportPresenter = CustomerReportPresenter(this, resources)
    }

    override fun onDestroy() {
        super.onDestroy()
        customerReportPresenter.onDestroy()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        menu?.findItem(R.id.actionRefresh)?.isVisible = true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionRefresh -> {
                resetAllData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeUI() {
        updateTimeByRangeOptionSelected()

        radioGroupDateRange.setOnCheckedChangeListener(this)
        textViewStartDate.setOnClickListener(this)
        textViewEndDate.setOnClickListener(this)
        buttonGenerateReport.setOnClickListener(this)
    }

    private fun resetAllData() {
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
                updateSelectedDateText()
            }
            radioButtonWeekly.id -> {
                selectedEndDate = calendar.time
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                selectedStartDate = calendar.time
                updateSelectedDateText()
            }
            radioButtonMonthly.id -> {
                selectedEndDate = calendar.time
                calendar.add(Calendar.MONTH, -1)
                selectedStartDate = calendar.time
                updateSelectedDateText()
            }
            radioButtonCustom.id -> {
                selectedStartDate = null
                selectedEndDate = null
                updateSelectedDateText()
            }
        }
    }

    private fun updateSelectedDateText() {
        selectedStartDate?.also { date ->
            textViewStartDate.text = DateUtils.getDateString(DateUtils.PATTERN_DATE_DISPLAY, date)
        } ?: run {
            textViewStartDate.text = ""
        }

        selectedEndDate?.also { date ->
            textViewEndDate.text = DateUtils.getDateString(DateUtils.PATTERN_DATE_DISPLAY, date)
        } ?: run {
            textViewEndDate.text = ""
        }

        updateGenerateButtonUI()
    }

    private fun updateGenerateButtonUI() {
        buttonGenerateReport.isEnabled = selectedStartDate != null && selectedEndDate != null
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
        CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                showSuccessDialog()
            }

            override fun onCancelClick() {
            }
        })
    }

    private fun showSuccessDialog() {
        CustomProgressBar.getInstance().showSuccessOptionDialog(getString(R.string.reports_generate_success_message),
            activity, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    downloadFile()
                }

                override fun onCancelClick() {
                }
            })
    }

    private fun downloadFile() {
        val excelFileUrl = "https://file-examples.com/wp-content/uploads/2017/02/file_example_XLSX_5000.xlsx"
        val pdfFileUrl = "https://file-examples.com/wp-content/uploads/2017/10/file-example_PDF_1MB.pdf"

        val fileUrl = if (radioGroupReportType.checkedRadioButtonId == radioButtonPdf.id) pdfFileUrl else excelFileUrl
        DownloadUtils.downloadFile(fileUrl, activity)
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewStartDate.id -> showStartDatePicker()
                textViewEndDate.id -> showEndDatePicker()
                buttonGenerateReport.id -> {
                    if (PermissionUtil.checkStorage(activity)) {
                        showConfirmationDialog()
                    } else {
                        PermissionUtil.requestStorage(activity)
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        updateTimeByRangeOptionSelected()
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }
}
package com.quickhandslogistics.views.reports

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
        val calendar = Calendar.getInstance()
        selectedStartDate?.let { date ->
            calendar.time = date
        }
        val picker = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedStartDate = calendar.time
            updateSelectedDateText()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        selectedEndDate?.also { date ->
            picker.datePicker.maxDate = date.time
        } ?: run {
            picker.datePicker.maxDate = System.currentTimeMillis()
        }
        picker.show()
    }

    private fun showEndDatePicker() {
        val calendar = Calendar.getInstance()
        selectedEndDate?.let { date ->
            calendar.time = date
        }
        val picker = DatePickerDialog(activity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedEndDate = calendar.time
            updateSelectedDateText()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        selectedStartDate?.let { date ->
            picker.datePicker.minDate = date.time
        }
        picker.datePicker.maxDate = System.currentTimeMillis()
        picker.show()
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
        var fileName: String = fileUrl.substring(fileUrl.lastIndexOf('/') + 1)
        fileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1)

        val request = DownloadManager.Request(Uri.parse(fileUrl))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setTitle(fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
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
package com.quickhandslogistics.views.reports

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.quickhandslogistics.adapters.reports.LumperJobReportAdapter
import com.quickhandslogistics.contracts.reports.LumperJobReportContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.reports.LumperJobReportPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import kotlinx.android.synthetic.main.content_lumper_job_report.*
import kotlinx.android.synthetic.main.layout_date_filter.*
import kotlinx.android.synthetic.main.layout_report_type.*
import java.util.*

class LumperJobReportActivity : BaseActivity(), View.OnClickListener, LumperJobReportContract.View,
    LumperJobReportContract.View.OnAdapterItemClickListener, TextWatcher, RadioGroup.OnCheckedChangeListener {

    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null

    private lateinit var lumperJobReportPresenter: LumperJobReportPresenter
    private lateinit var lumperJobReportAdapter: LumperJobReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_job_report)
        setupToolbar(getString(R.string.lumper_job_report))

        initializeUI()

        lumperJobReportPresenter = LumperJobReportPresenter(this, resources)
        lumperJobReportPresenter.fetchLumpersList()
    }

    override fun onDestroy() {
        super.onDestroy()
        lumperJobReportPresenter.onDestroy()
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

    private fun updateSelectAllSectionUI() {
        val selectedCount = lumperJobReportAdapter.getSelectedLumpersList().size
        if (selectedCount == lumperJobReportAdapter.itemCount) {
            imageViewSelectAll.setImageResource(R.drawable.ic_add_lumer_tick)
            textViewSelectAll.text = getString(R.string.unselect_all)
        } else {
            imageViewSelectAll.setImageResource(R.drawable.ic_add_lumer_tick_blank)
            textViewSelectAll.text = getString(R.string.select_all)
        }
    }

    private fun updateGenerateButtonUI() {
        buttonGenerateReport.isEnabled = selectedStartDate != null && selectedEndDate != null && lumperJobReportAdapter.getSelectedLumpersList().size > 0
    }

    private fun invalidateEmptyView() {
        if (lumperJobReportAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (lumperJobReportAdapter.isSearchEnabled()) {
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
        updateTimeByRangeOptionSelected()
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        lumperJobReportAdapter.updateLumpersData(employeeDataList)
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
package com.quickhandslogistics.views.reports

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.reports.LumperJobHistoryAdapter
import com.quickhandslogistics.contracts.reports.LumperJobHistoryContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.reports.LumperJobHistoryPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import kotlinx.android.synthetic.main.content_lumper_job_history.*
import java.util.*

class LumperJobHistoryActivity : BaseActivity(), View.OnClickListener, LumperJobHistoryContract.View,
    LumperJobHistoryContract.View.OnAdapterItemClickListener, TextWatcher {

    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null

    private lateinit var lumperJobHistoryPresenter: LumperJobHistoryPresenter
    private lateinit var lumperJobHistoryAdapter: LumperJobHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lumper_job_history)
        setupToolbar(getString(R.string.lumper_job_history))

        initializeUI()

        lumperJobHistoryPresenter = LumperJobHistoryPresenter(this, resources)
        lumperJobHistoryPresenter.fetchLumpersList()
    }

    override fun onDestroy() {
        super.onDestroy()
        lumperJobHistoryPresenter.onDestroy()
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
            lumperJobHistoryAdapter = LumperJobHistoryAdapter(this@LumperJobHistoryActivity)
            adapter = lumperJobHistoryAdapter
        }

        lumperJobHistoryAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        textViewStartDate.setOnClickListener(this)
        textViewEndDate.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonGenerateReport.setOnClickListener(this)
    }

    private fun invalidateEmptyView() {
        if (lumperJobHistoryAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (lumperJobHistoryAdapter.isSearchEnabled()) {
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
            textViewStartDate.text = DateUtils.getDateString(DateUtils.PATTERN_DATE_DISPLAY, calendar.time)
            updateGenerateButtonUI()
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
            textViewEndDate.text = DateUtils.getDateString(DateUtils.PATTERN_DATE_DISPLAY, calendar.time)
            updateGenerateButtonUI()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        selectedStartDate?.let { date ->
            picker.datePicker.minDate = date.time
        }
        picker.datePicker.maxDate = System.currentTimeMillis()
        picker.show()
    }

    private fun updateGenerateButtonUI() {
        buttonGenerateReport.isEnabled = selectedStartDate != null && selectedEndDate != null && lumperJobHistoryAdapter.getSelectedLumpersList().size > 0
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
        val fileUrl = "https://file-examples.com/wp-content/uploads/2017/02/file_example_XLSX_5000.xlsx"
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
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            lumperJobHistoryAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        lumperJobHistoryAdapter.updateLumpersData(employeeDataList)
    }

    /** Adapter Listeners */
    override fun onSelectLumper() {
        updateGenerateButtonUI()
    }
}
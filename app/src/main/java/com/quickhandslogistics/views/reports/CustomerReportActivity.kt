package com.quickhandslogistics.views.reports

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.reports.CustomerJobReportAdapter
import com.quickhandslogistics.contracts.reports.CustomerReportContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.presenters.reports.CustomerReportPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.content_customer_report.*
import kotlinx.android.synthetic.main.content_customer_report.buttonGenerateReport
import kotlinx.android.synthetic.main.content_customer_report.mainConstraintLayout
import kotlinx.android.synthetic.main.content_customer_report.recyclerViewCustomer
import kotlinx.android.synthetic.main.content_lumper_job_report.*
import kotlinx.android.synthetic.main.layout_date_filter.*
import kotlinx.android.synthetic.main.layout_report_type.*
import java.util.*
import kotlinx.android.synthetic.main.content_lumper_job_report.textViewEmptyData as textViewEmptyData1

class CustomerReportActivity : BaseActivity(), View.OnClickListener, CustomerReportContract.View, CustomerReportContract.View.OnAdapterItemClickListener, RadioGroup.OnCheckedChangeListener {

    private var selectedStartDate: Date? = null
    private var selectedEndDate: Date? = null

    private lateinit var customerReportPresenter: CustomerReportPresenter
    private lateinit var customerJobReportAdapter: CustomerJobReportAdapter
    private  var buildingDetailList: ArrayList<BuildingDetailData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_report)
        setupToolbar(getString(R.string.customer_report))

        initializeUI()
        getBuildingDetail()

        customerReportPresenter = CustomerReportPresenter(this, resources)
    }

    private fun getBuildingDetail() {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        var buildingDetailData= leadProfile?.buildingDetailData

        buildingDetailData?.let { buildingDetailList.add(it) }

        customerJobReportAdapter.updateLumpersData(buildingDetailList)
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

    private fun initializeUI() {
        recyclerViewCustomer.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            customerJobReportAdapter = CustomerJobReportAdapter(this@CustomerReportActivity)
            adapter = customerJobReportAdapter
        }

        customerJobReportAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        updateTimeByRangeOptionSelected()

        radioGroupDateRange.setOnCheckedChangeListener(this)
        textViewStartDate.setOnClickListener(this)
        textViewEndDate.setOnClickListener(this)
        buttonGenerateReport.setOnClickListener(this)
    }

    private fun invalidateEmptyView() {
        if (customerJobReportAdapter.itemCount == 0) {
            buttonGenerateReport.isEnabled=false
            textViewEmptyData.visibility = View.VISIBLE
        } else {
            buttonGenerateReport.isEnabled=true
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
        }
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
                selectedStartDate = null
                selectedEndDate = null
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
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.generate_report_alert_message), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                val reportType = if (radioGroupReportType.checkedRadioButtonId == radioButtonPdf.id) "pdf" else "excel"
                customerReportPresenter.createTimeClockReport(selectedStartDate!!, selectedEndDate!!, reportType)
            }

            override fun onCancelClick() {
            }
        })
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

    override fun showReportDownloadDialog(reportUrl: String, mimeType: String) {
        DownloadUtils.downloadFile(reportUrl, mimeType, activity)

        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.reports_generate_success_message),
            activity, object : CustomDialogListener {
                override fun onConfirmClick() {
                    resetAllData()
                }
            })
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onLumperSelectionChanged() {
        TODO("Not yet implemented")
    }
}
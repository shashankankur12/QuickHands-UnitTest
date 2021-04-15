package com.quickhandslogistics.views.workSheet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.AllWorkScheduleCancelAdapter
import com.quickhandslogistics.contracts.reports.LumperJobReportContract
import com.quickhandslogistics.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.workSheet.AllWorkScheduleCancelPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.activity_all_work_schedule_cancel.*

class AllWorkScheduleCancelActivity : BaseActivity(), View.OnClickListener, LumperJobReportContract.View.OnAdapterItemClickListener, AllWorkScheduleCancelContract.View {

    private lateinit var allWorkScheduleCancelPresenter: AllWorkScheduleCancelPresenter
    private lateinit var allWorkScheduleCancelAdapter: AllWorkScheduleCancelAdapter
    private var employeeDataList: java.util.ArrayList<EmployeeData> =ArrayList()

    companion object {
        const val WORK_SCHEDULE_LIST = "WORK_SCHEDULE_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_work_schedule_cancel)
        setupToolbar(getString(R.string.cancel_all_work_schedules))

        initializeUI()

        allWorkScheduleCancelPresenter = AllWorkScheduleCancelPresenter(this, resources)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(WORK_SCHEDULE_LIST)) {
                employeeDataList = savedInstanceState.getParcelableArrayList(WORK_SCHEDULE_LIST)!!
                showLumpersData(employeeDataList)
            }
        } ?: run {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }

            allWorkScheduleCancelPresenter.fetchLumpersList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (employeeDataList != null)
            outState.putParcelableArrayList(WORK_SCHEDULE_LIST, employeeDataList)
        super.onSaveInstanceState(outState)
    }

    fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            allWorkScheduleCancelAdapter = AllWorkScheduleCancelAdapter(this@AllWorkScheduleCancelActivity)
            adapter = allWorkScheduleCancelAdapter
        }

        allWorkScheduleCancelAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        addNotesTouchListener(editTextCustomerNotes)
        addNotesTouchListener(editTextQHLNotes)

        buttonSubmit.setOnClickListener(this)
//        editTextSearch.addTextChangedListener(this)
        imageViewAdd.setOnClickListener(this)
        buttonCancelRequest.setOnClickListener(this)
        mainConstraintLayout.setOnClickListener(this)
    }

    private fun invalidateEmptyView() {
        if (allWorkScheduleCancelAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
//            isDataSave(true)
            buttonSubmit.isEnabled=false
            if (allWorkScheduleCancelAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_work_item_cancel_message)
            }
        } else if (allWorkScheduleCancelAdapter.getSelectedLumper().size>0) {
            buttonSubmit.isEnabled=true
//            isDataSave(false)
        }else{
            buttonSubmit.isEnabled=false
//            isDataSave(true)
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_work_item_cancel_message)
        }
    }

    private fun showConfirmationDialog(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
        
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.cancel_all_schedules_alert_message), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                allWorkScheduleCancelPresenter.initiateCancellingWorkSchedules(selectedLumperIdsList, notesQHL, notesCustomer)
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                buttonSubmit.id -> {
                    val selectedLumperIdsList = allWorkScheduleCancelAdapter.getSelectedLumper()
                    val notesQHL = editTextQHLNotes.text.toString()
                    val notesCustomer = editTextCustomerNotes.text.toString()
                    showConfirmationDialog(selectedLumperIdsList, notesQHL, notesCustomer)
                }

                imageViewAdd.id -> {
                    allWorkScheduleCancelAdapter.invokeSelectAll()
                }
                buttonCancelRequest.id -> {
                    super.onBackPressed()
                }

                mainConstraintLayout.id->{
                    AppUtils.hideSoftKeyboard(this)
                }
            }
        }
    }

    private fun updateSelectAllSectionUI() {
        val selectedCount = allWorkScheduleCancelAdapter.getSelectedLumper().size
        if (selectedCount == allWorkScheduleCancelAdapter.itemCount) {
            imageViewAdd.setImageResource(R.drawable.ic_add_lumper_green_tick)
        } else {
            imageViewAdd.setImageResource(R.drawable.ic_add_lumer_tick_blank)
        }
    }


    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    /** Adapter Listeners */
    override fun onLumperSelectionChanged() {
        updateSelectAllSectionUI()
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.employeeDataList=employeeDataList
        if (employeeDataList.size>0){
            layoutClockedIn.visibility = View.VISIBLE
            textViewClockIn.text=getString(R.string.work_sheet_lumper_message)+ " ( " +employeeDataList.size +" )"
        }else{
            layoutClockedIn.visibility = View.GONE
        }
        allWorkScheduleCancelAdapter.updateLumpersData(employeeDataList)
    }

    override fun cancellingWorkScheduleFinished() {
        setResult(RESULT_OK)
//        isDataSave(true)
        onBackPressed()
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
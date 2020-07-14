package com.quickhandslogistics.views.workSheet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.workSheet.AllWorkScheduleCancelAdapter
import com.quickhandslogistics.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.presenters.workSheet.AllWorkScheduleCancelPresenter
import com.quickhandslogistics.utils.AppUtils
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import kotlinx.android.synthetic.main.activity_all_work_schedule_cancel.*

class AllWorkScheduleCancelActivity : BaseActivity(), View.OnClickListener, TextWatcher, AllWorkScheduleCancelContract.View {

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
            allWorkScheduleCancelAdapter = AllWorkScheduleCancelAdapter()
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
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    private fun invalidateEmptyView() {
        if (allWorkScheduleCancelAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (allWorkScheduleCancelAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_add_work_item_lumpers_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_add_work_item_lumpers_info_message)
        }
    }

    private fun showConfirmationDialog(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String) {
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
        view?.let {
            when (view.id) {
                buttonSubmit.id -> {
                    val selectedLumperIdsList = allWorkScheduleCancelAdapter.getSelectedLumper()
                    val notesQHL = editTextQHLNotes.text.toString()
                    val notesCustomer = editTextCustomerNotes.text.toString()
                    showConfirmationDialog(selectedLumperIdsList, notesQHL, notesCustomer)
                }

                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(activity)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            allWorkScheduleCancelAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.employeeDataList=employeeDataList
        allWorkScheduleCancelAdapter.updateLumpersData(employeeDataList)
    }

    override fun cancellingWorkScheduleFinished() {
        setResult(RESULT_OK)
        onBackPressed()
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
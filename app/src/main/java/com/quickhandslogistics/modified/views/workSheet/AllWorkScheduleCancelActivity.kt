package com.quickhandslogistics.modified.views.workSheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.workSheet.AllWorkScheduleCancelAdapter
import com.quickhandslogistics.modified.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.workSheet.AllWorkScheduleCancelPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_all_work_schedule_cancel.*

class AllWorkScheduleCancelActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    AllWorkScheduleCancelContract.View, AllWorkScheduleCancelContract.View.OnAdapterItemClickListener {

    private lateinit var allWorkScheduleCancelPresenter: AllWorkScheduleCancelPresenter
    private lateinit var allWorkScheduleCancelAdapter: AllWorkScheduleCancelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_work_schedule_cancel)
        setupToolbar(getString(R.string.cancel_all_work_schedules))

        initializeUI()

        allWorkScheduleCancelPresenter = AllWorkScheduleCancelPresenter(this, resources)
        allWorkScheduleCancelPresenter.fetchLumpersList()
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
                textViewEmptyData.visibility = if (allWorkScheduleCancelAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        buttonSubmit.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    private fun showConfirmationDialog(selectedLumperIdsList: ArrayList<String>, notesQHL: String, notesCustomer: String) {
        CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
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
                    if (selectedLumperIdsList.size > 0) {
                        showConfirmationDialog(selectedLumperIdsList, notesQHL, notesCustomer)
                    }
                }

                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(activity)
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
        allWorkScheduleCancelAdapter.updateLumpersData(employeeDataList)
    }

    override fun cancellingWorkScheduleFinished() {
        setResult(RESULT_OK)
        onBackPressed()
    }

    /** Adapter Listeners */
    override fun onSelectLumper(totalSelectedCount: Int) {
        buttonSubmit.isEnabled = totalSelectedCount > 0
    }
}
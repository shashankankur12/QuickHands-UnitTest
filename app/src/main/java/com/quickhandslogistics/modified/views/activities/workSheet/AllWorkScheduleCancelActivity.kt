package com.quickhandslogistics.modified.views.activities.workSheet

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.AllWorkScheduleCancelContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.workSheet.AllWorkScheduleCancelPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.workSheet.AllWorkScheduleCancelAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_all_work_schedule_cancel.*

class AllWorkScheduleCancelActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    AllWorkScheduleCancelContract.View,
    AllWorkScheduleCancelContract.View.OnAdapterItemClickListener {

    private lateinit var allWorkScheduleCancelPresenter: AllWorkScheduleCancelPresenter
    private lateinit var allWorkScheduleCancelAdapter: AllWorkScheduleCancelAdapter

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_work_schedule_cancel)
        setupToolbar(getString(R.string.cancel_all_work_schedules))

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            allWorkScheduleCancelAdapter =
                AllWorkScheduleCancelAdapter(this@AllWorkScheduleCancelActivity)
            adapter = allWorkScheduleCancelAdapter
        }

        allWorkScheduleCancelAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility =
                    if (allWorkScheduleCancelAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        allWorkScheduleCancelPresenter = AllWorkScheduleCancelPresenter(this, resources, sharedPref)
        allWorkScheduleCancelPresenter.fetchLumpersList()

        buttonSubmit.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonSubmit.id -> {
                    val selectedLumperIdsList = allWorkScheduleCancelAdapter.getSelectedLumper()
                    if (selectedLumperIdsList.size > 0) {
                        onBackPressed()
/*                        allWorkScheduleCancelPresenter.initiateCancellingWorkSchedules(
                            selectedLumperIdsList
                        )*/
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

    override fun onSelectLumper(totalSelectedCount: Int) {
        buttonSubmit.isEnabled = totalSelectedCount > 0
    }

    /*
    * Presenter Listeners
    */
    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showProgressDialog(message: String) {
        progressDialog =
            CustomProgressBar.getInstance(activity).showProgressDialog(message)
    }

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
}

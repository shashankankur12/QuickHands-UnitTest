package com.quickhandslogistics.views.scheduleTime

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.scheduleTime.ChooseLumpersAdapter
import com.quickhandslogistics.contracts.scheduleTime.ChooseLumpersContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.presenters.scheduleTime.ChooseLumpersPresenter
import com.quickhandslogistics.utils.AppUtils
import com.quickhandslogistics.utils.CustomDialogWarningListener
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.common.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.content_choose_lumper.*
import java.util.*
import kotlin.collections.ArrayList

class ChooseLumpersActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    ChooseLumpersContract.View, ChooseLumpersContract.View.OnAdapterItemClickListener {

    private var selectedTime: Long = 0
    private var assignedLumpersList: ArrayList<EmployeeData> = ArrayList()
    private var scheduleTimeList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private  var employeeDataList: ArrayList<EmployeeData> =ArrayList()

    private lateinit var chooseLumpersPresenter: ChooseLumpersPresenter
    private lateinit var chooseLumpersAdapter: ChooseLumpersAdapter

    companion object {
        const val ARG_ASSIGNED_LUMPERS_LIST = "ARG_ASSIGNED_LUMPERS_LIST"
        const val CHOOSE_LUMPER_LIST = "CHOOSE_LUMPER_LIST"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_lumpers)
        setupToolbar(getString(R.string.choose_lumpers))

        intent.extras?.let { bundle ->
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
            assignedLumpersList = bundle.getParcelableArrayList<EmployeeData>(ARG_ASSIGNED_LUMPERS_LIST) as ArrayList<EmployeeData>
            scheduleTimeList = bundle.getParcelableArrayList<ScheduleTimeDetail>(ARG_SCHEDULED_TIME_LIST) as ArrayList<ScheduleTimeDetail>
        }

        initializeUI()

        chooseLumpersPresenter = ChooseLumpersPresenter(this, resources)
        savedInstanceState?.also {
            if (savedInstanceState.containsKey(CHOOSE_LUMPER_LIST)) {
                employeeDataList = savedInstanceState.getParcelableArrayList(CHOOSE_LUMPER_LIST)!!
                showLumpersData(employeeDataList)
            }
        } ?: run {
            chooseLumpersPresenter.fetchLumpersList(Date(selectedTime))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (employeeDataList != null)
            outState.putParcelableArrayList(CHOOSE_LUMPER_LIST, employeeDataList)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        chooseLumpersPresenter.onDestroy()
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            chooseLumpersAdapter = ChooseLumpersAdapter(assignedLumpersList, scheduleTimeList, this@ChooseLumpersActivity)
            adapter = chooseLumpersAdapter
        }

        chooseLumpersAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        // Enable Submit button if Lead is Updating Lumpers List
        onSelectLumper(assignedLumpersList.size)

        buttonAdd.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    private fun invalidateEmptyView() {
        if (chooseLumpersAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (chooseLumpersAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
        }
    }

    private fun showConfirmationDialog(selectedLumpersList: ArrayList<EmployeeData>) {
        CustomProgressBar.getInstance().showWarningDialog(
            getString(R.string.choose_lumper_alert_message), activity, object : CustomDialogWarningListener {
                override fun onConfirmClick() {
                    val intent = Intent().apply {
                        putExtras(Bundle().apply {
                            putParcelableArrayList(ARG_LUMPERS_LIST, selectedLumpersList)
                        })
                    }
                    setResult(RESULT_OK, intent)
                    onBackPressed()
                }

                override fun onCancelClick() {
                }
            })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAdd.id -> {
                    val selectedLumpersList = chooseLumpersAdapter.getSelectedLumpersList()
                    if (selectedLumpersList.isNotEmpty()) {
                        showConfirmationDialog(selectedLumpersList)
                    }
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
            chooseLumpersAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumpersData(employeeDataList: ArrayList<EmployeeData>) {
        this.employeeDataList=employeeDataList
        chooseLumpersAdapter.updateLumpersData(employeeDataList)
    }

    /** Adapter Listeners */
    override fun onSelectLumper(totalSelectedCount: Int) {
        buttonAdd.isEnabled = totalSelectedCount > 0
    }
}

package com.quickhandslogistics.modified.views.activities.scheduleTime

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.presenters.scheduleTime.EditScheduleTimePresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.scheduleTime.EditScheduleTimeAdapter
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleMainFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_edit_schedule_time.*
import java.util.*
import kotlin.collections.ArrayList


class EditScheduleTimeActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    EditScheduleTimeContract.View, EditScheduleTimeContract.View.OnAdapterItemClickListener {

    private var selectedTime: Long = 0
    private var assignedLumpersList: ArrayList<EmployeeData> = ArrayList()

    private lateinit var editScheduleTimePresenter: EditScheduleTimePresenter
    private lateinit var editScheduleTimeAdapter: EditScheduleTimeAdapter

    private var progressDialog: Dialog? = null

    companion object {
        const val ARG_IS_ADD_LUMPER = "ARG_IS_ADD_LUMPER"
        const val ARG_ASSIGNED_LUMPERS_LIST = "ARG_ASSIGNED_LUMPERS_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule_time)
        setupToolbar(getString(R.string.schedule_lumpers_time))

        intent.extras?.let { bundle ->
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
        }
        /*intent.extras?.let { it ->
            val isAddLumper = it.getBoolean(ARG_IS_ADD_LUMPER, true)
            if (it.containsKey(ARG_ASSIGNED_LUMPERS_LIST)) {
                assignedLumpersList =
                    it.getParcelableArrayList<EmployeeData>(ARG_ASSIGNED_LUMPERS_LIST) as ArrayList<EmployeeData>
            }
        }*/

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            editScheduleTimeAdapter = EditScheduleTimeAdapter(this@EditScheduleTimeActivity)
            adapter = editScheduleTimeAdapter
        }

        editScheduleTimeAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                textViewEmptyData.visibility =
                    if (editScheduleTimeAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        // Enable Submit button if Lead is Updating Lumpers
        onSelectLumper(assignedLumpersList.size)

        editScheduleTimePresenter = EditScheduleTimePresenter(this, resources, sharedPref)
        editScheduleTimePresenter.fetchLumpersList()

        buttonAddStartTimeAll.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonAddStartTimeAll.id -> {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = selectedTime
                    val mHour = calendar.get(Calendar.HOUR_OF_DAY)
                    val mMinute = calendar.get(Calendar.MINUTE)

                    val timePickerDialog = TimePickerDialog(
                        this,
                        OnTimeSetListener { view, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            editScheduleTimeAdapter.addStartTimetoAll(calendar.timeInMillis)
                        },
                        mHour, mMinute, false
                    )
                    timePickerDialog.show()
                }
                buttonSubmit.id -> {
                    /* val selectedLumperIdsList = addWorkItemLumperAdapter.getSelectedLumper()
                     if (selectedLumperIdsList.size > 0) {
                         addWorkItemLumpersPresenter.initiateAssigningLumpers(
                             selectedLumperIdsList, workItemId, workItemType
                         )
                     }*/
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
            editScheduleTimeAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onSelectLumper(totalSelectedCount: Int) {
        /*if (totalSelectedCount > 0) {
            buttonAdd.isEnabled = true
            buttonAdd.setBackgroundResource(R.drawable.round_button_red_new)
        } else {
            buttonAdd.isEnabled = false
            buttonAdd.setBackgroundResource(R.drawable.round_button_disabled)
        }*/
    }

    override fun onAddStartTimeClick(adapterPosition: Int, timeInMillis: Long) {
        val calendar = Calendar.getInstance()
        if (timeInMillis > 0) {
            calendar.timeInMillis = timeInMillis
        } else {
            calendar.timeInMillis = selectedTime
        }
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                editScheduleTimeAdapter.addStartTime(adapterPosition, calendar.timeInMillis)
            },
            mHour, mMinute, false
        )
        timePickerDialog.show()
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
        editScheduleTimeAdapter.updateLumpersData(employeeDataList)
        if (employeeDataList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun scheduleTimeFinished() {
        setResult(RESULT_OK)
        onBackPressed()
    }
}

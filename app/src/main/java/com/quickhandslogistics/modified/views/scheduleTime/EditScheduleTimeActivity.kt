package com.quickhandslogistics.modified.views.scheduleTime

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
import com.quickhandslogistics.modified.adapters.scheduleTime.EditScheduleTimeAdapter
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeNotes
import com.quickhandslogistics.modified.presenters.scheduleTime.EditScheduleTimePresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SCHEDULED_TIME_NOTES
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import com.quickhandslogistics.utils.ValueUtils
import kotlinx.android.synthetic.main.activity_edit_schedule_time.*
import java.util.*
import kotlin.collections.ArrayList


class EditScheduleTimeActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    EditScheduleTimeContract.View, EditScheduleTimeContract.View.OnAdapterItemClickListener {

    private var selectedTime: Long = 0
    private var scheduleTimeList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var scheduleTimeNotes: ScheduleTimeNotes? = null

    private lateinit var editScheduleTimePresenter: EditScheduleTimePresenter
    private lateinit var editScheduleTimeAdapter: EditScheduleTimeAdapter

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule_time)
        setupToolbar(getString(R.string.schedule_lumpers_time))

        intent.extras?.let { bundle ->
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
            scheduleTimeList =
                bundle.getParcelableArrayList<ScheduleTimeDetail>(ARG_SCHEDULED_TIME_LIST) as ArrayList<ScheduleTimeDetail>

            if (bundle.containsKey(ARG_SCHEDULED_TIME_NOTES)) {
                scheduleTimeNotes = bundle.getParcelable(ARG_SCHEDULED_TIME_NOTES)
            }
        }

        scheduleTimeNotes?.let { notes ->
            editTextNotes.setText(notes.notesForLead)
            editTextLumpersRequired.setText("${ValueUtils.getDefaultOrValue(notes.requestedLumperCount)}")
            editTextDMNotes.setText(notes.notesForDM)
        }

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            editScheduleTimeAdapter =
                EditScheduleTimeAdapter(scheduleTimeList, this@EditScheduleTimeActivity)
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
                    val scheduledLumpersIdsTimeMap = editScheduleTimeAdapter.getSelectedLumpers()
                    if (scheduledLumpersIdsTimeMap.size > 0) {
                        val requiredLumperCount = editTextLumpersRequired.text.toString()
                        editScheduleTimePresenter.initiateScheduleTime(
                            scheduledLumpersIdsTimeMap, editTextNotes.text.toString(),
                            if (requiredLumperCount.isNotEmpty()) requiredLumperCount.toInt() else 0,
                            editTextDMNotes.text.toString(), Date(selectedTime)
                        )
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
            editScheduleTimeAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
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

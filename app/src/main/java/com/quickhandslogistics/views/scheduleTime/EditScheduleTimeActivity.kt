package com.quickhandslogistics.views.scheduleTime

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.scheduleTime.EditScheduleTimeAdapter
import com.quickhandslogistics.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.presenters.scheduleTime.EditScheduleTimePresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.common.DisplayLumpersListActivity.Companion.ARG_LUMPERS_LIST
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_NOTES
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.activity_edit_schedule_time.*
import kotlinx.android.synthetic.main.bottom_sheet_create_lumper_request.editTextDMNotes
import kotlinx.android.synthetic.main.bottom_sheet_create_lumper_request.textViewTitle
import kotlinx.android.synthetic.main.bottom_sheet_edit_schedule_time_note.*
import kotlinx.android.synthetic.main.content_edit_schedule_time.*
import kotlinx.android.synthetic.main.content_edit_schedule_time.buttonCancelNote
import kotlinx.android.synthetic.main.content_edit_schedule_time.buttonSubmit
import java.util.*
import kotlin.collections.ArrayList

class EditScheduleTimeActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    EditScheduleTimeContract.View, EditScheduleTimeContract.View.OnAdapterItemClickListener {

    private var selectedTime: Long = 0
    private var scheduleTimeList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var scheduleTimeNotes: String? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var editScheduleTimePresenter: EditScheduleTimePresenter
    private lateinit var editScheduleTimeAdapter: EditScheduleTimeAdapter

    private var dateString: String? = null
    private var isLeavePage: Boolean = true

    companion object {
        const val SELECTED_DATE_STRING = "SELECTED_DATE_STRING"
        const val SCHEDULED_TIME_LIST = "SCHEDULED_TIME_LIST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule_time)
        setupToolbar(getString(R.string.schedule_lumpers))

        intent.extras?.let { bundle ->
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
            scheduleTimeList = bundle.getParcelableArrayList<ScheduleTimeDetail>(ARG_SCHEDULED_TIME_LIST) as ArrayList<ScheduleTimeDetail>
            scheduleTimeNotes = bundle.getString(ARG_SCHEDULED_TIME_NOTES, "")
        }

        editScheduleTimePresenter = EditScheduleTimePresenter(this, resources, sharedPref)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(SELECTED_DATE_STRING)) {
                dateString = savedInstanceState.getString(SELECTED_DATE_STRING)!!
                showDateString(dateString!!)
            }

            if (savedInstanceState.containsKey(SCHEDULED_TIME_LIST)) {
                val tempScheduleTimeList = savedInstanceState.getParcelableArrayList<ScheduleTimeDetail>(SCHEDULED_TIME_LIST) as ArrayList<ScheduleTimeDetail>
                initializeUI(tempScheduleTimeList)
            }
        } ?: run {
            editScheduleTimePresenter.getHeaderDateString(Date(selectedTime))
            initializeUI(scheduleTimeList)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == RESULT_OK) {
            data?.extras?.let { bundle ->
                val employeeDataList = bundle.getParcelableArrayList<EmployeeData>(ARG_LUMPERS_LIST)
                employeeDataList?.let {
                    editScheduleTimeAdapter.addLumpersList(employeeDataList)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (dateString != null)
            outState.putString(SELECTED_DATE_STRING, dateString)
        if (editScheduleTimeAdapter != null)
            outState.putParcelableArrayList(SCHEDULED_TIME_LIST, editScheduleTimeAdapter.getScheduledTimeList())
        super.onSaveInstanceState(outState)
    }

    private fun initializeUI(scheduleTimeList: ArrayList<ScheduleTimeDetail>) {
        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetEditSchedule)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        editTextNotes.setText(scheduleTimeNotes)

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            editScheduleTimeAdapter = EditScheduleTimeAdapter(scheduleTimeList, this@EditScheduleTimeActivity)
            adapter = editScheduleTimeAdapter
        }

        editScheduleTimeAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        addNotesTouchListener(editTextNotes)

        imageViewAddLumpers.setOnClickListener(this)
        textViewAddSameTime.setOnClickListener(this)
        buttonSubmit.setOnClickListener(this)
        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonCancelNote.setOnClickListener(this)
        bottomSheetBackgroundEditSchedule.setOnClickListener(this)
//        buttonCancel.setOnClickListener(this)

        invalidateOptionsMenu()

//        if (scheduleTimeList.size == 0) {
//            textViewEmptyData.visibility = View.VISIBLE
//            showChooseLumpersScreen()
//        } else {
//            textViewAddSameTime.visibility = View.VISIBLE
//            buttonSubmit.isEnabled = true
//        }

        textViewEmptyData.visibility = View.VISIBLE
        showChooseLumpersScreen()
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackgroundEditSchedule.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            closeBottomSheet()
        } else {
            super.onBackPressed()
        }
    }

    private fun invalidateEmptyView() {
        if (editScheduleTimeAdapter.itemCount == 0) {
            buttonSubmit.isEnabled = false
            isDataSave(true)
            textViewAddSameTime.visibility = View.GONE
            layoutEditTextNotes.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
            if (editScheduleTimeAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_edit_schedule_time_info_message)
            }
        } else {
            isDataSave (!(editScheduleTimeAdapter.getLumpersList().size > 0 && editScheduleTimeAdapter.getScheduledLumpersTimeMap().size > 0) && editScheduleTimeAdapter.getScheduledLumpersTimeMap().size == editScheduleTimeAdapter.getLumpersList().size)
            buttonSubmit.isEnabled = (editScheduleTimeAdapter.getLumpersList().size > 0 && editScheduleTimeAdapter.getScheduledLumpersTimeMap().size > 0) && editScheduleTimeAdapter.getScheduledLumpersTimeMap().size == editScheduleTimeAdapter.getLumpersList().size
            textViewAddSameTime.visibility = View.VISIBLE
            textViewEmptyData.visibility = View.GONE
            layoutEditTextNotes.visibility = View.VISIBLE

            textViewEmptyData.text = getString(R.string.empty_edit_schedule_time_info_message)
        }
    }

    private fun chooseSameTimeForAllLumpers() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedTime
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this, OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                editScheduleTimeAdapter.addStartTimetoAll(calendar.timeInMillis)
            }, mHour, mMinute, false
        ).show()
    }

    private fun showChooseLumpersScreen() {
        val lumpersList = editScheduleTimeAdapter.getLumpersList()
        val bundle = Bundle()
        bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
        bundle.putParcelableArrayList(ChooseLumpersActivity.ARG_ASSIGNED_LUMPERS_LIST, lumpersList)
        bundle.putParcelableArrayList(ARG_SCHEDULED_TIME_LIST, scheduleTimeList)
        startIntent(ChooseLumpersActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
    }

    private fun saveLumperScheduleTimings() {
        val scheduledLumpersIdsTimeMap = editScheduleTimeAdapter.getScheduledLumpersTimeMap()
        if (scheduledLumpersIdsTimeMap.size > 0) {
            val notes = editTextNotes.text.toString()
            showConfirmationDialog(scheduledLumpersIdsTimeMap, notes)
        }
    }

    private fun showConfirmationDialog(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String) {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.schedule_lumpers_alert_message), activity, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                editScheduleTimePresenter.initiateScheduleTime(scheduledLumpersIdsTimeMap, notes, Date(selectedTime))
            }

            override fun onCancelClick() {
            }
        })
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun showSuccessDialog(message: String, position: Int) {
        setResult(RESULT_OK)
        scheduleTimeList.removeAt(position)
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewAddLumpers.id -> showChooseLumpersScreen()
                bottomSheetBackgroundEditSchedule.id -> closeBottomSheet()
//                buttonCancel.id -> closeBottomSheet()
                textViewAddSameTime.id -> chooseSameTimeForAllLumpers()
                buttonCancelNote.id -> onBackPressed()
                buttonSubmit.id -> saveLumperScheduleTimings()
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
            editScheduleTimeAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showLeavePopup() {
        CustomProgressBar.getInstance().showLeaveDialog(
            getString(R.string.discard_leave_alert_message),
            activity,
            object : CustomDialogWarningListener {
                override fun onConfirmClick() {

                }
                override fun onCancelClick() {
                }
            })
    }


    private fun showBottomSheetWithData(record: RequestLumpersRecord? = null) {
        record?.also {
            textViewTitle.text = getString(R.string.update_request)
            val requestedLumpersCount = ValueUtils.getDefaultOrValue(record.requestedLumpersCount)
            editTextDMNotes.setText(record.notesForDM)
            buttonSubmit.setTag(R.id.requestLumperId, record.id)
        } ?: run {
            textViewTitle.text = getString(R.string.add_notes)
            editTextDMNotes.setText("")
            buttonSubmit.setTag(R.id.requestLumperId, "")
        }

        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackgroundEditSchedule.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    private fun showDeleteDialog(adapterPosition: Int, item: ScheduleTimeDetail) {
//        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.cancel_request_lumper), this, object : CustomDialogWarningListener {
//            override fun onConfirmClick() {
                scheduleTimeList.forEach {
                    if (it.lumperInfo?.id!!.equals(item.lumperInfo?.id!!)){
                        removeFromList(scheduleTimeList.indexOf(it), item)
                    }
                }
                editScheduleTimeAdapter.removeLumpersInList(adapterPosition, item)

//            }
//
//            override fun onCancelClick() {
//            }
//        })
    }

    private fun removeFromList(adapterPosition: Int, item: ScheduleTimeDetail) {
        var scheduleTimeDetail= item
        editScheduleTimePresenter.cancelScheduleLumpers(scheduleTimeDetail.lumperInfo?.id!!,DateUtils.getDateFromDateString(DateUtils.PATTERN_API_RESPONSE, scheduleTimeDetail.reportingTimeAndDay),adapterPosition)
    }

    /** Presenter Listeners */
    override fun showDateString(dateString: String) {
        this.dateString = dateString
        textViewDate.text = UIUtils.getSpannedText(dateString)
    }

    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun scheduleTimeFinished() {
        setResult(RESULT_OK)
        isDataSave(true)
        onBackPressed()
    }

    /** Adapter Listeners */
    override fun onAddStartTimeClick(adapterPosition: Int, timeInMillis: Long) {
        val calendar = Calendar.getInstance()
        if (timeInMillis > 0) {
            calendar.timeInMillis = timeInMillis
        } else {
            calendar.timeInMillis = selectedTime
        }
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            this, OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                editScheduleTimeAdapter.addStartTime(adapterPosition, calendar.timeInMillis)
            }, mHour, mMinute, false
        ).show()
    }

    override fun onAddScheduleNoteClick(adapterPosition: Int) {
        showBottomSheetWithData()
    }

    override fun onAddRemoveClick(adapterPosition: Int, item: ScheduleTimeDetail) {
        showDeleteDialog(adapterPosition , item)
    }
}
package com.quickhandslogistics.views.workSheet

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.AddLumperTimeWorkSheetItemContract
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.presenters.workSheet.AddLumperTimeWorkSheetItemPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.isFutureTime
import com.quickhandslogistics.utils.ScheduleUtils.calculatePercent
import com.quickhandslogistics.utils.ValueUtils.isNumeric
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_TIMING_DATA
import com.quickhandslogistics.views.schedule.ScheduleFragment
import kotlinx.android.synthetic.main.content_add_lumper_time_work_sheet_item.*
import java.util.*

class AddLumperTimeWorkSheetItemActivity : BaseActivity(), View.OnClickListener, AddLumperTimeWorkSheetItemContract.View,
    TextWatcher {

    private var workItemId = ""
    private var totalCases :String = ""
    private var employeeData: EmployeeData? = null
    private var employeeTimingData: LumpersTimeSchedule? = null

    private var initialStartTime: Long = 0
    private var initialEndTime: Long = 0
    private var initialBreakInTime: Long = 0
    private var initialBreakOutTime: Long = 0

    private var selectedStartTime: Long = 0
    private var selectedEndTime: Long = 0
    private var selectedBreakInTime: Long = 0
    private var selectedBreakOutTime: Long = 0
    private var percentageTime: Double = 0.0
    private var partWorkDone: Int = 0
    private var isPartWorkDoneValid: Boolean = true

    private lateinit var addLumperTimeWorkSheetItemPresenter: AddLumperTimeWorkSheetItemPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lumper_time_work_sheet_item)
        setupToolbar(getString(R.string.add_time))

        intent.extras?.let { it ->
            if (it.containsKey(ARG_LUMPER_DATA)) {
                workItemId = it.getString(ScheduleFragment.ARG_WORK_ITEM_ID, "")
                totalCases = it.getString(WorkSheetItemDetailLumpersFragment.TOTAL_CASES, "")
                employeeData = it.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
                employeeTimingData = it.getParcelable(ARG_LUMPER_TIMING_DATA) as LumpersTimeSchedule?
            }
        }

        initializeUI()

        addLumperTimeWorkSheetItemPresenter = AddLumperTimeWorkSheetItemPresenter(this, resources)
    }

    private fun initializeUI() {
        employeeData?.let { employeeData ->
            UIUtils.showEmployeeProfileImage(activity, employeeData, circleImageViewProfile)
            UIUtils.updateProfileBorder(activity, employeeData.isTemporaryAssigned, circleImageViewProfile)
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)
        }

        employeeTimingData?.let { timingDetail ->
            val waitingTime = ValueUtils.getDefaultOrValue(timingDetail.waitingTime)
            if (waitingTime.isNotEmpty() && waitingTime.toInt() != 0) {
                editTextWaitingTime.setText(waitingTime)
                editTextWaitingTime.isEnabled = false
            }
            if(!timingDetail.partWorkDone.isNullOrEmpty() && timingDetail.partWorkDone!!.toInt()!=0){
                partWorkDone= timingDetail.partWorkDone!!.toInt()
                lumpercaseVisibility()
                editTextCasesLumpers.setText(partWorkDone.toString())
            }

            updateTimingsDetails(timingDetail)
        }
        if (!totalCases.isNullOrEmpty()&& isNumeric(totalCases)){
            editTextTotalCases.setText(totalCases)
            editTextTotalCases.isEnabled=false
            editTextCasesLumpers.addTextChangedListener(this)
            if (partWorkDone>0){
                getPercent(partWorkDone.toString(), totalCases)
            }

        }else{
            editTextTotalCases.isEnabled=false
            editTextCasesLumpers.isEnabled=false
        }
        updateButtonsUI()
        if(!totalCases.isNullOrEmpty() && partWorkDone!=0) lumpercaseVisibility()
        toggleSaveButtonVisibility()


        buttonStartTime.setOnClickListener(this)
        buttonEndTime.setOnClickListener(this)
        buttonBreakInTime.setOnClickListener(this)
        buttonBreakOutTime.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
    }

    private fun updateTimingsDetails(timingDetail: LumpersTimeSchedule) {
        initialStartTime = updateInitialTime(timingDetail.startTime, buttonStartTime)
        initialEndTime = updateInitialTime(timingDetail.endTime, buttonEndTime)
        initialBreakInTime = updateInitialTime(timingDetail.breakTimeStart, buttonBreakInTime)
        initialBreakOutTime = updateInitialTime(timingDetail.breakTimeEnd, buttonBreakOutTime)

        selectedStartTime = initialStartTime
        selectedEndTime = initialEndTime
        selectedBreakInTime = initialBreakInTime
        selectedBreakOutTime = initialBreakOutTime
    }

    private fun updateButtonsUI() {
        buttonStartTime.isEnabled = initialStartTime <= 0
        buttonEndTime.isEnabled = initialEndTime <= 0 && selectedStartTime > 0 && checkBrackout()
        buttonBreakInTime.isEnabled = initialBreakInTime <= 0 && selectedStartTime > 0
        buttonBreakOutTime.isEnabled = initialBreakOutTime <= 0 && selectedBreakInTime > 0
    }

    private fun checkBrackout(): Boolean {
        if (selectedBreakInTime>0&& selectedBreakOutTime<=0)
            return false
        return true
    }

    private fun toggleSaveButtonVisibility() {
        buttonSave.isEnabled = editTextWaitingTime.isEnabled || buttonStartTime.isEnabled || buttonEndTime.isEnabled || buttonBreakInTime.isEnabled || buttonBreakOutTime.isEnabled || editTextCasesLumpers.isEnabled
    }

    private fun lumpercaseVisibility() {
        editTextCasesLumpers.isEnabled = editTextWaitingTime.isEnabled || buttonStartTime.isEnabled || buttonEndTime.isEnabled || buttonBreakInTime.isEnabled || buttonBreakOutTime.isEnabled
    }

    private fun updateInitialTime(dateStamp: String?, buttonTime: Button): Long {
        var milliseconds: Long = 0

        val time = DateUtils.convertDateStringToTime(PATTERN_API_RESPONSE, dateStamp)
        if (time.isNotEmpty()) {
            buttonTime.text = time
            buttonTime.isEnabled = false
            val currentDateString = DateUtils.convertUTCDateStringToLocalDateString(PATTERN_API_RESPONSE, dateStamp)
            milliseconds = DateUtils.getMillisecondsFromDateString(PATTERN_API_RESPONSE, currentDateString)
        }

        return milliseconds
    }

    private fun onSelectStartTime(calendar: Calendar) {
        val selectedStartTime = calendar.timeInMillis
        if (selectedEndTime > 0) {
            if (!isFutureTime(selectedStartTime, selectedEndTime)) {
                showErrorDialog(getString(R.string.work_start_less_work_end_warning_message))
                return
            }
        }
        this.selectedStartTime = selectedStartTime
        buttonStartTime.text = DateUtils.convertMillisecondsToTimeString(selectedStartTime)
        updateButtonsUI()
    }

    private fun onSelectEndTime(calendar: Calendar) {
        val selectedEndTime = calendar.timeInMillis
        if (!isFutureTime(selectedStartTime, selectedEndTime)) {
            showErrorDialog(getString(R.string.work_end_greater_work_start_warning_message))
        } else if (selectedBreakInTime > 0 && !isFutureTime(selectedBreakInTime, selectedEndTime)) {
            showErrorDialog(getString(R.string.work_end_greater_break_start_warning_message))
        } else {
            this.selectedEndTime = selectedEndTime
            buttonEndTime.text = DateUtils.convertMillisecondsToTimeString(selectedEndTime)
            updateButtonsUI()
        }
    }

    private fun onSelectBreakInTime(calendar: Calendar) {
        val selectedBreakInTime = calendar.timeInMillis
        if (selectedBreakOutTime > 0) {
            if (!isFutureTime(selectedStartTime, selectedBreakInTime)) {
                showErrorDialog(getString(R.string.break_start_greater_work_start_warning_message))
                return
            } else if (!isFutureTime(selectedBreakInTime, selectedBreakOutTime)) {
                showErrorDialog(getString(R.string.break_start_less_break_end_warning_message))
                return
            } else if (selectedEndTime > 0 && !isFutureTime(selectedBreakInTime, selectedEndTime)) {
                showErrorDialog(getString(R.string.break_start_less_work_end_warning_message))
                return
            }
        }
        this.selectedBreakInTime = selectedBreakInTime
        buttonBreakInTime.text = DateUtils.convertMillisecondsToTimeString(selectedBreakInTime)
        updateButtonsUI()
    }

    private fun onSelectBreakOutTime(calendar: Calendar) {
        val selectedBreakOutTime = calendar.timeInMillis
        if (!isFutureTime(selectedStartTime, selectedBreakInTime)) {
            showErrorDialog(getString(R.string.break_end_greater_work_start_warning_message))
        } else if (!isFutureTime(selectedBreakInTime, selectedBreakOutTime)) {
            showErrorDialog(getString(R.string.break_end_greater_break_start_warning_message))
        } else if (selectedEndTime > 0 && !isFutureTime(selectedBreakOutTime, selectedEndTime)) {
            showErrorDialog(getString(R.string.break_end_less_work_end_warning_message))
        } else {
            this.selectedBreakOutTime = selectedBreakOutTime
            buttonBreakOutTime.text = DateUtils.convertMillisecondsToTimeString(selectedBreakOutTime)
            updateButtonsUI()
        }
    }

    private fun chooseTime(listener: OnTimeSetListener) {
        val calendar = Calendar.getInstance()
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                listener.onSelectTime(calendar)
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    private fun saveSelectedTimings() {
        CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                val waitingTime = editTextWaitingTime.text.toString()

                addLumperTimeWorkSheetItemPresenter.saveLumperTimings(
                    employeeData?.id!!, workItemId, selectedStartTime, selectedEndTime,
                    selectedBreakInTime, selectedBreakOutTime, waitingTime,partWorkDone
                )
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                buttonStartTime.id -> {
                    chooseTime(object : OnTimeSetListener {
                        override fun onSelectTime(calendar: Calendar) {
                            onSelectStartTime(calendar)
                        }
                    })
                }
                buttonEndTime.id -> {
                    chooseTime(object : OnTimeSetListener {
                        override fun onSelectTime(calendar: Calendar) {
                            onSelectEndTime(calendar)
                        }
                    })
                }
                buttonBreakInTime.id -> {
                    chooseTime(object : OnTimeSetListener {
                        override fun onSelectTime(calendar: Calendar) {
                            onSelectBreakInTime(calendar)
                        }
                    })
                }
                buttonBreakOutTime.id -> {
                    chooseTime(object : OnTimeSetListener {
                        override fun onSelectTime(calendar: Calendar) {
                            onSelectBreakOutTime(calendar)
                        }
                    })
                }
                buttonSave.id -> {
                    if (!isPartWorkDoneValid){
                        CustomProgressBar.getInstance().showMessageDialog(getString(R.string.lumper_cases_error) , activity)
                    }else saveSelectedTimings()
                }
            }
        }
    }



    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun lumpersTimingSaved() {
        setResult(RESULT_OK)
        onBackPressed()
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    interface OnTimeSetListener {
        fun onSelectTime(calendar: Calendar)
    }

    override fun afterTextChanged(s: Editable?) {    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        text?.let {
            if (!text.isNullOrEmpty() && (text.toString()).toInt() != 0) {
                getPercent(text.toString(), totalCases)
            } else {
                percentWorkDone.text = "0.0%"
                partWorkDone=0
            }
        }
    }

    private fun getPercent(lumperCase: String, totalCases: String) {
        if (lumperCase.toDouble() <= totalCases.toDouble()) {
            partWorkDone = lumperCase.toInt()
            percentageTime = calculatePercent(lumperCase, totalCases)
            percentWorkDone.text = String.format("%.2f", percentageTime) + "%"
            isPartWorkDoneValid= true
        } else {
            isPartWorkDoneValid= false
            CustomProgressBar.getInstance().showMessageDialog(getString(R.string.lumper_cases_error) , activity)
            percentWorkDone.text = "0.0%"
        }

    }
}
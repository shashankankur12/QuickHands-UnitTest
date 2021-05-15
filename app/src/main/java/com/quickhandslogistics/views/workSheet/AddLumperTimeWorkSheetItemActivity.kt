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
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.PauseTime
import com.quickhandslogistics.data.workSheet.PauseTimeRequest
import com.quickhandslogistics.presenters.workSheet.AddLumperTimeWorkSheetItemPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.isFutureTime
import com.quickhandslogistics.utils.ScheduleUtils.calculatePercent
import com.quickhandslogistics.utils.ValueUtils.isNumeric
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_DATA
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_PRESENT
import com.quickhandslogistics.views.lumpers.LumperDetailActivity.Companion.ARG_LUMPER_TIMING_DATA
import com.quickhandslogistics.views.schedule.ScheduleFragment
import kotlinx.android.synthetic.main.content_add_lumper_time_work_sheet_item.*
import java.util.*
import kotlin.collections.ArrayList

class AddLumperTimeWorkSheetItemActivity : BaseActivity(), View.OnClickListener,
    AddLumperTimeWorkSheetItemContract.View,
    TextWatcher {

    private var workItemId = ""
    private var totalCases: String = ""
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
    private var isLumperPresent: Boolean = false
    private var isTimeSelected: Boolean = false
    private lateinit var watcher: TextWatcher
    private var selectedTime: Long = 0
    private var tempLumperIds: ArrayList<String> = ArrayList()

    private lateinit var addLumperTimeWorkSheetItemPresenter: AddLumperTimeWorkSheetItemPresenter
    private val mPauseTimeRequestList: ArrayList<PauseTimeRequest> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lumper_time_work_sheet_item)
        setupToolbar(getString(R.string.lumper_parameter))

        intent.extras?.let { it ->
            if (it.containsKey(ARG_LUMPER_DATA)) {
                workItemId = it.getString(ScheduleFragment.ARG_WORK_ITEM_ID, "")
                isLumperPresent = it.getBoolean(ARG_LUMPER_PRESENT, false)
                totalCases = it.getString(WorkSheetItemDetailLumpersFragment.TOTAL_CASES, "")
                employeeData = it.getParcelable(ARG_LUMPER_DATA) as EmployeeData?
                employeeTimingData =
                    it.getParcelable(ARG_LUMPER_TIMING_DATA) as LumpersTimeSchedule?
                tempLumperIds =
                    it.getStringArrayList(WorkSheetItemDetailLumpersFragment.TEMP_LUMPER_IDS) as ArrayList<String>
                selectedTime = it.getLong(WorkSheetItemDetailLumpersFragment.SCHEDULE_SELECTED_TIME)
            }
        }

        initializeUI()

        addLumperTimeWorkSheetItemPresenter = AddLumperTimeWorkSheetItemPresenter(this, resources)
    }

    private fun initializeUI() {
        waitingTimeDisableInPastDat()
        employeeData?.let { employeeData ->
            val leadProfile = DateUtils.sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            var buildingId = ""
            leadProfile?.buildingDetailData?.get(0)?.id?.let { id ->
                buildingId = id
            }

            UIUtils.showEmployeeProfileImage(activity, employeeData, circleImageViewProfile)
            UIUtils.updateProfileBorder(
                activity,
                buildingId != employeeData.buildingIdAsLumper,
                circleImageViewProfile
            )
            textViewLumperName.text = UIUtils.getEmployeeFullName(employeeData)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData)
            viewAttendanceStatus.setBackgroundResource(if (isLumperPresent) R.drawable.online_dot else R.drawable.offline_dot)

        }

        employeeTimingData?.let { timingDetail ->
            val waitingTimeHours = ValueUtils.getHoursFromMinutes(timingDetail.waitingTime)
            val waitingTimeMinutes = ValueUtils.getRemainingMinutes(timingDetail.waitingTime)
            if (waitingTimeHours.isNotEmpty() /*&& (0.0 != waitingTimeHours.toDouble() || 0.0 != waitingTimeMinutes.toDouble())*/) {
                editTextWaitingTime.setText(waitingTimeHours)
//                editTextWaitingTime.isEnabled = false
            }
            if (waitingTimeMinutes.isNotEmpty() /*&& (0.0 != waitingTimeMinutes.toDouble() || 0.0 != waitingTimeHours.toDouble())*/) {
                editTextWaitingTimeMinutes.setText(waitingTimeMinutes)
//                editTextWaitingTimeMinutes.isEnabled = false
            }
            if (!timingDetail.partWorkDone.isNullOrEmpty() && timingDetail.partWorkDone!!.toInt() != 0) {
                partWorkDone = timingDetail.partWorkDone!!.toInt()
                lumpercaseVisibility()
                editTextCasesLumpers.setText(partWorkDone.toString())
            }

            updateTimingsDetails(timingDetail)
        }
        if (totalCases.isNotEmpty() && isNumeric(totalCases)) {
            editTextTotalCases.setText(totalCases)
            editTextTotalCases.isEnabled = false
            editTextCasesLumpers.addTextChangedListener(this)
            if (partWorkDone > 0) {
                getPercent(partWorkDone.toString(), totalCases)
            }

        } else {
            editTextTotalCases.isEnabled = false
            editTextCasesLumpers.isEnabled = false
        }

        editTextWaitingTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val inputHour = editable?.toString()
                if (!inputHour.isNullOrBlank()) {
                    if (24 <= inputHour.toInt()) {
                        editTextWaitingTime.text = null
                        editTextWaitingTime.error = getString(R.string.invalid_hour_message)
                        editTextWaitingTime.requestFocus()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isDataSave(text.isNullOrEmpty())
            }
        })

        editTextWaitingTimeMinutes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val inputMinutes = editable?.toString()
                if (!inputMinutes.isNullOrEmpty()) {
                    if (60 <= inputMinutes.toInt()) {
                        editTextWaitingTimeMinutes.text = null
                        editTextWaitingTimeMinutes.error = getString(R.string.invalid_hour_minutes)
                        editTextWaitingTimeMinutes.requestFocus()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isDataSave(text.isNullOrEmpty())
            }
        })

        updateButtonsUI()
        if (totalCases.isNotEmpty() && partWorkDone != 0) lumpercaseVisibility()
        toggleSaveButtonVisibility()
        buttonStartTime.setOnClickListener(this)
        buttonEndTime.setOnClickListener(this)
        buttonBreakInTime.setOnClickListener(this)
        buttonBreakOutTime.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
        buttonCancelRequest.setOnClickListener(this)
    }

    private fun updateTimingsDetails(timingDetail: LumpersTimeSchedule) {
        initialStartTime = updateInitialTime(timingDetail.startTime, buttonStartTime)
        initialEndTime = updateInitialTime(timingDetail.endTime, buttonEndTime)
//        initialBreakInTime = updateInitialTime(timingDetail.breakTimeStart, buttonBreakInTime)
//        initialBreakOutTime = updateInitialTime(timingDetail.breakTimeEnd, buttonBreakOutTime)

        timingDetail.breakTimes?.let {
            if (it.isNotEmpty()) {
                val lastItem = it[it.lastIndex]
                initialBreakInTime = updateInitialTime(lastItem.startTime, buttonBreakInTime)
                initialBreakOutTime = updateInitialTime(lastItem.endTime, buttonBreakOutTime)
            }
            getBreakTimeList(it)
        }

        selectedStartTime = initialStartTime
        selectedEndTime = initialEndTime
        selectedBreakInTime = initialBreakInTime
        selectedBreakOutTime = initialBreakOutTime
        updatePauseTimeLayout()
    }

    private fun getBreakTimeList(arrayList: ArrayList<PauseTime>) {
        arrayList.forEach {
            val breakTime = PauseTimeRequest()
            breakTime.startTime = updateInitialTime(it.startTime, buttonBreakInTime)
            breakTime.endTime = updateInitialTime(it.endTime, buttonBreakInTime)
            mPauseTimeRequestList.add(breakTime)
            isTimeSelected= false
        }
    }

    private fun updatePauseTimeLayout() {
        if (0L != initialBreakInTime && 0L != initialBreakOutTime) {
            buttonBreakInTime.text = getString(R.string.pause)
            buttonBreakOutTime.text = getString(R.string.resume)
            mPauseTimeRequestList.run {
//                mPauseTimeRequestList.clear()
                getPauseTimeCalculate()
            }
        }
    }

    private fun updateButtonsUI() {
//        if (initialStartTime <= 0 ) {
//            buttonStartTime.isEnabled = initialStartTime <= 0
//            buttonEndTime.isEnabled =
//                initialEndTime <= 0 && selectedStartTime > 0 && checkBrackout()
//            buttonBreakInTime.isEnabled = initialBreakInTime <= 0 && selectedStartTime > 0
//            buttonBreakOutTime.isEnabled = initialBreakOutTime <= 0 && selectedBreakInTime > 0
//        }
        buttonEndTime.isEnabled=selectedStartTime>0 && checkBrackout()
        buttonBreakOutTime.isEnabled = initialBreakInTime>0 || selectedBreakInTime>0
        getTimeCalculate()
    }

    private fun getPauseTimeCalculate() {
        val timeRequest = PauseTimeRequest()
        if (selectedBreakInTime > 0 && selectedBreakOutTime == 0L) {
            timeRequest.startTime = selectedBreakInTime
            for (it in mPauseTimeRequestList) {
                if (isTimeSelected)
                    if (it.startTime!! <= selectedBreakInTime && selectedBreakInTime < it.endTime!!) {
                        showErrorDialog("brake in time already available in time slot ")
                        return
                    }
                if (it.startTime != null && it.endTime == null) {
                    mPauseTimeRequestList.remove(it)
                }
            }
            mPauseTimeRequestList.add(timeRequest)
        } else if (selectedBreakOutTime > 0 && selectedBreakInTime == 0L) {
            timeRequest.endTime = selectedBreakOutTime
            for (it in mPauseTimeRequestList) {
                if (isTimeSelected)
                  if (it.startTime!! <= selectedBreakOutTime && selectedBreakOutTime < it.endTime!!) {
                        showErrorDialog("brake out time already available in time slot ")
                        return
                    }
                if (it.endTime != null && it.startTime == null) {
                    mPauseTimeRequestList.remove(it)
                }
            }
            mPauseTimeRequestList.add(timeRequest)
        } else if (selectedBreakInTime > 0 && selectedBreakOutTime > 0) {
            for (it in mPauseTimeRequestList) {
                if (it.endTime == null || it.startTime == null) {
                    mPauseTimeRequestList.remove(it)
                } else {
                    if (isTimeSelected)
                        if ((it.startTime!! <= selectedBreakInTime && selectedBreakOutTime < it.endTime!!)) {
                            showErrorDialog("break in/out overriding in previous slot ")
                            return
                        } else if (it.startTime!! <= selectedBreakInTime && selectedBreakInTime < it.endTime!!) {
                            showErrorDialog("brake in time already available in time slot ")
                            return
                        } else if (it.startTime!! <= selectedBreakOutTime && selectedBreakOutTime < it.endTime!!) {
                            showErrorDialog("brake out time already available in time slot ")
                            return
                        }

                    if (it.endTime == 0L || it.startTime == 0L) {
                        mPauseTimeRequestList.remove(it)
                    } else if (it.startTime!! == selectedBreakInTime && it.endTime!! == selectedBreakOutTime) {
                        mPauseTimeRequestList.remove(it)
                    }
                }
            }

            timeRequest.endTime = selectedBreakOutTime
            timeRequest.startTime = selectedBreakInTime
            mPauseTimeRequestList.add(timeRequest)

        }

        if (selectedBreakInTime > 0 && selectedBreakOutTime > 0) {
            totalPauseTime.visibility = View.VISIBLE
//            mPauseTimeRequestList.add(PauseTimeRequest(selectedBreakInTime,selectedBreakOutTime))
            showPauseTimeDuration()
            var dateTime: Long = 0
            for (pauseTime in mPauseTimeRequestList) {
                dateTime += (pauseTime.endTime!! - pauseTime.startTime!!)
            }
            totalPauseTime.text =
                getString(R.string.total_pause_colon) + DateUtils.getDateTimeCalculatedLong(dateTime)
        } else totalPauseTime.visibility = View.GONE
    }

    private fun showPauseTimeDuration() {
        totalPauseTimeDurationTextView?.visibility = View.VISIBLE
        totalPauseTimeDurationTextView?.text = null
        mPauseTimeRequestList.forEachIndexed { index, pauseTime ->
            val pauseTimeIn = pauseTime.startTime
            val pauseTimeOut = pauseTime.endTime
            val pauseTimeInStr = DateUtils.convertMillisecondsToTimeString(pauseTimeIn?.toLong()!!)
            val pauseTimeOutStr =
                DateUtils.convertMillisecondsToTimeString(pauseTimeOut?.toLong()!!)
            if (index != 0) totalPauseTimeDurationTextView?.append("\n")
            totalPauseTimeDurationTextView?.append("$pauseTimeInStr - $pauseTimeOutStr")
        }
        buttonBreakInTime.isEnabled = true
//        buttonBreakOutTime.isEnabled = false
        buttonBreakInTime.text = getString(R.string.pause)
    }

    private fun getTimeCalculate() {
        if (selectedStartTime > 0 && selectedEndTime > 0) {
            totalWorkTime.visibility = View.VISIBLE
            val dateString = String.format(
                getString(R.string.total_time),
                DateUtils.getDateTimeCalculatedLong(selectedStartTime, selectedEndTime)
            )
            totalWorkTime.text = dateString
        } else totalWorkTime.visibility = View.GONE
    }

    private fun checkBrackout(): Boolean {
        if (selectedBreakInTime > 0 && selectedBreakOutTime <= 0)
            return false
        return true
    }

    private fun toggleSaveButtonVisibility() {
        buttonSave.isEnabled =
            editTextWaitingTime.isEnabled || editTextWaitingTimeMinutes.isEnabled || buttonStartTime.isEnabled || buttonEndTime.isEnabled || buttonBreakInTime.isEnabled || buttonBreakOutTime.isEnabled || editTextCasesLumpers.isEnabled
    }

    private fun lumpercaseVisibility() {
        editTextCasesLumpers.isEnabled =
            editTextWaitingTime.isEnabled || editTextWaitingTimeMinutes.isEnabled || buttonStartTime.isEnabled || buttonEndTime.isEnabled || buttonBreakInTime.isEnabled || buttonBreakOutTime.isEnabled
    }

    private fun waitingTimeDisableInPastDat() {
        editTextWaitingTime.isEnabled =
            (DateUtils.isCurrentDate(selectedTime) || DateUtils.isFutureDate(selectedTime))
        editTextWaitingTimeMinutes.isEnabled =
            (DateUtils.isCurrentDate(selectedTime) || DateUtils.isFutureDate(selectedTime))

    }

    private fun updateInitialTime(dateStamp: String?, buttonTime: Button): Long {
        var milliseconds: Long = 0
        val time = DateUtils.convertDateStringToTime(PATTERN_API_RESPONSE, dateStamp)
        if (time.isNotEmpty()) {
            buttonTime.text = time
//            buttonTime.isEnabled = false
            val currentDateString =
                DateUtils.convertUTCDateStringToLocalDateString(PATTERN_API_RESPONSE, dateStamp)
            milliseconds =
                DateUtils.getMillisecondsFromDateString(PATTERN_API_RESPONSE, currentDateString)
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
        }else if (selectedBreakInTime>0){
            if (!isFutureTime(selectedStartTime, selectedBreakInTime)) {
                showErrorDialog(getString(R.string.work_start_less_work_break_warning_message))
                return
            }
        }
        this.selectedStartTime = selectedStartTime
        buttonStartTime.text = DateUtils.convertMillisecondsToTimeString(selectedStartTime)
        updateButtonsUI()
        isDataSave(false)
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
            buttonBreakInTime.isEnabled = true
//            buttonBreakOutTime.isEnabled = false
            buttonBreakInTime.text = getString(R.string.pause)
            isDataSave(false)
        }
    }

    private fun onSelectBreakInTime(calendar: Calendar) {
        val selectedBreakInTime = calendar.timeInMillis
        if (selectedEndTime > 0) {
            if (!isFutureTime(selectedBreakInTime, selectedEndTime)) {
                showErrorDialog(getString(R.string.break_start_less_end_warning_message))
                return
            }
        }else if (selectedStartTime > 0) {
            if (isFutureTime(selectedBreakInTime, selectedStartTime)) {
                showErrorDialog(getString(R.string.break_start_greater_start_warning_message))
                return
            }
        }

        isTimeSelected=true
        selectedBreakOutTime = 0
        buttonBreakOutTime.text = getString(R.string.resume)
        this.selectedBreakInTime = selectedBreakInTime
        buttonBreakInTime.text = DateUtils.convertMillisecondsToTimeString(selectedBreakInTime)
        updateButtonsUI()
        getPauseTimeCalculate()
        isDataSave(false)
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
            isTimeSelected=true
            this.selectedBreakOutTime = selectedBreakOutTime
            buttonBreakOutTime.text =
                DateUtils.convertMillisecondsToTimeString(selectedBreakOutTime)
            updateButtonsUI()
            getPauseTimeCalculate()
            isDataSave(false)
        }
    }

    private fun chooseTime(listener: OnTimeSetListener) {
        val calendar = Calendar.getInstance()
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                listener.onSelectTime(calendar)
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    private fun saveSelectedTimings() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        val breakTimeList = mPauseTimeRequestList
        val waitingTimeHours = editTextWaitingTime?.text.toString()
        val waitingTimeMinutes = editTextWaitingTimeMinutes?.text.toString()
        val waitingTime =
            ((if (waitingTimeHours.isEmpty()) 0 else waitingTimeHours.toInt() * 60) + if (waitingTimeMinutes.isEmpty()) 0 else waitingTimeMinutes.toInt()).toString()

        addLumperTimeWorkSheetItemPresenter.saveLumperTimings(
            employeeData?.id!!,
            workItemId,
            selectedStartTime,
            selectedEndTime,
            breakTimeList,
            waitingTime.toString(),
            partWorkDone
        )
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

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
                    if (!isPartWorkDoneValid) {
                        CustomProgressBar.getInstance()
                            .showMessageDialog(getString(R.string.lumper_cases_error), activity)
                    } else saveSelectedTimings()
                }
                buttonCancelRequest.id -> {
                    onBackPressed()
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
        isDataSave(true)
        onBackPressed()
    }

    override fun showLoginScreen() {
        startIntent(
            LoginActivity::class.java,
            isFinish = true,
            flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    interface OnTimeSetListener {
        fun onSelectTime(calendar: Calendar)
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        text?.let {
            if (!text.isNullOrEmpty() && (text.toString()).toInt() != 0) {
                getPercent(text.toString(), totalCases)
                isDataSave(false)
            } else {
                percentWorkDone.text = "0.0%"
                partWorkDone = 0
                isDataSave(true)
            }
        }
    }

    private fun getPercent(lumperCase: String, totalCases: String) {
        if (lumperCase.toDouble() <= totalCases.toDouble()) {
            partWorkDone = lumperCase.toInt()
            percentageTime = calculatePercent(lumperCase, totalCases)
            percentWorkDone.text = String.format("%.2f", percentageTime) + "%"
            isPartWorkDoneValid = true
        } else {
            isPartWorkDoneValid = false
            CustomProgressBar.getInstance()
                .showMessageDialog(getString(R.string.lumper_cases_error), activity)
            percentWorkDone.text = "0.0%"
        }

    }
}
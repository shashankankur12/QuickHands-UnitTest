package com.quickhandslogistics.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.lumperSheet.CorrectionBuildingOpsAdapter
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.lumperSheet.LumperCorrectionRequest
import com.quickhandslogistics.data.lumperSheet.LumperParameterCorrection
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule
import com.quickhandslogistics.data.workSheet.PauseTime
import com.quickhandslogistics.data.workSheet.PauseTimeRequest
import kotlinx.android.synthetic.main.new_bottum_sheet_request_correction.*
import java.util.*
import kotlin.collections.ArrayList

class CorrectionRequestBottomSheet {
    private var totalCases: String = ""
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
    private var isTimeSelected: Boolean = false
    private val mPauseTimeRequestList: ArrayList<PauseTimeRequest> = ArrayList()
    private var buildingOperationsAdapter: CorrectionBuildingOpsAdapter? =null

    companion object {
        private var correctionRequestBottomSheet: CorrectionRequestBottomSheet? = null

        fun getInstance(): CorrectionRequestBottomSheet {
            if (correctionRequestBottomSheet == null) {
                correctionRequestBottomSheet = CorrectionRequestBottomSheet()
            }
            return correctionRequestBottomSheet as CorrectionRequestBottomSheet
        }
    }

    fun newRequestCorrectionBottomSheetDialog(lumperTimeDetails: LumpersTimeSchedule?, buildingOps: HashMap<String, String>?, qhlNote: String?, correctionNote: String?, buildingParams: ArrayList<String>?, containerId: String?, lumperId: String?, isCompleted: Boolean?, context: Context, contractView: IDialogRequestCorrectionClick) {

        mPauseTimeRequestList.clear()
        val unFinishedBottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        unFinishedBottomSheet.setContentView(R.layout.new_bottum_sheet_request_correction)

        buildingParams?.let {
            unFinishedBottomSheet.recyclerViewBuildingOperations.apply {
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(SpaceDividerItemDecorator(20, 20))
                buildingOperationsAdapter = CorrectionBuildingOpsAdapter(ScheduleUtils.sortAccordingly(it), context)
                adapter = buildingOperationsAdapter
            }

        }
        buildingOps?.let {
            if (buildingOperationsAdapter != null) {
                buildingOperationsAdapter?.updateData(it, isCompleted)
            }
            totalCases=getTotalCases(it)
        }

        qhlNote?.let {
            if (it!= AppConstant.NOTES_NOT_AVAILABLE)
            unFinishedBottomSheet.editTextQHLNotes.setText(it.capitalize())
        }
        correctionNote?.let {
            if (it!= AppConstant.NOTES_NOT_AVAILABLE)
            unFinishedBottomSheet.editTextCorrection.setText(it.capitalize())
        }

        if(isCompleted == true){
            unFinishedBottomSheet.editTextWaitingTime.isEnabled =false
            unFinishedBottomSheet.editTextWaitingTimeMinutes.isEnabled =false
            unFinishedBottomSheet.textViewWaitingTimeHeader.isEnabled =false
            unFinishedBottomSheet.hoursTextViewHeading.isEnabled =false
            unFinishedBottomSheet.minutesTextViewHeading.isEnabled =false
            unFinishedBottomSheet.layoutLumperWaitingTime.background = ContextCompat.getDrawable(context, R.drawable.schedule_item_background_grey)
        }else {
            unFinishedBottomSheet.editTextWaitingTime.isEnabled =true
            unFinishedBottomSheet.editTextWaitingTimeMinutes.isEnabled =true
            unFinishedBottomSheet.textViewWaitingTimeHeader.isEnabled =true
            unFinishedBottomSheet.hoursTextViewHeading.isEnabled =true
            unFinishedBottomSheet.minutesTextViewHeading.isEnabled =true
            unFinishedBottomSheet.layoutLumperWaitingTime.background = ContextCompat.getDrawable(context, R.drawable.schedule_item_background)
        }

        lumperTimeDetails?.let { timingDetail ->
            unFinishedBottomSheet.editTextWaitingTime.isEnabled = isCompleted != true
            unFinishedBottomSheet.editTextWaitingTimeMinutes.isEnabled = isCompleted != true

            val waitingTimeHours = ValueUtils.getHoursFromMinutes(timingDetail.waitingTime)
            val waitingTimeMinutes = ValueUtils.getRemainingMinutes(timingDetail.waitingTime)
            if (waitingTimeHours.isNotEmpty() /*&& (0.0 != waitingTimeHours.toDouble() || 0.0 != waitingTimeMinutes.toDouble())*/) {
                unFinishedBottomSheet.editTextWaitingTime.setText(waitingTimeHours)
//                editTextWaitingTime.isEnabled = false
            }
            if (waitingTimeMinutes.isNotEmpty() /*&& (0.0 != waitingTimeMinutes.toDouble() || 0.0 != waitingTimeHours.toDouble())*/) {
                unFinishedBottomSheet.editTextWaitingTimeMinutes.setText(waitingTimeMinutes)
//                editTextWaitingTimeMinutes.isEnabled = false
            }
            if (!timingDetail.partWorkDone.isNullOrEmpty() && timingDetail.partWorkDone!!.toInt() != 0) {
                partWorkDone = timingDetail.partWorkDone!!.toInt()
                lumpercaseVisibility(unFinishedBottomSheet)
                unFinishedBottomSheet.editTextCasesLumpers.setText(partWorkDone.toString())
            }

            updateTimingsDetails(timingDetail,unFinishedBottomSheet, context)

        }

        if (totalCases.isNotEmpty() && ValueUtils.isNumeric(totalCases)) {
            unFinishedBottomSheet.editTextTotalCases.setText(totalCases)
            unFinishedBottomSheet.editTextTotalCases.isEnabled = false
            unFinishedBottomSheet.editTextCasesLumpers.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                    text?.let {
                        if (!text.isNullOrEmpty() && (text.toString()).toInt() != 0) {
                            getPercent(text.toString(), totalCases, unFinishedBottomSheet,context )
                        } else {
                            unFinishedBottomSheet.percentWorkDone.text = "0.0%"
                            partWorkDone = 0
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}

            })
            if (partWorkDone > 0) {
                getPercent(partWorkDone.toString(), totalCases, unFinishedBottomSheet,context)
            }

        } else {
            unFinishedBottomSheet.editTextTotalCases.isEnabled = false
            unFinishedBottomSheet.editTextCasesLumpers.isEnabled = false
        }

        unFinishedBottomSheet.editTextWaitingTime.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val inputHour = editable?.toString()
                if (!inputHour.isNullOrBlank()) {
                    if (24 <= inputHour.toInt()) {
                        unFinishedBottomSheet.editTextWaitingTime.text = null
                        unFinishedBottomSheet.editTextWaitingTime.error = context.getString(R.string.invalid_hour_message)
                        unFinishedBottomSheet.editTextWaitingTime.requestFocus()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        unFinishedBottomSheet.editTextWaitingTimeMinutes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val inputMinutes = editable?.toString()
                if (!inputMinutes.isNullOrEmpty()) {
                    if (60 <= inputMinutes.toInt()) {
                        unFinishedBottomSheet.editTextWaitingTimeMinutes.text = null
                        unFinishedBottomSheet.editTextWaitingTimeMinutes.error = context.getString(R.string.invalid_hour_minutes)
                        unFinishedBottomSheet.editTextWaitingTimeMinutes.requestFocus()
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        updateButtonsUI(unFinishedBottomSheet, context)
        if (totalCases.isNotEmpty() && partWorkDone != 0) lumpercaseVisibility(unFinishedBottomSheet)
        toggleSaveButtonVisibility(unFinishedBottomSheet)
        unFinishedBottomSheet.buttonStartTime.setOnClickListener{
            chooseTime(object : OnTimeSetListener {
                override fun onSelectTime(calendar: Calendar) {
                    onSelectStartTime(calendar, unFinishedBottomSheet, context)
                }
            }, selectedStartTime, context)
        }
        unFinishedBottomSheet.buttonEndTime.setOnClickListener{
            chooseTime(object : OnTimeSetListener {
                override fun onSelectTime(calendar: Calendar) {
                    onSelectEndTime(calendar,unFinishedBottomSheet, context)
                }
            }, selectedEndTime, context)
        }
        unFinishedBottomSheet.buttonBreakInTime.setOnClickListener{
            chooseTime(object : OnTimeSetListener {
                override fun onSelectTime(calendar: Calendar) {
                    onSelectBreakInTime(calendar,unFinishedBottomSheet, context)
                }
            }, selectedBreakInTime,context)
        }
        unFinishedBottomSheet.buttonBreakOutTime.setOnClickListener{
            chooseTime(object : OnTimeSetListener {
                override fun onSelectTime(calendar: Calendar) {
                    onSelectBreakOutTime(calendar, unFinishedBottomSheet, context)
                }
            }, selectedBreakOutTime, context)
        }

        unFinishedBottomSheet.buttonCancelCorrection.setOnClickListener { unFinishedBottomSheet.dismiss() }
        unFinishedBottomSheet.buttonSubmitCorrection.setOnClickListener {
                sendRequest(buildingParams,unFinishedBottomSheet, contractView, context, containerId, lumperId)
        }
        unFinishedBottomSheet.show()
    }

    private fun sendRequest(buildingParams: ArrayList<String>?, unFinishedBottomSheet: BottomSheetDialog, contractView: IDialogRequestCorrectionClick, context: Context, containerId: String?, lumperId: String?) {
        val parameters = ScheduleUtils.getBuildingParametersList(buildingParams)
        val buildingOps=if (buildingOperationsAdapter!= null)buildingOperationsAdapter?.getUpdatedData() else null
        val noteForQhl = unFinishedBottomSheet.editTextQHLNotes?.text.toString()
        val noteCorrection = unFinishedBottomSheet.editTextCorrection?.text.toString()
        var count = 0

        buildingOps?.let {
            for (key in it.keys) {
                val value = it[key]
                if (!value.isNullOrEmpty() && parameters.contains(key)) {
                    count++
                }
            }
        }

        if (noteCorrection.isNullOrEmpty()){
            CustomProgressBar.getInstance().showValidationErrorDialog(
                    context.getString(R.string.request_correction_message),
                    context)

            return
        }
        if (noteForQhl.isNullOrEmpty()){
            CustomProgressBar.getInstance().showValidationErrorDialog(
                    context.getString(R.string.group_qhl_note_error_message),
                    context)

            return
        }

        if (buildingOps.isNullOrEmpty() || count != parameters.size) {
            CustomProgressBar.getInstance().showValidationErrorDialog(
                    context.getString(R.string.fill_building_parameters_message),
                    context)
            return
        }

        //lumperParams request
        val breakTimeList = mPauseTimeRequestList
        val waitingTimeHours = unFinishedBottomSheet.editTextWaitingTime?.text.toString()
        val waitingTimeMinutes = unFinishedBottomSheet.editTextWaitingTimeMinutes?.text.toString()
        val waitingTime =
                ((if (waitingTimeHours.isEmpty()) 0 else waitingTimeHours.toInt() * 60) + if (waitingTimeMinutes.isEmpty()) 0 else waitingTimeMinutes.toInt()).toString()

        val waitingTimeInt = if (waitingTime.isNotEmpty()) waitingTime.toInt() else 0
        val timingDetail = LumperParameterCorrection()
        if (selectedStartTime > 0) timingDetail.startTime = selectedStartTime
        if (selectedEndTime > 0) timingDetail.endTime = selectedEndTime
        if (breakTimeList.size > 0) timingDetail.breakTimeRequests = breakTimeList
        timingDetail.waitingTime = waitingTimeInt
        timingDetail.partWork = partWorkDone

        val lumperCorrectionRequest = LumperCorrectionRequest(lumperId, containerId, timingDetail, buildingOps, noteForQhl, noteCorrection)
        if (!lumperId.isNullOrEmpty() && !containerId.isNullOrEmpty())
            contractView.onSendRequest(unFinishedBottomSheet, lumperCorrectionRequest, containerId)
    }


    private fun getTotalCases(permeters: HashMap<String, String>?): String {
        var cases: String = ""
        if (!permeters.isNullOrEmpty() && permeters.size > 0) {
            cases = permeters.get("Cases").toString()
        }
        return cases
    }

    private fun chooseTime(
        listener: OnTimeSetListener,
        selectedTime: Long,
        context: Context
    ) {
        val calendar = Calendar.getInstance()
        if (selectedTime > 0) {
            calendar.timeInMillis = selectedTime
        } else {
            calendar.timeInMillis = Date().time
        }

        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            context, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                listener.onSelectTime(calendar)
            }, mHour, mMinute, false
        )
        timePickerDialog.show()
    }

    private fun onSelectStartTime(
        calendar: Calendar,
        unFinishedBottomSheet: BottomSheetDialog,
        context: Context
    ) {
        val selectedStartTime = calendar.timeInMillis
        if (selectedEndTime > 0) {
            if (!DateUtils.isFutureTime(selectedStartTime, selectedEndTime)) {
                showErrorDialog(context.getString(R.string.work_start_less_work_end_warning_message),context)
                return
            }
        }else if (selectedBreakInTime>0){
            if (!DateUtils.isFutureTime(selectedStartTime, selectedBreakInTime)) {
                showErrorDialog(context.getString(R.string.work_start_less_work_break_warning_message), context)
                return
            }
        }
        this.selectedStartTime = selectedStartTime
        unFinishedBottomSheet.buttonStartTime.text = DateUtils.convertMillisecondsToTimeString(selectedStartTime)
        updateButtonsUI(unFinishedBottomSheet, context)
    }

    private fun onSelectEndTime(
        calendar: Calendar,
        unFinishedBottomSheet: BottomSheetDialog,
        context: Context
    ) {
        val selectedEndTime = calendar.timeInMillis
        if (!DateUtils.isFutureTime(selectedStartTime, selectedEndTime)) {
            showErrorDialog(context.getString(R.string.work_end_greater_work_start_warning_message), context)
        } else if (selectedBreakInTime > 0 && !DateUtils.isFutureTime(
                selectedBreakInTime,
                selectedEndTime
            )
        ) {
            showErrorDialog(context.getString(R.string.work_end_greater_break_start_warning_message), context)
        } else {
            this.selectedEndTime = selectedEndTime
            unFinishedBottomSheet.buttonEndTime.text = DateUtils.convertMillisecondsToTimeString(selectedEndTime)
            updateButtonsUI(unFinishedBottomSheet,context)
            unFinishedBottomSheet.buttonBreakInTime.isEnabled = true
//            buttonBreakOutTime.isEnabled = false
            unFinishedBottomSheet.buttonBreakInTime.text = context.getString(R.string.pause)
        }
    }

    private fun onSelectBreakInTime(
        calendar: Calendar,
        unFinishedBottomSheet: BottomSheetDialog,
        context: Context
    ) {
        val selectedBreakInTime = calendar.timeInMillis
        if (selectedEndTime > 0) {
            if (!DateUtils.isFutureTime(selectedBreakInTime, selectedEndTime)) {
                showErrorDialog(context.getString(R.string.break_start_less_end_warning_message), context)
                return
            }
        }else if (selectedStartTime > 0) {
            if (DateUtils.isFutureTime(selectedBreakInTime, selectedStartTime)) {
                showErrorDialog(context.getString(R.string.break_start_greater_start_warning_message), context)
                return
            }
        }

        isTimeSelected=true
        selectedBreakOutTime = 0
        unFinishedBottomSheet.buttonBreakOutTime.text = context.getString(R.string.resume)
        this.selectedBreakInTime = selectedBreakInTime
        unFinishedBottomSheet.buttonBreakInTime.text = DateUtils.convertMillisecondsToTimeString(selectedBreakInTime)
        updateButtonsUI(unFinishedBottomSheet,context)
        getPauseTimeCalculate(unFinishedBottomSheet,context)
    }

    private fun onSelectBreakOutTime(calendar: Calendar, unFinishedBottomSheet: BottomSheetDialog,
                                     context: Context) {
        val selectedBreakOutTime = calendar.timeInMillis
        if (!DateUtils.isFutureTime(selectedStartTime, selectedBreakInTime)) {
            showErrorDialog(context.getString(R.string.break_end_greater_work_start_warning_message), context)
        } else if (!DateUtils.isFutureTime(selectedBreakInTime, selectedBreakOutTime)) {
            showErrorDialog(context.getString(R.string.break_end_greater_break_start_warning_message), context)
        } else if (selectedEndTime > 0 && !DateUtils.isFutureTime(
                selectedBreakOutTime,
                selectedEndTime
            )
        ) {
            showErrorDialog(context.getString(R.string.break_end_less_work_end_warning_message), context)
        } else {
            isTimeSelected=true
            this.selectedBreakOutTime = selectedBreakOutTime
            unFinishedBottomSheet.buttonBreakOutTime.text =
                DateUtils.convertMillisecondsToTimeString(selectedBreakOutTime)
            updateButtonsUI(unFinishedBottomSheet, context)
            getPauseTimeCalculate(unFinishedBottomSheet, context)
        }
    }

    private fun updateTimingsDetails(
        timingDetail: LumpersTimeSchedule,
        unFinishedBottomSheet: BottomSheetDialog,
        context: Context
    ) {
        initialStartTime = updateInitialTime(timingDetail.startTime, unFinishedBottomSheet.buttonStartTime)
        initialEndTime = updateInitialTime(timingDetail.endTime, unFinishedBottomSheet.buttonEndTime)
//        initialBreakInTime = updateInitialTime(timingDetail.breakTimeStart, buttonBreakInTime)
//        initialBreakOutTime = updateInitialTime(timingDetail.breakTimeEnd, buttonBreakOutTime)

        timingDetail.breakTimes?.let {
            if (it.isNotEmpty()) {
                val lastItem = it[it.lastIndex]
                initialBreakInTime = updateInitialTime(lastItem.startTime, unFinishedBottomSheet.buttonBreakInTime)
                initialBreakOutTime = updateInitialTime(lastItem.endTime, unFinishedBottomSheet.buttonBreakOutTime)
            }
            getBreakTimeList(it, unFinishedBottomSheet)
        }

        selectedStartTime = initialStartTime
        selectedEndTime = initialEndTime
        selectedBreakInTime = initialBreakInTime
        selectedBreakOutTime = initialBreakOutTime
        updatePauseTimeLayout(unFinishedBottomSheet, context)
    }
    private fun getBreakTimeList(arrayList: ArrayList<PauseTime>,unFinishedBottomSheet: BottomSheetDialog) {
        arrayList.forEach {
            val breakTime = PauseTimeRequest()
            breakTime.startTime = updateInitialTime(it.startTime, unFinishedBottomSheet.buttonBreakInTime)
            breakTime.endTime = updateInitialTime(it.endTime, unFinishedBottomSheet.buttonBreakInTime)
            mPauseTimeRequestList.add(breakTime)
            isTimeSelected= false
        }
    }

    private fun updatePauseTimeLayout(unFinishedBottomSheet: BottomSheetDialog, context: Context) {
        if (0L != initialBreakInTime && 0L != initialBreakOutTime) {
            unFinishedBottomSheet.buttonBreakInTime.text = context.getString(R.string.pause)
            unFinishedBottomSheet.buttonBreakOutTime.text = context.getString(R.string.resume)
            mPauseTimeRequestList.run {
//                mPauseTimeRequestList.clear()
                getPauseTimeCalculate(unFinishedBottomSheet,context)
            }
        }
    }

    private fun getPauseTimeCalculate(unFinishedBottomSheet: BottomSheetDialog, context: Context) {
        val timeRequest = PauseTimeRequest()
        if (selectedBreakInTime > 0 && selectedBreakOutTime == 0L) {
            timeRequest.startTime = selectedBreakInTime
            for (it in mPauseTimeRequestList) {
                if (isTimeSelected)
                    if (it.startTime!! <= selectedBreakInTime && selectedBreakInTime < it.endTime!!) {
                        showErrorDialog("brake in time already available in time slot ", context)
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
                        showErrorDialog("brake out time already available in time slot ", context)
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
                            showErrorDialog("break in/out overriding in previous slot ", context)
                            return
                        } else if (it.startTime!! <= selectedBreakInTime && selectedBreakInTime < it.endTime!!) {
                            showErrorDialog("brake in time already available in time slot ", context)
                            return
                        } else if (it.startTime!! <= selectedBreakOutTime && selectedBreakOutTime < it.endTime!!) {
                            showErrorDialog(
                                "brake out time already available in time slot ",
                                context
                            )
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
            unFinishedBottomSheet.totalPauseTime.visibility = View.VISIBLE
//            mPauseTimeRequestList.add(PauseTimeRequest(selectedBreakInTime,selectedBreakOutTime))
            showPauseTimeDuration(context, unFinishedBottomSheet)
            var dateTime: Long = 0
            for (pauseTime in mPauseTimeRequestList) {
                dateTime += (pauseTime.endTime!! - pauseTime.startTime!!)
            }
            unFinishedBottomSheet. totalPauseTime.text = String.format( context.getString(R.string.total_pause_colon) , DateUtils.getDateTimeCalculatedLong(dateTime))
        } else unFinishedBottomSheet.totalPauseTime.visibility = View.GONE
    }

    private fun showPauseTimeDuration(context: Context, unFinishedBottomSheet: BottomSheetDialog) {
        unFinishedBottomSheet.totalPauseTimeDurationTextView?.visibility = View.VISIBLE
        unFinishedBottomSheet.totalPauseTimeDurationTextView?.text = null
        mPauseTimeRequestList.forEachIndexed { index, pauseTime ->
            val pauseTimeIn = pauseTime.startTime
            val pauseTimeOut = pauseTime.endTime
            val pauseTimeInStr = DateUtils.convertMillisecondsToTimeString(pauseTimeIn?.toLong()!!)
            val pauseTimeOutStr =
                DateUtils.convertMillisecondsToTimeString(pauseTimeOut?.toLong()!!)
            if (index != 0) unFinishedBottomSheet.totalPauseTimeDurationTextView?.append("\n")
            unFinishedBottomSheet.totalPauseTimeDurationTextView?.append("$pauseTimeInStr - $pauseTimeOutStr")
        }
        unFinishedBottomSheet.buttonBreakInTime.isEnabled = true
//        buttonBreakOutTime.isEnabled = false
        unFinishedBottomSheet.buttonBreakInTime.text = context.getString(R.string.pause)
    }

    fun showErrorDialog(message: String, context: Context) {
        CustomProgressBar.getInstance().showErrorDialog(message, context)
    }

    private fun updateInitialTime(dateStamp: String?, buttonTime: Button): Long {
        var milliseconds: Long = 0
        val time = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, dateStamp)
        if (time.isNotEmpty()) {
            buttonTime.text = time
//            buttonTime.isEnabled = false
            val currentDateString =
                DateUtils.convertUTCDateStringToLocalDateString(DateUtils.PATTERN_API_RESPONSE, dateStamp)
            milliseconds =
                DateUtils.getMillisecondsFromDateString(DateUtils.PATTERN_API_RESPONSE, currentDateString)
        }
        return milliseconds
    }

    private fun lumpercaseVisibility(unFinishedBottomSheet: BottomSheetDialog) {
        unFinishedBottomSheet.editTextCasesLumpers.isEnabled =
            unFinishedBottomSheet.editTextWaitingTime.isEnabled || unFinishedBottomSheet.editTextWaitingTimeMinutes.isEnabled || unFinishedBottomSheet.buttonStartTime.isEnabled || unFinishedBottomSheet.buttonEndTime.isEnabled || unFinishedBottomSheet.buttonBreakInTime.isEnabled || unFinishedBottomSheet.buttonBreakOutTime.isEnabled
    }
    private fun toggleSaveButtonVisibility(unFinishedBottomSheet: BottomSheetDialog) {
        unFinishedBottomSheet.buttonSubmitCorrection.isEnabled =
            unFinishedBottomSheet.editTextWaitingTime.isEnabled || unFinishedBottomSheet.editTextWaitingTimeMinutes.isEnabled || unFinishedBottomSheet.buttonStartTime.isEnabled || unFinishedBottomSheet.buttonEndTime.isEnabled || unFinishedBottomSheet.buttonBreakInTime.isEnabled || unFinishedBottomSheet.buttonBreakOutTime.isEnabled || unFinishedBottomSheet.editTextCasesLumpers.isEnabled
    }

    private fun getPercent(
        lumperCase: String,
        totalCases: String,
        unFinishedBottomSheet: BottomSheetDialog,
        context: Context
    ) {
        if (lumperCase.toDouble() <= totalCases.toDouble()) {
            partWorkDone = lumperCase.toInt()
            percentageTime = ScheduleUtils.calculatePercent(lumperCase, totalCases)
            unFinishedBottomSheet.percentWorkDone.text = String.format("%.2f", percentageTime) + "%"
            isPartWorkDoneValid = true
        } else {
            isPartWorkDoneValid = false
            CustomProgressBar.getInstance()
                .showMessageDialog(context.getString(R.string.lumper_cases_error), context)
            unFinishedBottomSheet.percentWorkDone.text = "0.0%"
        }

    }

    private fun updateButtonsUI(unFinishedBottomSheet: BottomSheetDialog, context: Context) {
//        if (initialStartTime <= 0 ) {
//            buttonStartTime.isEnabled = initialStartTime <= 0
//            buttonEndTime.isEnabled =
//                initialEndTime <= 0 && selectedStartTime > 0 && checkBrackout()
//            buttonBreakInTime.isEnabled = initialBreakInTime <= 0 && selectedStartTime > 0
//            buttonBreakOutTime.isEnabled = initialBreakOutTime <= 0 && selectedBreakInTime > 0
//        }
        unFinishedBottomSheet.buttonEndTime.isEnabled=selectedStartTime>0 && checkBrackout()
        unFinishedBottomSheet.buttonBreakOutTime.isEnabled = initialBreakInTime>0 || selectedBreakInTime>0
        getTimeCalculate(unFinishedBottomSheet, context)
    }

    private fun getTimeCalculate(unFinishedBottomSheet: BottomSheetDialog, context: Context) {
        if (selectedStartTime > 0 && selectedEndTime > 0) {
            unFinishedBottomSheet.totalWorkTime.visibility = View.VISIBLE
            val dateString = String.format(
                context.getString(R.string.total_time),
                DateUtils.getDateTimeCalculatedLong(selectedStartTime, selectedEndTime)
            )
            unFinishedBottomSheet.totalWorkTime.text = dateString
        } else unFinishedBottomSheet.totalWorkTime.visibility = View.GONE
    }

    private fun checkBrackout(): Boolean {
        if (selectedBreakInTime > 0 && selectedBreakOutTime <= 0)
            return false
        return true
    }

    interface IDialogRequestCorrectionClick{
        fun onSendRequest(dialog: Dialog, request: LumperCorrectionRequest, containerId: String)
    }
}

interface OnTimeSetListener {
    fun onSelectTime(calendar: Calendar)
}


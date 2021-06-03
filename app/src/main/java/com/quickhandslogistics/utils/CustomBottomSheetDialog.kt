package com.quickhandslogistics.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.quickhandslogistics.R
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.scheduleTime.RequestLumpersRecord
import com.quickhandslogistics.utils.DateUtils.Companion.sharedPref
import java.util.*

object CustomBottomSheetDialog {

    fun unfinishedBottomSheetDialog(context: Context, contractView: IDialogOnClick) {
        val unFinishedBottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        unFinishedBottomSheet.setContentView(R.layout.bottom_sheet_unfinished_container)
        val dateForCompletionText=unFinishedBottomSheet.findViewById<TextView>(R.id.dateForCompletionText)
        val startTimeText=unFinishedBottomSheet.findViewById<TextView>(R.id.startTimeText)
        val departmentText=unFinishedBottomSheet.findViewById<TextView>(R.id.departmentText)
        val shiftText=unFinishedBottomSheet.findViewById<TextView>(R.id.shiftText)
        val calendar = Calendar.getInstance()
        var selectedDate :Date
        var selectedTime:Long
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        selectedDate=calendar.time
        selectedTime= selectedDate.time

        shiftText?.text= leadProfile?.shift?.capitalize()
        departmentText?.text =  UIUtils.getDisplayEmployeeDepartmentHeader(leadProfile)
        dateForCompletionText?.text= DateUtils.getDateString(
            DateUtils.PATTERN_MONTH_DAY_DISPLAY,
            selectedDate
        )

        dateForCompletionText?.setOnClickListener {
            ReportUtils.showTomorrowDatePicker(
                selectedDate,
                context,
                object : ReportUtils.OnDateSetListener {
                    override fun onDateSet(selected: Date) {
                        dateForCompletionText.text =
                            DateUtils.getDateString(DateUtils.PATTERN_MONTH_DAY_DISPLAY, selected)
                        selectedDate = selected
                        selectedTime= selected.time
                    }
                })
        }

        startTimeText?.text = DateUtils.convertMillisecondsToTimeString(Date().time)
        startTimeText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis =selectedDate.time
            val mHour = calendar.get(Calendar.HOUR_OF_DAY)
            val mMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    startTimeText.text =
                        DateUtils.convertMillisecondsToTimeString(calendar.timeInMillis)
                    selectedTime = calendar.timeInMillis
                }, mHour, mMinute, false
            ).show()
        }

        unFinishedBottomSheet.findViewById<Button>(R.id.buttonCancelBottomSheet)?.setOnClickListener { unFinishedBottomSheet.dismiss() }
        unFinishedBottomSheet.findViewById<Button>(R.id.buttonSubmit)?.setOnClickListener {
            contractView.onSendRequest(unFinishedBottomSheet, selectedDate, selectedTime)
        }
        unFinishedBottomSheet.show()
    }
    
    
    fun createUpdateLumperRequest(
        context: Context,
        record: RequestLumpersRecord? = null,
        selectedTime: Long,
        contractView: IDialogOnLumperRequestClick
    ){
        val lumperRequestBottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        lumperRequestBottomSheet.setContentView(R.layout.bottom_sheet_create_lumper_request)
        val textViewTitle=lumperRequestBottomSheet.findViewById<TextView>(R.id.textViewTitle)
        val buttonSubmit=lumperRequestBottomSheet.findViewById<Button>(R.id.buttonSubmit)
        val buttonCancelNote=lumperRequestBottomSheet.findViewById<Button>(R.id.buttonCancelNote)
        val editTextLumpersRequired=lumperRequestBottomSheet.findViewById<EditText>(R.id.editTextLumpersRequired)
        val editTextDMNotes=lumperRequestBottomSheet.findViewById<EditText>(R.id.editTextDMNotes)
        val editTextLumperNotes=lumperRequestBottomSheet.findViewById<EditText>(R.id.editTextLumperNotes)
        val textViewStartTime=lumperRequestBottomSheet.findViewById<TextView>(R.id.textViewStartTime)
        var startTime: Long = selectedTime
        var lumperId = ""

        record?.also {
            textViewTitle?.text = context.getString(R.string.update_request)
            buttonSubmit?.text = context.getString(R.string.update)
            record.id?.let { lumperId = it }
            val requestedLumpersCount = ValueUtils.getDefaultOrValue(record.requestedLumpersCount)
            editTextLumpersRequired?.setText("$requestedLumpersCount")
            editTextDMNotes?.setText(record.notesForDM)
            editTextLumperNotes?.setText(record.notesForLumper)
            if (record.startTime != null) {
                startTime=record.startTime?.toLong()!!
                textViewStartTime?.text =
                    DateUtils.convertMillisecondsToTimeString(startTime)
            }
        } ?: run {
            textViewTitle?.text = context.getString(R.string.create_new_request)
            buttonSubmit?.text = context.getString(R.string.submit)
            lumperId = ""
            editTextLumpersRequired?.setText("")
            editTextDMNotes?.setText("")
            editTextLumperNotes?.setText("")
            textViewStartTime?.text = DateUtils.convertMillisecondsToTimeString(startTime)
        }

        buttonCancelNote?.setOnClickListener { lumperRequestBottomSheet.dismiss() }
        textViewStartTime?.setOnClickListener { val calendar = Calendar.getInstance()
            calendar.timeInMillis = startTime
            val mHour = calendar.get(Calendar.HOUR_OF_DAY)
            val mMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    textViewStartTime.text =
                        DateUtils.convertMillisecondsToTimeString(calendar.timeInMillis)
                    startTime = calendar.timeInMillis
                }, mHour, mMinute, false
            ).show() }
        buttonSubmit?.setOnClickListener {
            val requiredLumperCount = editTextLumpersRequired?.text.toString()
            val notesDM = editTextDMNotes?.text.toString()
            val noteLumper = editTextLumperNotes?.text.toString()
            val startTimeText = textViewStartTime?.text.toString()
            when {
                requiredLumperCount.isEmpty() -> {
                    CustomProgressBar.getInstance().showValidationErrorDialog(
                        context.getString(R.string.request_lumper_number_message),
                        context
                    )
                }
                notesDM.isEmpty() -> {
                    CustomProgressBar.getInstance().showValidationErrorDialog(
                        context.getString(R.string.request_lumper_DM_note_message),
                        context
                    )
                }
                noteLumper.isEmpty() -> {
                    CustomProgressBar.getInstance().showValidationErrorDialog(
                        context.getString(R.string.request_lumper_lumper_note_message),
                        context
                    )
                }
                startTimeText.isEmpty() -> {
                    CustomProgressBar.getInstance().showValidationErrorDialog(
                        context.getString(R.string.request_lumper_start_time_message),
                        context
                    )
                }
                requiredLumperCount.toInt()==0 -> {
                    CustomProgressBar.getInstance().showValidationErrorDialog(
                        context.getString(R.string.request_valid_message),
                        context
                    )
                }
                else -> {
                    contractView.onSendLumperRequest(
                        lumperRequestBottomSheet,
                        requiredLumperCount,
                        notesDM,
                        noteLumper,
                        startTime,
                        lumperId
                    )
                }
            }
        }
        lumperRequestBottomSheet.show()
    }

    fun requestCorrectionBottomSheetDialog(context: Context, contractView: IDialogRequestCorrectionClick) {
        val unFinishedBottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        unFinishedBottomSheet.setContentView(R.layout.bottumsheet_request_correction)
        unFinishedBottomSheet.findViewById<TextView>(R.id.textViewTitle)
        val editTextCorrection = unFinishedBottomSheet.findViewById<EditText>(R.id.editTextCorrection)

        unFinishedBottomSheet.findViewById<Button>(R.id.buttonCancelCorrection)
            ?.setOnClickListener { unFinishedBottomSheet.dismiss() }
        unFinishedBottomSheet.findViewById<Button>(R.id.buttonSubmitCorrection)
            ?.setOnClickListener {
                if (!editTextCorrection?.text.isNullOrEmpty())
                    contractView.onSendRequest(
                        unFinishedBottomSheet,
                        editTextCorrection?.text.toString()
                    )
                else CustomProgressBar.getInstance().showValidationErrorDialog(
                    context.getString(R.string.request_correction_message),
                    context
                )
            }
        unFinishedBottomSheet.show()
    }


    fun sendMessageBottomSheetDialog(context: Context, contractView: IDialogRequestCorrectionClick) {
        val unFinishedBottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        unFinishedBottomSheet.setContentView(R.layout.bottumsheet_send_message)
        unFinishedBottomSheet.findViewById<TextView>(R.id.textViewTitle)
        val editTextMessage = unFinishedBottomSheet.findViewById<EditText>(R.id.editTextMessage)

        unFinishedBottomSheet.findViewById<Button>(R.id.buttonCancelMessage)
            ?.setOnClickListener { unFinishedBottomSheet.dismiss() }
        unFinishedBottomSheet.findViewById<Button>(R.id.buttonSendMessage)
            ?.setOnClickListener {
                if (!editTextMessage?.text.isNullOrEmpty())
                    contractView.onSendRequest(
                        unFinishedBottomSheet,
                        editTextMessage?.text.toString()
                    )
                else CustomProgressBar.getInstance().showValidationErrorDialog(
                    context.getString(R.string.please_enter_message_error_message),
                    context
                )
            }
        unFinishedBottomSheet.show()
    }

    interface IDialogOnClick{
        fun onSendRequest(dialog: Dialog, selectedDate: Date, selectedTime: Long)
    }

    interface IDialogRequestCorrectionClick{
        fun onSendRequest(dialog: Dialog, request: String)
    }

    interface IDialogOnLumperRequestClick{
        fun onSendLumperRequest(
            dialog: Dialog,
            requiredLumper: String,
            noteForDm: String,
            noteForLumper: String,
            startTime: Long,
            lumperId: String
        )
    }
}
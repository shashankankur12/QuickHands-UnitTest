package com.quickhandslogistics.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.quickhandslogistics.R
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.utils.DateUtils.Companion.sharedPref
import java.util.*

object CustomBottomSheetDialog {

    fun unfinishedBottomSheetDialog(context: Context, contractView: IDialogOnClick) {
        val unFinishedBottomSheet = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        unFinishedBottomSheet.setContentView(R.layout.bottom_sheet_unfinished_container)
        val dateForCompletionText =
            unFinishedBottomSheet.findViewById<TextView>(R.id.dateForCompletionText)
        val startTimeText = unFinishedBottomSheet.findViewById<TextView>(R.id.startTimeText)
        val departmentText = unFinishedBottomSheet.findViewById<TextView>(R.id.departmentText)
        val shiftText = unFinishedBottomSheet.findViewById<TextView>(R.id.shiftText)

        val leadProfile = sharedPref.getClassObject(
            AppConstant.PREFERENCE_LEAD_PROFILE,
            LeadProfileData::class.java
        ) as LeadProfileData?

        shiftText?.text = leadProfile?.shift?.capitalize()
        departmentText?.text = UIUtils.getDisplayEmployeeDepartment(leadProfile)

        dateForCompletionText?.text =
            DateUtils.getDateString(DateUtils.PATTERN_MONTH_DAY_DISPLAY, Date())
        dateForCompletionText?.setOnClickListener {
            ReportUtils.showDatePicker(Date(), context, object : ReportUtils.OnDateSetListener {
                override fun onDateSet(selected: Date) {
                    dateForCompletionText.text =
                        DateUtils.getDateString(DateUtils.PATTERN_MONTH_DAY_DISPLAY, selected)
                }
            })
        }

        startTimeText?.text = DateUtils.convertMillisecondsToTimeString(Date().time)
        startTimeText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = Date().time
            val mHour = calendar.get(Calendar.HOUR_OF_DAY)
            val mMinute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    startTimeText.text =
                        DateUtils.convertMillisecondsToTimeString(calendar.timeInMillis)
                }, mHour, mMinute, false
            ).show()
        }

        unFinishedBottomSheet.findViewById<Button>(R.id.buttonCancelBottomSheet)
            ?.setOnClickListener { unFinishedBottomSheet.dismiss() }
        unFinishedBottomSheet.findViewById<Button>(R.id.buttonSubmit)?.setOnClickListener {
            contractView.onSendRequest(unFinishedBottomSheet)
        }
        unFinishedBottomSheet.show()
    }

    interface IDialogOnClick {
        fun onSendRequest(dialog: Dialog)
    }
}
package com.quickhandslogistics.modified.views.workSheet

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.utils.DateUtils
import kotlinx.android.synthetic.main.content_add_lumper_time_work_sheet_item.*
import java.util.*

class AddLumperTimeWorkSheetItemActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lumper_time_work_sheet_item)
        setupToolbar(getString(R.string.add_time))

        initializeUI()
    }

    private fun initializeUI() {
        linearLayoutStartTime.setOnClickListener(this)
        linearLayoutEndTime.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                linearLayoutStartTime.id -> {
                    chooseTime(textViewStartTime)
                }
                linearLayoutEndTime.id -> {
                    chooseTime(textViewEndTime)
                }
                buttonSave.id -> {
                    onBackPressed()
                }
            }
        }
    }

    private fun chooseTime(textView: TextView) {
        val calendar = Calendar.getInstance()
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                textView.text = DateUtils.convertMillisecondsToTimeString(calendar.timeInMillis)
            },
            mHour, mMinute, false
        )
        timePickerDialog.show()
    }
}

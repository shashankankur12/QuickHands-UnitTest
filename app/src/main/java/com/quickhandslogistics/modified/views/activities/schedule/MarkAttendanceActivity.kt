package com.quickhandslogistics.modified.views.activities.schedule

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.MarkAttendanceContract
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.MarkAttendanceAdapter
import com.quickhandslogistics.modified.views.fragments.schedule.ScheduleFragment
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_mark_attendance.*
import kotlinx.android.synthetic.main.bottom_sheet_add_time.*
import kotlinx.android.synthetic.main.content_mark_attendance.*

class MarkAttendanceActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    MarkAttendanceContract.View.OnAdapterItemClickListener,
    RangeTimePickerDialog.ISelectedTime {

    private lateinit var markAttendanceAdapter: MarkAttendanceAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var isCurrentDate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_attendance)
        setupToolbar(getString(R.string.mark_attendance))

        intent.extras?.let {
            if (it.containsKey(ScheduleFragment.ARG_SELECTED_TIME)) {
                val selectedTime = it.getLong(ScheduleFragment.ARG_SELECTED_TIME)
                isCurrentDate = DateUtils.isCurrentDate(selectedTime)
            }
        }

        initializeUI()
    }

    private fun initializeUI() {

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            markAttendanceAdapter = MarkAttendanceAdapter(activity, this@MarkAttendanceActivity)
            adapter = markAttendanceAdapter
        }

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)

        textViewShiftTime.setOnClickListener(this)
        textViewLunchTime.setOnClickListener(this)
        bottomSheetBackground.setOnClickListener(this)
    }

    /*
    * Native Views Listeners
    */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                bottomSheetBackground.id -> closeBottomSheet()
                textViewShiftTime.id -> {
                    closeBottomSheet()
                    showTimePicker()
                }
                textViewLunchTime.id -> {
                    closeBottomSheet()
                    showTimePicker()
                }
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(activity)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        text?.let {
            markAttendanceAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }
    
    /*
    * Adapter Item Click Listeners
    */
    override fun onItemClick() {

    }

    override fun onAddTimeClick() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackground.visibility = View.GONE
    }

    private fun showTimePicker() {
        Handler().postDelayed({
            val dialog = RangeTimePickerDialog()
            dialog.apply {
                newInstance()
                setRadiusDialog(20)
                setIs24HourView(false)
                setColorTabSelected(R.color.textBlack)
                setColorTabUnselected(R.color.detailHeader)
                setColorBackgroundHeader(R.color.colorPrimary)
                setColorBackgroundTimePickerHeader(R.color.scheduleDetail)
                setColorTextButton(R.color.buttonRed)
            }
            dialog.setTextBtnPositive(getString(R.string.string_select))
            dialog.show(fragmentManager, RangeTimePickerDialog::class.java.simpleName)
        }, 100)
    }

    override fun onSelectedTime(hourStart: Int, minuteStart: Int, hourEnd: Int, minuteEnd: Int) {

    }
}

package com.quickhandslogistics.modified.views.activities

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.MarkAttendanceContract
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData
import com.quickhandslogistics.modified.presenters.MarkAttendancePresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.adapters.schedule.MarkAttendanceAdapter
import com.quickhandslogistics.utils.CustomProgressBar
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SnackBarFactory
import com.quickhandslogistics.utils.Utils
import kotlinx.android.synthetic.main.activity_mark_attendance.*
import kotlinx.android.synthetic.main.bottom_sheet_add_time.*
import kotlinx.android.synthetic.main.content_mark_attendance.*

class MarkAttendanceActivity : BaseActivity(), View.OnClickListener, TextWatcher,
    MarkAttendanceContract.View, MarkAttendanceContract.View.OnAdapterItemClickListener,
    RangeTimePickerDialog.ISelectedTime {

    private lateinit var markAttendancePresenter: MarkAttendancePresenter
    private lateinit var markAttendanceAdapter: MarkAttendanceAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_attendance)
        setupToolbar(getString(R.string.time_clock_attendance))

        initializeUI()

        markAttendancePresenter = MarkAttendancePresenter(this, resources)
        markAttendancePresenter.fetchAttendanceList()
    }

    private fun initializeUI() {

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration =
                DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            markAttendanceAdapter = MarkAttendanceAdapter(this@MarkAttendanceActivity)
            adapter = markAttendanceAdapter
        }

        markAttendanceAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                val updatedData = markAttendanceAdapter.getUpdatedData()
                buttonSave.isEnabled = updatedData.size > 0
            }
        })

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonSave.setOnClickListener(this)

        buttonClockIn.setOnClickListener(this)
        buttonClockOut.setOnClickListener(this)
        buttonLunchIn.setOnClickListener(this)
        buttonLunchOut.setOnClickListener(this)
        bottomSheetBackground.setOnClickListener(this)
    }

    /*
    * Native Views Listeners
    */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                bottomSheetBackground.id -> closeBottomSheet()
                buttonClockIn.id -> {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                    markAttendanceAdapter.updateClockInTime(
                        itemPosition,
                        System.currentTimeMillis()
                    )
                }
                buttonClockOut.id -> {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                    markAttendanceAdapter.updateClockOutTime(
                        itemPosition,
                        System.currentTimeMillis()
                    )
                }
                buttonLunchIn.id -> {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                    markAttendanceAdapter.updateLunchInTime(
                        itemPosition,
                        System.currentTimeMillis()
                    )
                }
                buttonLunchOut.id -> {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                    markAttendanceAdapter.updateLunchOutTime(
                        itemPosition,
                        System.currentTimeMillis()
                    )
                }
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(activity)
                }
                buttonSave.id -> {
                    imageViewCancel.performClick()
                    val updatedData = markAttendanceAdapter.getUpdatedData()
                    markAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct())
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
    override fun onAddTimeClick(lumperAttendanceData: LumperAttendanceData, itemPosition: Int) {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
            bottomSheetBackground.setTag(R.id.attendancePosition, itemPosition)

            // Show Clock-In Time
            val clockInTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.morningPunchIn
            )
            buttonClockIn.text =
                if (clockInTime.isNotEmpty()) clockInTime else getString(R.string.clock_in)
            buttonClockIn.isEnabled = clockInTime.isEmpty()


            // Show Clock-Out Time
            val clockOutTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.eveningPunchOut
            )
            buttonClockOut.text =
                if (clockOutTime.isNotEmpty()) clockOutTime else getString(R.string.clock_out)
            buttonClockOut.isEnabled = clockOutTime.isEmpty()

            // Show Lunch-In Time
            val lunchInTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.lunchPunchIn
            )
            buttonLunchIn.text =
                if (lunchInTime.isNotEmpty()) lunchInTime else getString(R.string.out_to_lunch)
            buttonLunchIn.isEnabled = lunchInTime.isEmpty()

            // Show Lunch-Out Time
            val lunchOutTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.lunchPunchOut
            )
            buttonLunchOut.text =
                if (lunchOutTime.isNotEmpty()) lunchOutTime else getString(R.string.back_to_work)
            buttonLunchOut.isEnabled = lunchOutTime.isEmpty()
        } else {
            closeBottomSheet()
        }
    }

    override fun onAddNotes(updatedDataSize: Int) {
        buttonSave.isEnabled = updatedDataSize > 0
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

    override fun showLumpersAttendance(lumperAttendanceList: ArrayList<LumperAttendanceData>) {
        markAttendanceAdapter.updateList(lumperAttendanceList)
    }

    override fun showDataSavedMessage() {
        markAttendancePresenter.fetchAttendanceList()
    }
}

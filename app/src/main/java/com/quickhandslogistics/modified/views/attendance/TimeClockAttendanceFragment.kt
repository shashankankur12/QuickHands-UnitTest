package com.quickhandslogistics.modified.views.attendance

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.attendance.TimeClockAttendanceAdapter
import com.quickhandslogistics.modified.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.modified.data.attendance.LumperAttendanceData
import com.quickhandslogistics.modified.presenters.attendance.TimeClockAttendancePresenter
import com.quickhandslogistics.modified.views.BaseFragment
import com.quickhandslogistics.utils.*
import kotlinx.android.synthetic.main.bottom_sheet_add_attendance_time.*
import kotlinx.android.synthetic.main.content_time_clock_attendance.*
import kotlinx.android.synthetic.main.fragment_time_clock_attendance.*

class TimeClockAttendanceFragment : BaseFragment(), View.OnClickListener, TextWatcher,
    TimeClockAttendanceContract.View, TimeClockAttendanceContract.View.OnAdapterItemClickListener {

    private lateinit var timeClockAttendancePresenter: TimeClockAttendancePresenter
    private lateinit var timeClockAttendanceAdapter: TimeClockAttendanceAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeClockAttendancePresenter = TimeClockAttendancePresenter(this, resources)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_clock_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()

        timeClockAttendancePresenter.fetchAttendanceList()
    }

    private fun initializeUI() {

        recyclerViewLumpers.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            timeClockAttendanceAdapter = TimeClockAttendanceAdapter(this@TimeClockAttendanceFragment)
            adapter = timeClockAttendanceAdapter
        }

        timeClockAttendanceAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                val updatedData = timeClockAttendanceAdapter.getUpdatedData()
                buttonSave.isEnabled = updatedData.size > 0

                textViewEmptyData.visibility = if (timeClockAttendanceAdapter.itemCount == 0) View.VISIBLE else View.GONE
            }
        })

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
        buttonAddTime.setOnClickListener(this)

        buttonClockIn.setOnClickListener(this)
        buttonClockOut.setOnClickListener(this)
        buttonLunchIn.setOnClickListener(this)
        buttonLunchOut.setOnClickListener(this)
        bottomSheetBackground.setOnClickListener(this)
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackground.visibility = View.GONE
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

                    val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
                    if (isMultiSelect) {
                        timeClockAttendanceAdapter.updateClockInTimeForSelectedPositions(System.currentTimeMillis())
                        timeClockAttendanceAdapter.resetAnimationIndex()
                        toggleSaveButton(timeClockAttendanceAdapter.getSelectedItemCount())
                    } else {
                        val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                        timeClockAttendanceAdapter.updateClockInTime(itemPosition, System.currentTimeMillis())
                    }
                }
                buttonClockOut.id -> {
                    closeBottomSheet()

                    val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
                    if (isMultiSelect) {
                        timeClockAttendanceAdapter.updateClockOutTimeForSelectedPositions(System.currentTimeMillis())
                        timeClockAttendanceAdapter.resetAnimationIndex()
                        toggleSaveButton(timeClockAttendanceAdapter.getSelectedItemCount())
                    } else {
                        val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                        timeClockAttendanceAdapter.updateClockOutTime(itemPosition, System.currentTimeMillis())
                    }

                }
                buttonLunchIn.id -> {
                    closeBottomSheet()

                    val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
                    if (isMultiSelect) {
                        timeClockAttendanceAdapter.updateLunchInTimeForSelectedPositions(System.currentTimeMillis())
                        timeClockAttendanceAdapter.resetAnimationIndex()
                        toggleSaveButton(timeClockAttendanceAdapter.getSelectedItemCount())
                    } else {
                        val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                        timeClockAttendanceAdapter.updateLunchInTime(itemPosition, System.currentTimeMillis())
                    }
                }
                buttonLunchOut.id -> {
                    closeBottomSheet()

                    val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
                    if (isMultiSelect) {
                        timeClockAttendanceAdapter.updateLunchOutTimeForSelectedPositions(System.currentTimeMillis())
                        timeClockAttendanceAdapter.resetAnimationIndex()
                        toggleSaveButton(timeClockAttendanceAdapter.getSelectedItemCount())
                    } else {
                        val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
                        timeClockAttendanceAdapter.updateLunchOutTime(itemPosition, System.currentTimeMillis())
                    }
                }
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    Utils.hideSoftKeyboard(fragmentActivity!!)
                }
                buttonSave.id -> {
                    CustomProgressBar.getInstance().showWarningDialog(
                        getString(R.string.string_ask_to_save_attendance_details), fragmentActivity!!, object : CustomDialogWarningListener {
                            override fun onConfirmClick() {
                                imageViewCancel.performClick()
                                val updatedData = timeClockAttendanceAdapter.getUpdatedData()
                                timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct())
                            }

                            override fun onCancelClick() {
                            }
                        })
                }
                buttonAddTime.id -> {
                    showTimePickerLayoutForMultipleLumpers()
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
            timeClockAttendanceAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeClockAttendancePresenter.onDestroy()
    }


    /*
    * Presenter Listeners
    */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
        recyclerViewLumpers.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
    }

    override fun showLumpersAttendance(lumperAttendanceList: ArrayList<LumperAttendanceData>) {
        timeClockAttendanceAdapter.updateList(lumperAttendanceList)
        if (lumperAttendanceList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun showDataSavedMessage() {
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.attendance_saved_successfully),
            fragmentActivity!!, object : CustomDialogListener {
                override fun onConfirmClick() {
                    timeClockAttendancePresenter.fetchAttendanceList()
                }
            })
    }

    /*
    * Adapter Item Click Listeners
    */
    override fun onAddTimeClick(lumperAttendanceData: LumperAttendanceData, itemPosition: Int) {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
            bottomSheetBackground.setTag(R.id.attendancePosition, itemPosition)
            bottomSheetBackground.setTag(R.id.isMultiSelect, false)

            // Show Clock-In Time
            val clockInTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.morningPunchIn
            )
            buttonClockIn.text = if (clockInTime.isNotEmpty()) clockInTime else getString(R.string.clock_in)
            buttonClockIn.isEnabled = timeClockAttendanceAdapter.checkIfEditable(
                clockInTime.isNotEmpty(), "isMorningPunchIn", lumperAttendanceData.id!!
            )

            // Show Clock-Out Time
            val clockOutTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.eveningPunchOut
            )
            buttonClockOut.text =
                if (clockOutTime.isNotEmpty()) clockOutTime else getString(R.string.clock_out)
            buttonClockOut.isEnabled = timeClockAttendanceAdapter.checkIfEditable(
                clockOutTime.isNotEmpty(), "isEveningPunchOut", lumperAttendanceData.id!!
            )

            // Show Lunch-In Time
            val lunchInTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.lunchPunchIn
            )
            buttonLunchIn.text =
                if (lunchInTime.isNotEmpty()) lunchInTime else getString(R.string.out_to_lunch)
            buttonLunchIn.isEnabled = timeClockAttendanceAdapter.checkIfEditable(
                lunchInTime.isNotEmpty(), "isLunchPunchIn", lumperAttendanceData.id!!
            )

            // Show Lunch-Out Time
            val lunchOutTime = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.attendanceDetail?.lunchPunchOut
            )
            buttonLunchOut.text =
                if (lunchOutTime.isNotEmpty()) lunchOutTime else getString(R.string.back_to_work)
            buttonLunchOut.isEnabled = timeClockAttendanceAdapter.checkIfEditable(
                lunchOutTime.isNotEmpty(), "isLunchPunchOut", lumperAttendanceData.id!!
            )
        } else {
            closeBottomSheet()
        }
    }

    override fun onAddNotes(updatedDataSize: Int) {
        buttonSave.isEnabled = updatedDataSize > 0
    }

    override fun onRowLongClicked(itemPosition: Int) {
        timeClockAttendanceAdapter.toggleSelection(itemPosition)
        val count: Int = timeClockAttendanceAdapter.getSelectedItemCount()

        toggleSaveButton(count)
    }

    override fun onRowClicked(itemPosition: Int) {
        if (timeClockAttendanceAdapter.getSelectedItemCount() > 0) {
            onRowLongClicked(itemPosition)
        }
    }

    private fun toggleSaveButton(selectedCount: Int) {
        if (selectedCount > 0) {
            buttonAddTime.visibility = View.VISIBLE
            buttonSave.visibility = View.GONE
        } else {
            buttonAddTime.visibility = View.GONE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showTimePickerLayoutForMultipleLumpers() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            var isClockInEditable = true
            var isClockOutEditable = true
            var isLunchInEditable = true
            var isLunchOutEditable = true

            val list = timeClockAttendanceAdapter.getSelectedItems()
            for (lumperAttendanceData in list) {
                // Show Clock-In Time
                val clockInTime = DateUtils.convertDateStringToTime(
                    DateUtils.PATTERN_API_RESPONSE,
                    lumperAttendanceData.attendanceDetail?.morningPunchIn
                )
                if (isClockInEditable && !(timeClockAttendanceAdapter.checkIfEditable(
                        clockInTime.isNotEmpty(), "isMorningPunchIn", lumperAttendanceData.id!!
                    ))
                ) {
                    isClockInEditable = false
                }

                // Show Clock-Out Time
                val clockOutTime = DateUtils.convertDateStringToTime(
                    DateUtils.PATTERN_API_RESPONSE,
                    lumperAttendanceData.attendanceDetail?.eveningPunchOut
                )
                if (isClockOutEditable && !(timeClockAttendanceAdapter.checkIfEditable(
                        clockOutTime.isNotEmpty(), "isEveningPunchOut", lumperAttendanceData.id!!
                    ))
                ) {
                    isClockOutEditable = false
                }

                // Show Lunch-In Time
                val lunchInTime = DateUtils.convertDateStringToTime(
                    DateUtils.PATTERN_API_RESPONSE,
                    lumperAttendanceData.attendanceDetail?.lunchPunchIn
                )
                if (isLunchInEditable && !(timeClockAttendanceAdapter.checkIfEditable(
                        lunchInTime.isNotEmpty(), "isLunchPunchIn", lumperAttendanceData.id!!
                    ))
                ) {
                    isLunchInEditable = false
                }

                // Show Lunch-Out Time
                val lunchOutTime = DateUtils.convertDateStringToTime(
                    DateUtils.PATTERN_API_RESPONSE,
                    lumperAttendanceData.attendanceDetail?.lunchPunchOut
                )
                if (isLunchOutEditable && !(timeClockAttendanceAdapter.checkIfEditable(
                        lunchOutTime.isNotEmpty(), "isLunchPunchOut", lumperAttendanceData.id!!
                    ))
                ) {
                    isLunchOutEditable = false
                }
            }

            buttonClockIn.isEnabled = isClockInEditable
            buttonClockOut.isEnabled = isClockOutEditable
            buttonLunchIn.isEnabled = isLunchInEditable
            buttonLunchOut.isEnabled = isLunchOutEditable

            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
            bottomSheetBackground.setTag(R.id.isMultiSelect, true)
        } else {
            closeBottomSheet()
        }
    }
}

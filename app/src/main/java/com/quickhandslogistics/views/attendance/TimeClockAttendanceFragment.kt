package com.quickhandslogistics.views.attendance

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.attendance.TimeClockAttendanceAdapter
import com.quickhandslogistics.contracts.attendance.TimeClockAttendanceContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.presenters.attendance.TimeClockAttendancePresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_EVENING_PUNCH_OUT
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_LUNCH_PUNCH_IN
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_LUNCH_PUNCH_OUT
import com.quickhandslogistics.utils.AppConstant.Companion.ATTENDANCE_MORNING_PUNCH_IN
import com.quickhandslogistics.utils.DateUtils.Companion.PATTERN_API_RESPONSE
import com.quickhandslogistics.utils.DateUtils.Companion.convertDateStringToTime
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.DashBoardActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.workSheet.WorkSheetFragment
import kotlinx.android.synthetic.main.bottom_sheet_add_attendance_time.*
import kotlinx.android.synthetic.main.content_time_clock_attendance.*
import kotlinx.android.synthetic.main.fragment_time_clock_attendance.*

class TimeClockAttendanceFragment : BaseFragment(), View.OnClickListener, TextWatcher,
    TimeClockAttendanceContract.View, TimeClockAttendanceContract.View.OnAdapterItemClickListener, TimeClockAttendanceContract.View.fragmentDataListener{

    private lateinit var timeClockAttendancePresenter: TimeClockAttendancePresenter
    private lateinit var timeClockAttendanceAdapter: TimeClockAttendanceAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private  var lumperAttendanceList: ArrayList<LumperAttendanceData> =ArrayList()

    private lateinit var date: String

    companion object {
        const val LUMPER_ATTENDANCE_LIST = "LUMPER_ATTENDANCE_LIST"
        const val TIME_CLOCK_DATE_SELECTED_HEADER = "TIME_CLOCK_DATE_SELECTED_HEADER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeClockAttendancePresenter = TimeClockAttendancePresenter(this, resources, sharedPref)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_clock_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(TIME_CLOCK_DATE_SELECTED_HEADER)) {
                date = savedInstanceState.getString(TIME_CLOCK_DATE_SELECTED_HEADER)!!
                showHeaderInfo(date)
            }
            if (savedInstanceState.containsKey(LUMPER_ATTENDANCE_LIST)) {
                lumperAttendanceList =
                    savedInstanceState.getParcelableArrayList(LUMPER_ATTENDANCE_LIST)!!
                showLumpersAttendance(lumperAttendanceList)
            }
        } ?: run {
            timeClockAttendancePresenter.fetchAttendanceList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeClockAttendancePresenter.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (lumperAttendanceList != null)
            outState.putParcelableArrayList(LUMPER_ATTENDANCE_LIST, lumperAttendanceList)
        if (!date.isNullOrEmpty())
            outState.putString(TIME_CLOCK_DATE_SELECTED_HEADER, date)
        super.onSaveInstanceState(outState)
    }

    private fun initializeUI() {
        recyclerViewLumpers.apply {
            layoutManager = LinearLayoutManager(fragmentActivity!!)
            addItemDecoration(SpaceDividerItemDecorator(15))
            timeClockAttendanceAdapter = TimeClockAttendanceAdapter(this@TimeClockAttendanceFragment)
            adapter = timeClockAttendanceAdapter
        }

        timeClockAttendanceAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                val updatedData = timeClockAttendanceAdapter.getUpdatedData()
                buttonSave.isEnabled = updatedData.size > 0
                var dashboardInstance = activity as DashBoardActivity?
                dashboardInstance?.isShowLeavePopup = updatedData.size > 0

                invalidateEmptyView()
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

    private fun invalidateEmptyView() {
        if (timeClockAttendanceAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (timeClockAttendanceAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message)
        }
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackground.visibility = View.GONE
    }

    private fun showTimePickerLayoutForMultipleLumpers() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            var isClockInEditable = true
            var isClockOutEditable = true
            var isLunchInEditable = true
            var isLunchOutEditable = true

            var hasAllPreSavedClockInTime = true
            var hasAllPreSavedClockOutTime = true
            var hasAllPreSavedLunchInTime = true
            var hasAllPreSavedLunchOutTime = true

            val list = timeClockAttendanceAdapter.getSelectedItems()
            textViewLName.text=getString(R.string.bulk_action)
            for (lumperAttendanceData in list) {
                // Check Clock-In Time
                val clockInTime = convertDateStringToTime(PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.morningPunchIn)
                if (hasAllPreSavedClockInTime && clockInTime.isEmpty()) {
                    hasAllPreSavedClockInTime = false
                }
                if (isClockInEditable && !(timeClockAttendanceAdapter.checkIfEditable(clockInTime.isNotEmpty(), ATTENDANCE_MORNING_PUNCH_IN, lumperAttendanceData.id!!))) {
                    isClockInEditable = false
                }

                // Check Clock-Out Time
                val clockOutTime = convertDateStringToTime(PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.eveningPunchOut)
                if (hasAllPreSavedClockOutTime && clockOutTime.isEmpty()) {
                    hasAllPreSavedClockOutTime = false
                }
                if (isClockOutEditable && !(timeClockAttendanceAdapter.checkIfEditable(clockOutTime.isNotEmpty(), ATTENDANCE_EVENING_PUNCH_OUT, lumperAttendanceData.id!!))) {
                    isClockOutEditable = false
                }

                // Check Lunch-In Time
                val lunchInTime = convertDateStringToTime(PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.lunchPunchIn)
                if (hasAllPreSavedLunchInTime && lunchInTime.isEmpty()) {
                    hasAllPreSavedLunchInTime = false
                }
                if (isLunchInEditable && !(timeClockAttendanceAdapter.checkIfEditable(lunchInTime.isNotEmpty(), ATTENDANCE_LUNCH_PUNCH_IN, lumperAttendanceData.id!!))) {
                    isLunchInEditable = false
                }

                // Check Lunch-Out Time
                val lunchOutTime = convertDateStringToTime(PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.lunchPunchOut)
                if (hasAllPreSavedLunchOutTime && lunchOutTime.isEmpty()) {
                    hasAllPreSavedLunchOutTime = false
                }
                if (isLunchOutEditable && !(timeClockAttendanceAdapter.checkIfEditable(lunchOutTime.isNotEmpty(), ATTENDANCE_LUNCH_PUNCH_OUT, lumperAttendanceData.id!!))) {
                    isLunchOutEditable = false
                }
            }

            buttonClockIn.text = getString(R.string.clock_in)
            buttonClockIn.isEnabled = isClockInEditable

            /* ClockOut Button will only be enabled in these cases:
                1. All the selected items ClockIn Time is Punched
                2. All the selected items LunchIn Time is Not Punched
                3. All the selected items LunchIn Time & LunchOut Time are both punched */
            buttonClockOut.text = getString(R.string.clock_out)
            val isEnabledClockOut = isClockOutEditable && !isClockInEditable && (isLunchInEditable || !(!isLunchInEditable && isLunchOutEditable))
            buttonClockOut.isEnabled = isEnabledClockOut && hasAllPreSavedClockInTime &&
                    ((hasAllPreSavedLunchInTime && hasAllPreSavedLunchOutTime) || (!hasAllPreSavedLunchInTime && !hasAllPreSavedLunchOutTime))

            /* LunchIn Button will only be enabled in these cases:
                1. All the selected items ClockIn Time is Punched
                2. All the selected items ClockOut Time is Not Punched */
            buttonLunchIn.text = getString(R.string.out_to_lunch)
            val isEnabledLunchIn = isLunchInEditable && !isClockInEditable && isClockOutEditable
            buttonLunchIn.isEnabled = isEnabledLunchIn && hasAllPreSavedClockInTime

            /* LunchOut Button will only be enabled in these cases:
                1. All the selected items LunchIn Time is Punched
                2. All the selected items ClockIn Time is Punched */
            buttonLunchOut.text = getString(R.string.back_to_work)
            val isEnabledLunchOut = isLunchOutEditable && !isLunchInEditable && !isClockInEditable
            buttonLunchOut.isEnabled = isEnabledLunchOut && hasAllPreSavedClockInTime && hasAllPreSavedLunchInTime

            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
            bottomSheetBackground.setTag(R.id.isMultiSelect, true)
        } else {
            closeBottomSheet()
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

    private fun clockInButtonClicked() {
        closeBottomSheet()

        val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
        if (isMultiSelect) {
            timeClockAttendanceAdapter.updateClockInTimeForSelectedPositions(System.currentTimeMillis())
            timeClockAttendanceAdapter.resetAnimationIndex()
            toggleSaveButton(timeClockAttendanceAdapter.getSelectedItemCount())
        } else {
            val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int
            timeClockAttendanceAdapter.updatePresentRecord(itemPosition, true)
            timeClockAttendanceAdapter.updateClockInTime(itemPosition, System.currentTimeMillis())
        }
    }

    private fun clockOutButtonClicked() {
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

    private fun lunchInButtonClicked() {
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

    private fun lunchOutButtonClicked() {
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

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.save_attendance_alert_message), fragmentActivity!!, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                imageViewCancel.performClick()
                val updatedData = timeClockAttendanceAdapter.getUpdatedData()
                timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct())
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                bottomSheetBackground.id -> closeBottomSheet()
                buttonClockIn.id -> clockInButtonClicked()
                buttonClockOut.id -> clockOutButtonClicked()
                buttonLunchIn.id -> lunchInButtonClicked()
                buttonLunchOut.id -> lunchOutButtonClicked()
                buttonSave.id -> showConfirmationDialog()
                buttonAddTime.id -> showTimePickerLayoutForMultipleLumpers()
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(fragmentActivity!!)
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

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
        recyclerViewLumpers.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
    }

    override fun showLumpersAttendance(lumperAttendanceList: ArrayList<LumperAttendanceData>) {
        this.lumperAttendanceList=lumperAttendanceList
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
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.attendance_saved_success_message),
            fragmentActivity!!, object : CustomDialogListener {
                override fun onConfirmClick() {
                    timeClockAttendancePresenter.fetchAttendanceList()
                }
            })
    }

    override fun showHeaderInfo(date: String) {
        this.date = date

        textViewTimeClockDate.text = UIUtils.getSpannedText(date)
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */
    override fun onAddTimeClick(lumperAttendanceData: LumperAttendanceData, itemPosition: Int) {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
            bottomSheetBackground.setTag(R.id.attendancePosition, itemPosition)
            bottomSheetBackground.setTag(R.id.isMultiSelect, false)
            var fullName= String.format("%s %s" ,lumperAttendanceData.firstName, lumperAttendanceData.lastName)
            textViewLName.text=fullName

            // Show Clock-In Time
            val clockInTime = convertDateStringToTime(PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.morningPunchIn)
            buttonClockIn.text = if (clockInTime.isNotEmpty()) clockInTime else getString(R.string.clock_in)
            val isClockInEditable = timeClockAttendanceAdapter.checkIfEditable(clockInTime.isNotEmpty(), ATTENDANCE_MORNING_PUNCH_IN, lumperAttendanceData.id!!)

            // Show Clock-Out Time
            val clockOutTime = convertDateStringToTime(PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.eveningPunchOut)
            buttonClockOut.text = if (clockOutTime.isNotEmpty()) clockOutTime else getString(R.string.clock_out)
            val isClockOutEditable = timeClockAttendanceAdapter.checkIfEditable(clockOutTime.isNotEmpty(), ATTENDANCE_EVENING_PUNCH_OUT, lumperAttendanceData.id!!)

            // Show Lunch-In Time
            val lunchInTime = convertDateStringToTime(PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.lunchPunchIn)
            buttonLunchIn.text = if (lunchInTime.isNotEmpty()) lunchInTime else getString(R.string.out_to_lunch)
            val isLunchInEditable = timeClockAttendanceAdapter.checkIfEditable(lunchInTime.isNotEmpty(), ATTENDANCE_LUNCH_PUNCH_IN, lumperAttendanceData.id!!)

            // Show Lunch-Out Time
            val lunchOutTime = convertDateStringToTime(PATTERN_API_RESPONSE, lumperAttendanceData.attendanceDetail?.lunchPunchOut)
            buttonLunchOut.text = if (lunchOutTime.isNotEmpty()) lunchOutTime else getString(R.string.back_to_work)
            val isLunchOutEditable = timeClockAttendanceAdapter.checkIfEditable(lunchOutTime.isNotEmpty(), ATTENDANCE_LUNCH_PUNCH_OUT, lumperAttendanceData.id!!)

            buttonClockIn.isEnabled = isClockInEditable

            /* ClockOut Button will only be enabled in these cases:
                1. ClockIn Time is Punched
                2. LunchIn Time is Not Punched
                3. LunchIn Time & LunchOut Time are punched */
            buttonClockOut.isEnabled = isClockOutEditable && !isClockInEditable && (isLunchInEditable || !(!isLunchInEditable && isLunchOutEditable))

            /* LunchIn Button will only be enabled in these cases:
                1. ClockIn Time is Punched
                2. ClockOut Time is Not Punched */
            buttonLunchIn.isEnabled = isLunchInEditable && !isClockInEditable && isClockOutEditable

            /* LunchOut Button will only be enabled in these cases:
                1. LunchIn Time is Punched
                2. ClockIn Time is Punched */
            buttonLunchOut.isEnabled = isLunchOutEditable && !isLunchInEditable && !isClockInEditable
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

    override fun onDataChanges(): Boolean {
        val updatedData = timeClockAttendanceAdapter.getUpdatedData()
        return updatedData.size > 0
    }
}

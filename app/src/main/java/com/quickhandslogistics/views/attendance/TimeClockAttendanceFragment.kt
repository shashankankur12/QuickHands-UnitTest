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
import com.quickhandslogistics.data.schedule.PastFutureDates
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
import com.quickhandslogistics.views.lumperSheet.LumperSheetFragment
import kotlinx.android.synthetic.main.bottom_sheet_add_attendance_time.*
import kotlinx.android.synthetic.main.content_time_clock_attendance.*
import kotlinx.android.synthetic.main.content_time_clock_attendance.editTextSearch
import kotlinx.android.synthetic.main.content_time_clock_attendance.imageViewCancel
import kotlinx.android.synthetic.main.content_time_clock_attendance.mainConstraintLayout
import kotlinx.android.synthetic.main.content_time_clock_attendance.textViewEmptyData
import kotlinx.android.synthetic.main.content_time_clock_bottom_sheet.*
import kotlinx.android.synthetic.main.content_time_clock_bottom_sheet_group.*
import kotlinx.android.synthetic.main.fragment_lumper_sheet.*
import kotlinx.android.synthetic.main.fragment_time_clock_attendance.*
import java.util.*
import kotlin.collections.ArrayList

class TimeClockAttendanceFragment : BaseFragment(), View.OnClickListener, TextWatcher,
    TimeClockAttendanceContract.View, TimeClockAttendanceContract.View.OnAdapterItemClickListener, TimeClockAttendanceContract.View.fragmentDataListener,
    View.OnLongClickListener, CalendarUtils.CalendarSelectionListener {

    private lateinit var timeClockAttendancePresenter: TimeClockAttendancePresenter
    private lateinit var timeClockAttendanceAdapter: TimeClockAttendanceAdapter
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private  var lumperAttendanceList: ArrayList<LumperAttendanceData> =ArrayList()
    private lateinit var availableDates: List<Date>
    private lateinit var date: String
    private var shift: String = ""
    private var dept: String = ""
    private var selectedTime: Long = 0
    private var datePosition: Int = 0
    private var isSavedState: Boolean = false
    private var pastFutureDates: ArrayList<PastFutureDates> = ArrayList()

    companion object {
        const val LUMPER_ATTENDANCE_LIST = "LUMPER_ATTENDANCE_LIST"
        const val TIME_CLOCK_DATE_SELECTED_HEADER = "TIME_CLOCK_DATE_SELECTED_HEADER"
        const val TIME_CLOCK_DEPT_SELECTED_HEADER = "TIME_CLOCK_DEPT_SELECTED_HEADER"
        const val TIME_CLOCK_SHIFT_SELECTED_HEADER = "TIME_CLOCK_SHIFT_SELECTED_HEADER"
        const val SELECTED_DATE_POSITION = "SELECTED_DATE_POSITION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeClockAttendancePresenter = TimeClockAttendancePresenter(this, resources, sharedPref)

        // Setup Calendar Dates
        selectedTime = Date().time
        availableDates = CalendarUtils.getPastCalendarDates()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_time_clock_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarTimeClock, availableDates, this)

        savedInstanceState?.also {
            isSavedState=true
            if (savedInstanceState.containsKey(LumperSheetFragment.SELECTED_DATE_POSITION)) {
                datePosition = savedInstanceState.getInt(LumperSheetFragment.SELECTED_DATE_POSITION)!!
                singleRowCalendarTimeClock.select(datePosition)
            }
            if (savedInstanceState.containsKey(TIME_CLOCK_DEPT_SELECTED_HEADER)) {
                dept = savedInstanceState.getString(TIME_CLOCK_DEPT_SELECTED_HEADER)!!
            }
            if (savedInstanceState.containsKey(TIME_CLOCK_SHIFT_SELECTED_HEADER)) {
                shift = savedInstanceState.getString(TIME_CLOCK_SHIFT_SELECTED_HEADER)!!

            }
            if (savedInstanceState.containsKey(TIME_CLOCK_DATE_SELECTED_HEADER)) {
                date = savedInstanceState.getString(TIME_CLOCK_DATE_SELECTED_HEADER)!!
                showHeaderInfo(date, shift, dept)
            }
            if (savedInstanceState.containsKey(LUMPER_ATTENDANCE_LIST)) {
                lumperAttendanceList =
                    savedInstanceState.getParcelableArrayList(LUMPER_ATTENDANCE_LIST)!!
                showLumpersAttendance(lumperAttendanceList, Date(selectedTime))
            }
        } ?: run {
            if (!ConnectionDetector.isNetworkConnected(activity)) {
                ConnectionDetector.createSnackBar(activity)
                return
            }
            isSavedState=false
            singleRowCalendarTimeClock.select(availableDates.size - 1)

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
        if (!dept.isNullOrEmpty())
            outState.putString(TIME_CLOCK_DEPT_SELECTED_HEADER, dept)
        if (!shift.isNullOrEmpty())
            outState.putString(TIME_CLOCK_SHIFT_SELECTED_HEADER, shift)
        if (datePosition != null)
            outState.putInt(LumperSheetFragment.SELECTED_DATE_POSITION, datePosition)
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
        buttonClockInGroup.setOnClickListener(this)
        buttonClockOut.setOnClickListener(this)
        buttonClockOutGroup.setOnClickListener(this)
        buttonLunchIn.setOnClickListener(this)
        buttonLunchInGroup.setOnClickListener(this)
        buttonLunchOut.setOnClickListener(this)
        buttonLunchOutGroup.setOnClickListener(this)
        bottomSheetBackground.setOnClickListener(this)
        mainCoordinatorLayout.setOnClickListener(this)

        buttonClockIn.setOnLongClickListener(this)
        buttonClockOut.setOnLongClickListener(this)
        buttonLunchIn.setOnLongClickListener(this)
        buttonLunchOut.setOnLongClickListener(this)
    }

    private fun invalidateEmptyView() {
        if (timeClockAttendanceAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (timeClockAttendanceAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message_time_clock)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_lumpers_list_info_message_time_clock)
        }
    }

    private fun closeBottomSheet() {
        AppUtils.hideSoftKeyboard(activity!!)
        bottom_sheet.visibility=View.GONE
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackground.visibility = View.GONE
    }

    private fun showTimePickerLayoutForMultipleLumpers() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottom_sheet.visibility=View.VISIBLE
            constraintRootSingle.visibility=View.GONE
            constraintRootGroup.visibility=View.VISIBLE
            var isClockInEditable = true
            var isClockOutEditable = true
            var isLunchInEditable = true
            var isLunchOutEditable = true

            var hasAllPreSavedClockInTime = true
            var hasAllPreSavedClockOutTime = true
            var hasAllPreSavedLunchInTime = true
            var hasAllPreSavedLunchOutTime = true

            val list = timeClockAttendanceAdapter.getSelectedItems()
            textViewLNameGroup.text=getString(R.string.bulk_action)
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

            buttonClockInGroup.text = getString(R.string.clock_in)
            buttonClockInGroup.isEnabled = isClockInEditable

            /* ClockOut Button will only be enabled in these cases:
                1. All the selected items ClockIn Time is Punched
                2. All the selected items LunchIn Time is Not Punched
                3. All the selected items LunchIn Time & LunchOut Time are both punched */
            buttonClockOutGroup.text = getString(R.string.clock_out)
            val isEnabledClockOut = isClockOutEditable && !isClockInEditable && (isLunchInEditable || !(!isLunchInEditable && isLunchOutEditable))
            buttonClockOutGroup.isEnabled = isEnabledClockOut && hasAllPreSavedClockInTime &&
                    ((hasAllPreSavedLunchInTime && hasAllPreSavedLunchOutTime) || (!hasAllPreSavedLunchInTime && !hasAllPreSavedLunchOutTime))

            /* LunchIn Button will only be enabled in these cases:
                1. All the selected items ClockIn Time is Punched
                2. All the selected items ClockOut Time is Not Punched */
            buttonLunchInGroup.text = getString(R.string.out_to_lunch)
            val isEnabledLunchIn = isLunchInEditable && !isClockInEditable && isClockOutEditable
            buttonLunchInGroup.isEnabled = isEnabledLunchIn && hasAllPreSavedClockInTime

            /* LunchOut Button will only be enabled in these cases:
                1. All the selected items LunchIn Time is Punched
                2. All the selected items ClockIn Time is Punched */
            buttonLunchOutGroup.text = getString(R.string.back_to_work)
            val isEnabledLunchOut = isLunchOutEditable && !isLunchInEditable && !isClockInEditable
            buttonLunchOutGroup.isEnabled = isEnabledLunchOut && hasAllPreSavedClockInTime && hasAllPreSavedLunchInTime

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
            buttonSave.visibility=if (timeClockAttendanceAdapter.getUpdatedData().size > 0) View.VISIBLE else View.GONE
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

            imageViewCancel.performClick()
            val updatedData = timeClockAttendanceAdapter.getUpdatedData()
            timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct(), Date(selectedTime))
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

            imageViewCancel.performClick()
            val updatedData = timeClockAttendanceAdapter.getUpdatedData()
            timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct(), Date(selectedTime))
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

            imageViewCancel.performClick()
            val updatedData = timeClockAttendanceAdapter.getUpdatedData()
            timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct(), Date(selectedTime))
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

            imageViewCancel.performClick()
            val updatedData = timeClockAttendanceAdapter.getUpdatedData()
            timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct(), Date(selectedTime))
        }
    }

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.save_attendance_alert_message), fragmentActivity!!, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                imageViewCancel.performClick()
                val updatedData = timeClockAttendanceAdapter.getUpdatedData()
                timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct(), Date(selectedTime))
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        view?.let {
            when (view.id) {
                bottomSheetBackground.id -> closeBottomSheet()
                mainCoordinatorLayout.id -> {AppUtils.hideSoftKeyboard(activity!!)}
                buttonClockIn.id -> clockInButtonClicked()
                buttonClockInGroup.id -> clockInButtonClicked()
                buttonClockOut.id -> clockOutButtonClicked()
                buttonClockOutGroup.id -> clockOutButtonClicked()
                buttonLunchIn.id -> lunchInButtonClicked()
                buttonLunchInGroup.id -> lunchInButtonClicked()
                buttonLunchOut.id -> lunchOutButtonClicked()
                buttonLunchOutGroup.id -> lunchOutButtonClicked()
                buttonSave.id -> showConfirmationDialog()
                buttonAddTime.id -> showTimePickerLayoutForMultipleLumpers()
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(fragmentActivity!!)
                }
            }
        }
    }

    override fun onLongClick(view: View?): Boolean {
        view?.let {
            return when (view.id) {
                buttonClockIn.id -> {
                    val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
                    if (!isMultiSelect && !timeClockAttendanceAdapter.getUpdatedData().isEmpty())
                    clockButtonClear(buttonClockIn.id)
                    return true
                }
                buttonClockOut.id -> {
                    val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
                    if (!isMultiSelect && !timeClockAttendanceAdapter.getUpdatedData().isEmpty())
                    clockButtonClear(buttonClockOut.id)
                    return true
                }
                buttonLunchIn.id -> {
                    val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
                    if (!isMultiSelect &&!timeClockAttendanceAdapter.getUpdatedData().isEmpty())
                    clockButtonClear(buttonLunchIn.id)
                    return true
                }
                buttonLunchOut.id -> {
                    val isMultiSelect = bottomSheetBackground.getTag(R.id.isMultiSelect) as Boolean
                    if (!isMultiSelect && !timeClockAttendanceAdapter.getUpdatedData().isEmpty())
                    clockButtonClear(buttonLunchOut.id)
                    return true
                }
                else -> {
                    false
                }
            }
        }
        return false
    }


    private fun clockButtonClear(id: Int) {
        CustomProgressBar.getInstance().showLeaveDialog(getString(R.string.clear_time_alert_message), fragmentActivity!!, object : CustomDialogWarningListener { override fun onConfirmClick() {
                    closeBottomSheet()
                    val itemPosition = bottomSheetBackground.getTag(R.id.attendancePosition) as Int

                    when (id) {
                        buttonClockIn.id -> {
                            timeClockAttendanceAdapter.clearPresentRecord(itemPosition, false)
                            timeClockAttendanceAdapter.clearClockInTime(itemPosition)
                        }
                        buttonClockOut.id -> {
                            timeClockAttendanceAdapter.clearClockOutTime(itemPosition)
                        }
                        buttonLunchIn.id -> {
                            timeClockAttendanceAdapter.clearLunchInTime(itemPosition)
                        }
                        buttonLunchOut.id -> {
                            timeClockAttendanceAdapter.clearLunchOutTime(itemPosition)
                        }
                    }
                }

                override fun onCancelClick() {
                }
            })
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
        if (message.equals(getString(R.string.attendece_api_error_message))){
            recyclerViewLumpers.visibility = View.VISIBLE
            textViewEmptyData.visibility = View.GONE
        }else {
        recyclerViewLumpers.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        }
    }

    override fun showPastFutureDate(pastFutureDate: ArrayList<PastFutureDates>) {
        isSavedState = true
        this.pastFutureDates= pastFutureDate
        CalendarUtils.pastFutureDatesNew=pastFutureDates
        singleRowCalendarTimeClock.adapter?.notifyDataSetChanged()
    }

    override fun showLumpersAttendance(
        lumperAttendanceList: ArrayList<LumperAttendanceData>,
        selectedTime: Date
    ) {
        this.lumperAttendanceList=lumperAttendanceList
        this.selectedTime=selectedTime.time
        timeClockAttendanceAdapter.updateList(lumperAttendanceList,  this.selectedTime)
        if (lumperAttendanceList.size > 0) {
            textViewEmptyData.visibility = View.GONE
            recyclerViewLumpers.visibility = View.VISIBLE
        } else {
            recyclerViewLumpers.visibility = View.GONE
            textViewEmptyData.visibility = View.VISIBLE
        }
        buttonSave.visibility= View.GONE
    }

    override fun showDataSavedMessage() {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }

        timeClockAttendancePresenter.fetchAttendanceList(Date(selectedTime))
//        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.attendance_saved_success_message),
//            fragmentActivity!!, object : CustomDialogListener {
//                override fun onConfirmClick() {
//                    timeClockAttendancePresenter.fetchAttendanceList()
//                }
//            })
    }

    override fun showHeaderInfo(date: String, shift: String, dept: String) {
        this.date = date
        this.shift = shift
        this.dept = dept

        textViewTimeClockDate.text = UIUtils.getSpannedText(date)
        textViewTimeClockDate.visibility = View.GONE
        textViewTimeClockShift.visibility = View.GONE
        textViewTimeClockDept.visibility = View.GONE
//        textViewTimeClockShift.text = UIUtils.getSpannedText(shift)
//        textViewTimeClockDept.text = UIUtils.getSpannedText(dept)
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Adapter Listeners */
    override fun onAddTimeClick(lumperAttendanceData: LumperAttendanceData, itemPosition: Int) {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottom_sheet.visibility=View.VISIBLE
            constraintRootSingle.visibility=View.VISIBLE
            constraintRootGroup.visibility=View.GONE
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

    override fun onSaveNote() {
        imageViewCancel.performClick()
        val updatedData = timeClockAttendanceAdapter.getUpdatedData()
        timeClockAttendancePresenter.saveAttendanceDetails(updatedData.values.distinct(), Date(selectedTime))
    }

    override fun onDataChanges(): Boolean {
        val updatedData = timeClockAttendanceAdapter.getUpdatedData()
        return updatedData.size > 0
    }

    override fun onSelectCalendarDate(date: Date, selected: Boolean, position: Int) {
        if (!ConnectionDetector.isNetworkConnected(activity)) {
            ConnectionDetector.createSnackBar(activity)
            return
        }
        if (!isSavedState)
            timeClockAttendancePresenter.fetchAttendanceList(date)

        isSavedState = false
        datePosition = position

    }
}

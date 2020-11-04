package com.quickhandslogistics.views.scheduleTime

import android.app.Activity
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
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
import com.quickhandslogistics.adapters.scheduleTime.ScheduleTimeAdapter
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeNoteRequest
import com.quickhandslogistics.presenters.scheduleTime.ScheduleTimePresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.ScheduleUtils.getShiftDetailString
import com.quickhandslogistics.views.BaseFragment
import com.quickhandslogistics.views.DashBoardActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_LUMPERS_COUNT
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_LIST
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SCHEDULED_TIME_NOTES
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.bottom_sheet_create_lumper_request.textViewTitle
import kotlinx.android.synthetic.main.bottom_sheet_schdule_time_fragement.*
import kotlinx.android.synthetic.main.content_schedule_time_fragment.*
import kotlinx.android.synthetic.main.fragment_schedule_time.*
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimeFragment : BaseFragment(), TextWatcher, View.OnClickListener, ScheduleTimeContract.View, ScheduleTimeContract.View.OnAdapterItemClickListener, CalendarUtils.CalendarSelectionListener {

    private var onFragmentInteractionListener: DashBoardContract.View.OnFragmentInteractionListener? = null

    private var scheduleTimeSelectedDate: String? = null

    private var selectedTime: Long = 0
    private var timeInMillis: Long = 0
    private var selectedDatePosition: Int = 0
    private var datePosition: Int = 0
    private var isPastDate: Boolean = false

    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var availableDates: List<Date>
    private var scheduleTimeDetailList: ArrayList<ScheduleTimeDetail> = ArrayList()
    private var scheduleTimeNotes: String? = null
    private var dateString: String? = null
    private var isSavedState: Boolean = false
    private var selectedDate: Date = Date()
    private var tempLumperIds: ArrayList<String> = ArrayList()

    private lateinit var scheduleTimeAdapter: ScheduleTimeAdapter
    private lateinit var scheduleTimePresenter: ScheduleTimePresenter

    companion object {
        const val SCHEDULE_TIME_DETAIL = "SCHEDULE_TIME_DETAIL"
        const val DATE_SELECTED_SCHEDULE_TIME = "DATE_SELECTED_SCHEDULE_TIME"
        const val TEMP_LUMPER_SCHEDULE_TIME = "TEMP_LUMPER_SCHEDULE_TIME"
        const val NOTE_SCHEDULE_TIME = "NOTE_SCHEDULE_TIME"
        const val DATE_HEADER_SCHEDULE_TIME = "DATE_HEADER_SCHEDULE_TIME"
        const val SELECTED_DATE_POSITION_SCHEDULE_TIME = "SELECTED_DATE_POSITION_SCHEDULE_TIME"
        const val EDIT_SCHEDULE_LUMPER = "EDIT_SCHEDULE_LUMPER"
        const val CANCEL_SCHEDULE_LUMPER = "CANCEL_SCHEDULE_LUMPER"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DashBoardContract.View.OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleTimePresenter = ScheduleTimePresenter(this, resources, sharedPref)

        arguments?.let { bundle ->
            scheduleTimeSelectedDate = bundle.getString(DashBoardActivity.ARG_SCHEDULE_TIME_SELECTED_DATE)
        }

        // Setup Calendar Dates
        selectedTime = Date().time
        val pair = CalendarUtils.getPastFutureCalendarDates()
        availableDates = pair.first
        val currentDatePosition = pair.second

        selectedDatePosition = CalendarUtils.getSelectedDatePosition(availableDates, scheduleTimeSelectedDate)
        // Check if we didn't get the date position
        if (selectedDatePosition == -1) {

            // Check if we get the current date position
            selectedDatePosition = if (currentDatePosition == 0) {
                availableDates.size - 1
            } else {
                currentDatePosition
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetScheduleLumper)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        recyclerViewScheduleTime.apply {
            val linearLayoutManager = LinearLayoutManager(fragmentActivity!!)
            layoutManager = linearLayoutManager
            val dividerItemDecoration = DividerItemDecoration(fragmentActivity!!, linearLayoutManager.orientation)
            addItemDecoration(dividerItemDecoration)
            scheduleTimeAdapter = ScheduleTimeAdapter(this@ScheduleTimeFragment)
            adapter = scheduleTimeAdapter
        }

        scheduleTimeAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateEmptyView()
            }
        })

        invalidateScheduleButton()

        editTextSearch.addTextChangedListener(this)
        imageViewCancel.setOnClickListener(this)
        buttonScheduleLumpers.setOnClickListener(this)
        buttonRequestLumpers.setOnClickListener(this)
        bottomSheetBackground.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)
        buttonUpdate.setOnClickListener(this)
        textViewScheduleLumperTime.setOnClickListener(this)
        buttonYes.setOnClickListener(this)
        buttonNo.setOnClickListener(this)

        CalendarUtils.initializeCalendarView(fragmentActivity!!, singleRowCalendarScheduleTime, availableDates, this)
        savedInstanceState?.also {
            isSavedState = true
            if (savedInstanceState.containsKey(DATE_HEADER_SCHEDULE_TIME)) {
                dateString = savedInstanceState.getString(DATE_HEADER_SCHEDULE_TIME)!!
                showDateString(dateString!!)
            }
            if (savedInstanceState.containsKey(NOTE_SCHEDULE_TIME)) {
                scheduleTimeNotes = savedInstanceState.getString(NOTE_SCHEDULE_TIME)!!
                showNotesData(scheduleTimeNotes!!)
            }
            if (savedInstanceState.containsKey(TEMP_LUMPER_SCHEDULE_TIME)) {
                tempLumperIds = savedInstanceState.getStringArrayList(TEMP_LUMPER_SCHEDULE_TIME)!!

            }
            if (savedInstanceState.containsKey(SELECTED_DATE_POSITION_SCHEDULE_TIME)) {
                datePosition = savedInstanceState.getInt(SELECTED_DATE_POSITION_SCHEDULE_TIME)
                singleRowCalendarScheduleTime.select(datePosition)
            }
            if (savedInstanceState.containsKey(DATE_SELECTED_SCHEDULE_TIME)) {
                selectedDate =
                    savedInstanceState.getSerializable(DATE_SELECTED_SCHEDULE_TIME) as Date
            }
            if (savedInstanceState.containsKey(SCHEDULE_TIME_DETAIL)) {
                scheduleTimeDetailList =
                    savedInstanceState.getParcelableArrayList(SCHEDULE_TIME_DETAIL)!!
                showScheduleTimeData(
                    selectedDate,
                    scheduleTimeDetailList,
                    tempLumperIds,
                    scheduleTimeNotes
                )
            }
        } ?: run {
            isSavedState = false
            singleRowCalendarScheduleTime.select(selectedDatePosition)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (scheduleTimeDetailList != null)
            outState.putParcelableArrayList(SCHEDULE_TIME_DETAIL, scheduleTimeDetailList)
        if (selectedDate != null)
            outState.putSerializable(DATE_SELECTED_SCHEDULE_TIME, selectedDate)
        if (tempLumperIds != null)
            outState.putStringArrayList(TEMP_LUMPER_SCHEDULE_TIME, tempLumperIds)
        if (scheduleTimeNotes != null)
            outState.putString(NOTE_SCHEDULE_TIME, scheduleTimeNotes)
        if (dateString != null)
            outState.putString(DATE_HEADER_SCHEDULE_TIME, dateString)
        if (datePosition != null)
            outState.putInt(SELECTED_DATE_POSITION_SCHEDULE_TIME, datePosition)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        scheduleTimePresenter.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            if (singleRowCalendarScheduleTime.getSelectedDates().isNotEmpty()) {
                scheduleTimePresenter.getSchedulesTimeByDate(singleRowCalendarScheduleTime.getSelectedDates()[0])
            }
        }
    }

    private fun invalidateScheduleButton() {
        buttonScheduleLumpers.visibility = if (isPastDate) View.GONE else View.VISIBLE
        buttonRequestLumpers.visibility = if (isPastDate) View.GONE else View.VISIBLE
    }

    private fun invalidateEmptyView() {
        if (scheduleTimeAdapter.itemCount == 0) {
            textViewEmptyData.visibility = View.VISIBLE
            if (scheduleTimeAdapter.isSearchEnabled()) {
                textViewEmptyData.text = getString(R.string.no_record_found_info_message)
            } else {
                getString(R.string.empty_schedule_time_list_info_message)
            }
        } else {
            textViewEmptyData.visibility = View.GONE
            textViewEmptyData.text = getString(R.string.empty_schedule_time_list_info_message)
        }
    }

    private fun showBottomSheetWithData(record: ScheduleTimeDetail, operation: String) {
        when(operation){
            EDIT_SCHEDULE_LUMPER->{
                record.let {
                    textViewTitle.text = getString(R.string.edit_schedule_lumper)
                    linearLayoutCancelLumper.visibility=View.GONE
                    linearLayoutEditLumper.visibility=View.VISIBLE
                    setLumperDetails(it)
                }

            }
            CANCEL_SCHEDULE_LUMPER->{
                record.let {
                    textViewTitle.text = getString(R.string.cancel_lumper)
                    linearLayoutCancelLumper.visibility=View.VISIBLE
                    linearLayoutEditLumper.visibility=View.GONE
                    textViewheaderTitle.text =ScheduleUtils.getCancelHeaderDetails(it, getString(R.string.cancel_header_details))
                    buttonYes.setTag(R.id.requestLumperId, it.lumperInfo!!.id)
                    textViewTitle.setTag(R.id.cancelLumperDay, it.reportingTimeAndDay!!)
                }
            }
        }

        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackground.visibility = View.VISIBLE
        } else {
            closeBottomSheet()
        }
    }

    private fun setLumperDetails(record: ScheduleTimeDetail) {
        UIUtils.showEmployeeProfileImage(context!!, record.lumperInfo!!.profileImageUrl, circleImageViewLumperProfile)
        UIUtils.updateProfileBorder(context!!, tempLumperIds.contains(record.lumperInfo!!.id), circleImageViewLumperProfile)
        textViewLumpersName.setText( UIUtils.getEmployeeFullName(record.lumperInfo!!))
        textViewLumperId.text = UIUtils.getDisplayEmployeeID(record.lumperInfo!!)
        timeInMillis= DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, record.reportingTimeAndDay)
        textViewScheduleLumperTime.text = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, record.reportingTimeAndDay)
        buttonUpdate.setTag(R.id.requestLumperId, record.lumperInfo!!.id)
        editTextNote.setText(record.notesForLumper)
        editTextNoteGroup.setText(scheduleTimeNotes)
        textViewTitle.setTag(R.id.cancelLumperDay, record.reportingTimeAndDay!!)
    }

    private fun editTime() {
        val calendar = Calendar.getInstance()
        if (timeInMillis > 0) {
            calendar.timeInMillis = timeInMillis
        } else {
            calendar.timeInMillis = selectedTime
        }
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context, OnTimeSetListener { view, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                textViewScheduleLumperTime.text=DateUtils.convertMillisecondsToTimeString(calendar.timeInMillis)
                timeInMillis=calendar.timeInMillis
            }, mHour, mMinute, false
        ).show()
    }

    private fun closeBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBackground.visibility = View.GONE
    }
    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                imageViewCancel.id -> {
                    editTextSearch.setText("")
                    AppUtils.hideSoftKeyboard(fragmentActivity!!)
                }
                buttonScheduleLumpers.id -> {
                    val bundle = Bundle()
                    bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
                    bundle.putParcelableArrayList(ARG_SCHEDULED_TIME_LIST, scheduleTimeDetailList)
                    bundle.putString(ARG_SCHEDULED_TIME_NOTES, scheduleTimeNotes)
                    startIntent(EditScheduleTimeActivity::class.java, bundle = bundle, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                }
                buttonRequestLumpers.id -> {
                    val bundle = Bundle()
                    bundle.putLong(ARG_SELECTED_DATE_MILLISECONDS, selectedTime)
                    bundle.putInt(ARG_SCHEDULED_LUMPERS_COUNT, scheduleTimeDetailList.size)
                    startIntent(RequestLumpersActivity::class.java, bundle = bundle)
                }
                bottomSheetBackground.id-> closeBottomSheet()
                buttonCancel.id-> closeBottomSheet()
                buttonNo.id-> closeBottomSheet()
                buttonUpdate.id-> {showConfirmationDialog(EDIT_SCHEDULE_LUMPER)}
                textViewScheduleLumperTime.id-> {
                    editTime()
                }
                buttonYes.id-> {showConfirmationDialog(CANCEL_SCHEDULE_LUMPER)}
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        text?.let {
            scheduleTimeAdapter.setSearchEnabled(text.isNotEmpty(), text.toString())
            imageViewCancel.visibility = if (text.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showConfirmationDialog(type: String) {
        var message =""

        if (type.equals(CANCEL_SCHEDULE_LUMPER))
            message=getString(R.string.cancel_request_lumper)
        else if(type.equals(EDIT_SCHEDULE_LUMPER))
            message=getString(R.string.edit_schedule_lumper_message)

        CustomProgressBar.getInstance().showWarningDialog(message, context!!, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                closeBottomSheet()
                if (type.equals(CANCEL_SCHEDULE_LUMPER)) {
                    val requestLumperId = buttonYes.getTag(R.id.requestLumperId) as String?
                    val requestLumperDate = textViewTitle.getTag(R.id.cancelLumperDay) as String?
                    if (!requestLumperId.isNullOrEmpty() && !requestLumperDate.isNullOrEmpty()) {
                        scheduleTimePresenter.cancelScheduleLumpers(requestLumperId, selectedDate/*DateUtils.getDateFromDateString(DateUtils.PATTERN_API_RESPONSE, requestLumperDate)*/)
                    }
                } else if(type.equals(EDIT_SCHEDULE_LUMPER)){
                    val requestLumperId = buttonUpdate.getTag(R.id.requestLumperId) as String?
                    val requestLumperDate = textViewTitle.getTag(R.id.cancelLumperDay) as String?
                    val individualNote = editTextNote.text.toString()
                    val groupNote = editTextNoteGroup.text.toString()
                    val request = ScheduleTimeNoteRequest(individualNote,groupNote)
                    if (!requestLumperId.isNullOrEmpty() && !requestLumperDate.isNullOrEmpty()) {
                        scheduleTimePresenter.editScheduleLumpers(requestLumperId,selectedDate /*DateUtils.getDateFromDateString(DateUtils.PATTERN_API_RESPONSE, requestLumperDate)*/,timeInMillis!!, request )
                    }
                }
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Presenter Listeners */
    override fun showDateString(dateString: String) {
        this.dateString = dateString
        textViewDate.text = UIUtils.getSpannedText(dateString)
        textViewDate.visibility =View.GONE
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?

        if (leadProfile?.buildingDetailData != null) {
            textViewBuildingName.text = leadProfile?.buildingDetailData?.buildingName!!.capitalize()
            textViewDept.text = UIUtils.getSpannableText(getString(R.string.bar_header_dept), UIUtils.getDisplayEmployeeDepartment(leadProfile))
            textViewShift.text = UIUtils.getSpannableText(getString(R.string.bar_header_shift), leadProfile.shift?.capitalize().toString())
        } else {
            layoutWorkScheduleInfo.visibility = View.GONE
            appBarView.visibility = View.GONE
        }

        if(!leadProfile?.shift.isNullOrEmpty()){
            textViewHeaderShift.text=UIUtils.getSpannedText(getShiftDetailString(leadProfile))
            textViewHeaderShift.visibility=View.GONE
        } else textViewHeaderShift.visibility=View.GONE

        if (!leadProfile?.department.isNullOrEmpty()){
            textViewHeaderDept.text=UIUtils.getSpannedText("${ ResourceManager.getInstance().getString(R.string.dept_bold)} ${ UIUtils.getDisplayEmployeeDepartment(leadProfile)}")
            textViewHeaderDept.visibility=View.GONE
        }else textViewHeaderDept.visibility=View.GONE
    }

    override fun showAPIErrorMessage(message: String) {
        recyclerViewScheduleTime.visibility = View.GONE
        textViewEmptyData.visibility = View.VISIBLE
        SnackBarFactory.createSnackBar(fragmentActivity!!, mainConstraintLayout, message)
    }

    override fun showScheduleTimeData(mSelectedDate: Date, mScheduleTimeDetailList: ArrayList<ScheduleTimeDetail>, mTempLumperIds: ArrayList<String>, notes: String?) {
        this.scheduleTimeDetailList = mScheduleTimeDetailList
        this.selectedDate = mSelectedDate
        this.tempLumperIds = mTempLumperIds
        this.scheduleTimeNotes = notes

        selectedTime = selectedDate.time
        isPastDate = !DateUtils.isFutureDate(selectedTime) && !DateUtils.isCurrentDate(selectedTime)
        invalidateScheduleButton()

        scheduleTimeAdapter.updateLumpersData(scheduleTimeDetailList, tempLumperIds,scheduleTimeNotes,isPastDate)

        if (!scheduleTimeSelectedDate.isNullOrEmpty()) {
            scheduleTimeSelectedDate = ""
            buttonRequestLumpers.performClick()
        }
    }

    override fun showNotesData(notes: String?) {
        scheduleTimeNotes = notes
//        if (!scheduleTimeNotes.isNullOrEmpty()) {
//            onFragmentInteractionListener?.invalidateScheduleTimeNotes(scheduleTimeNotes!!)
//        } else {
//            onFragmentInteractionListener?.invalidateScheduleTimeNotes("")
//        }
    }

    override fun showSuccessDialog(message: String, date: Date) {
        CustomProgressBar.getInstance().showSuccessDialog(message, context!!, object : CustomDialogListener {
            override fun onConfirmClick() {
                scheduleTimePresenter.getSchedulesTimeByDate(date)
            }
        })
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Calendar Listeners */
    override fun onSelectCalendarDate(date: Date, selected: Boolean, position: Int) {
        if (!isSavedState)
            scheduleTimePresenter.getSchedulesTimeByDate(date)
        isSavedState = false
        datePosition = position
    }

    /** Adapter Listeners */
    override fun onEditTimeClick(adapterPosition: Int, timeInMillis: Long, details: ScheduleTimeDetail) {
        var timeInMillis = DateUtils.convertUTCDateStringToMilliseconds(
            DateUtils.PATTERN_API_RESPONSE,
            scheduleTimeDetailList[adapterPosition].reportingTimeAndDay
        )
        if (DateUtils.isTwoHourFromCurrentTime(timeInMillis))
            showBottomSheetWithData(details, EDIT_SCHEDULE_LUMPER)
        else
            CustomProgressBar.getInstance()
                .showMessageDialog(getString(R.string.edit_schedule_lumper_invalidate_message), context!!)
    }

    override fun onScheduleNoteClick(
        adapterPosition: Int, notes: String?, item: ScheduleTimeDetail) {
        CustomeDialog.showLeadNoteDialog(activity, "Lead Note ", item.notesForLumper,notes)
//        CustomProgressBar.getInstance().showInfoDialog(getString(R.string.note), notes, fragmentActivity!!)
    }

    override fun onAddRemoveClick(adapterPosition: Int, details: ScheduleTimeDetail) {
        var timeInMillis = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, scheduleTimeDetailList[adapterPosition].reportingTimeAndDay)
        if (DateUtils.isTwoHourFromCurrentTime(timeInMillis))
            showBottomSheetWithData(details, CANCEL_SCHEDULE_LUMPER)
        else
            CustomProgressBar.getInstance()
                .showMessageDialog(getString(R.string.cancel_schedule_lumper_invalidate_message), context!!)
    }
}

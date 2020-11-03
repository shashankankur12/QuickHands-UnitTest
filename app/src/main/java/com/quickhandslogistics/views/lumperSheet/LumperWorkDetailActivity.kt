package com.quickhandslogistics.views.lumperSheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.lumperSheet.LumperWorkDetailAdapter
import com.quickhandslogistics.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.lumperSheet.LumperWorkDetailPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.DateUtils.Companion.getDateTimeCalculeted
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.common.AddSignatureActivity
import com.quickhandslogistics.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.views.lumperSheet.LumperSheetFragment.Companion.ARG_LUMPER_INFO
import com.quickhandslogistics.views.lumperSheet.LumperSheetFragment.Companion.TEMP_LUMPER
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETER_VALUES
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.activity_lumper_work_detail.*
import kotlinx.android.synthetic.main.activity_lumper_work_detail.mainConstraintLayout
import kotlinx.android.synthetic.main.bottom_sheet_lumper_work_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_lumper_work_detail.textViewLunchTime
import kotlinx.android.synthetic.main.content_add_lumper_time_work_sheet_item.*
import kotlinx.android.synthetic.main.content_lumper_work_detail.*
import kotlinx.android.synthetic.main.content_lumper_work_detail.buttonCancelRequest
import kotlinx.android.synthetic.main.content_lumper_work_detail.buttonSave
import kotlinx.android.synthetic.main.content_lumper_work_detail.circleImageViewProfile
import kotlinx.android.synthetic.main.content_lumper_work_detail.layoutSaveCancelButton
import kotlinx.android.synthetic.main.content_lumper_work_detail.textViewEmployeeId
import kotlinx.android.synthetic.main.content_lumper_work_detail.textViewLumperName
import kotlinx.android.synthetic.main.content_lumper_work_detail.textViewShiftTime
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.bottom_sheet_lumper_work_detail.constraintLayoutBottomSheetRequestLumpers as constraintLayoutBottomSheetRequestLumpers1
import kotlinx.android.synthetic.main.content_add_lumper_time_work_sheet_item.linearLayout as linearLayout1
import kotlinx.android.synthetic.main.content_lumper_work_detail.viewAttendanceStatus as viewAttendanceStatus1

class LumperWorkDetailActivity : BaseActivity(), View.OnClickListener, LumperWorkDetailContract.View,
    LumperWorkDetailContract.View.OnAdapterItemClickListener, TextWatcher {

    private var signatureFilePath = ""
    private var selectedTime: Long = 0
    private var lumpersInfo: LumpersInfo? = null

    private lateinit var lumperWorkDetailPresenter: LumperWorkDetailPresenter
    private lateinit var lumperWorkDetailAdapter: LumperWorkDetailAdapter
    private  var lumperDaySheetList: ArrayList<LumperDaySheet> = ArrayList()
    private  var lumperAttendanceData: AttendanceDetail = AttendanceDetail()
//    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var updateData: HashMap<String, AttendanceDetail> = HashMap()
    private var tempLumperIds: ArrayList<String> =ArrayList()

    companion object {
        const val LUMPER_WORK_DETAIL = "LUMPER_WORK_DETAIL"
        const val LUMPER_SIGNATURE = "LUMPER_SIGNATURE"
        const val LUMPER_EDIT_TIEM= "LUMPER_EDIT_TIEM"
        const val LUMPER_EDIT_NOTE = "LUMPER_EDIT_NOTE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_lumper_work_detail)
        setupToolbar(getString(R.string.lumper_work_detail))

        intent.extras?.let { bundle ->
            lumpersInfo = bundle.getParcelable(ARG_LUMPER_INFO) as LumpersInfo?
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
            tempLumperIds = bundle.getStringArrayList(TEMP_LUMPER) as ArrayList<String>
        }

        initializeUI()

        lumperWorkDetailPresenter = LumperWorkDetailPresenter(this, resources)

        savedInstanceState?.also {
            if (savedInstanceState.containsKey(LUMPER_WORK_DETAIL)) {
                lumperDaySheetList= savedInstanceState.getParcelableArrayList(LUMPER_WORK_DETAIL)!!
                showLumperWorkDetails(lumperDaySheetList, lumperAttendanceData)
            }
            if (savedInstanceState.containsKey(LUMPER_SIGNATURE)) {
                signatureFilePath= savedInstanceState.getString(LUMPER_SIGNATURE)!!
                showLocalSignatureOnUI(signatureFilePath)
            }
        } ?: run {
            lumperWorkDetailPresenter.getLumperWorkDetails(getDefaultOrValue(lumpersInfo?.lumperId), Date(selectedTime))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstant.REQUEST_CODE_CHANGED && resultCode == Activity.RESULT_OK) {
            data?.let {
                val signatureFilePath = data.getStringExtra(AddSignatureActivity.ARG_SIGNATURE_FILE_PATH)
                showLocalSignatureOnUI(signatureFilePath)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (lumperDaySheetList != null)
            outState.putParcelableArrayList(LUMPER_WORK_DETAIL, lumperDaySheetList)
        if (!TextUtils.isEmpty(signatureFilePath))
            outState.putString(LUMPER_SIGNATURE, signatureFilePath)
        super.onSaveInstanceState(outState)
    }

    private fun initializeUI() {
//        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetRequestLumpers1)
//        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        lumpersInfo?.let { employeeData ->
            UIUtils.showEmployeeProfileImage(activity, employeeData.lumperImageUrl, circleImageViewProfile)
            UIUtils.updateProfileBorder(this, tempLumperIds.contains(employeeData.lumperId), circleImageViewProfile)
            textViewLumperName.text = getDefaultOrValue(employeeData.lumperName)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData.lumperEmployeeId)
            viewAttendanceStatus.setBackgroundResource(if (employeeData.isPresent!!) R.drawable.online_dot else R.drawable.offline_dot)
        }

        recyclerViewLumperWork.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            lumperWorkDetailAdapter = LumperWorkDetailAdapter(resources, sharedPref, this@LumperWorkDetailActivity)
            adapter = lumperWorkDetailAdapter
        }

        textViewAddSignature.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
        buttonCancelRequest.setOnClickListener(this)
//        layoutEditTimeClock.setOnClickListener(this)
//        layoutEditLumerNote.setOnClickListener(this)
//        bottomSheetBackground.setOnClickListener(this)
//        buttonSubmit.setOnClickListener(this)
//        buttonCancelNote.setOnClickListener(this)
//        buttonClockIn.setOnClickListener(this)
//        buttonClockOut.setOnClickListener(this)
//        buttonLunchIn.setOnClickListener(this)
//        buttonLunchOut.setOnClickListener(this)
//        editTextLumpersRequired.addTextChangedListener(this)
    }

    private fun showLocalSignatureOnUI(signatureFilePath: String?) {
        if (!signatureFilePath.isNullOrEmpty()) {
            this.signatureFilePath = signatureFilePath
            Glide.with(activity).load(File(signatureFilePath)).into(imageViewSignature)
            imageViewSignature.visibility = View.VISIBLE
            textViewAddSignature.visibility = View.GONE
            layoutSaveCancelButton.visibility = View.VISIBLE
            isDataSave(false)
        } else {
            this.signatureFilePath = ""
            imageViewSignature.visibility = View.GONE
            textViewAddSignature.visibility = View.VISIBLE
            layoutSaveCancelButton.visibility = View.GONE
            isDataSave(true)
        }
    }

    private fun updateUIVisibility(signed: Boolean, currentDate: Boolean, inCompleteWorkItemsCount: Int, signatureUrl: String? = "") {
        textViewSignature.visibility = View.GONE

        if (signed) {
            imageViewSignature.visibility = View.VISIBLE
            Glide.with(activity).load(signatureUrl).into(imageViewSignature)
        } else {
            imageViewSignature.visibility = View.GONE
            Glide.with(activity).clear(imageViewSignature)
        }

        if (!signed && currentDate && inCompleteWorkItemsCount == 0) {
            textViewAddSignature.visibility = View.VISIBLE
        } else {
            textViewAddSignature.visibility = View.GONE
        }

        if (signed || (currentDate && inCompleteWorkItemsCount == 0)) {
            layoutSignature.visibility = View.VISIBLE
        } else {
            layoutSignature.visibility = View.GONE
        }
    }

    private fun showConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(activityContext = activity, listener = object : CustomDialogWarningListener {
            override fun onConfirmClick() {
                lumperWorkDetailPresenter.saveLumperSignature(lumpersInfo?.lumperId!!, Date(selectedTime), signatureFilePath)
            }

            override fun onCancelClick() {
            }
        })
    }

    override fun showLoginScreen() {
        startIntent(LoginActivity::class.java, isFinish = true, flags = arrayOf(Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onBackPressed() {
//        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
//            closeBottomSheet()
//        } else {
            super.onBackPressed()
//        }
    }

//    private fun showBottomSheetWithData(type: String) {
//        if (type.equals(LUMPER_EDIT_NOTE)){
//            layoutTimeClockNote.visibility=View.VISIBLE
//            layoutEditLumperTimeClock.visibility=View.GONE
//            if (!lumperAttendanceData.attendanceNote.isNullOrEmpty())
//            editTextLumpersRequired.setText(lumperAttendanceData.attendanceNote)
//        }else if (type.equals(LUMPER_EDIT_TIEM)){
//            layoutTimeClockNote.visibility=View.GONE
//            layoutEditLumperTimeClock.visibility=View.VISIBLE
//            attendenceDetail()
//        }
//
//        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
//            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            bottomSheetBackground.visibility = View.VISIBLE
//
//        } else {
//            closeBottomSheet()
//        }
//    }

    private fun changeNotesRecord(lumperId: String?) {
        if (!lumperId.isNullOrEmpty() && !updateData.containsKey(lumperId)) {
            updateData[lumperId] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
        }
    }
    private fun attendenceDetail(){
        var fullName= String.format("%s" ,lumpersInfo!!.lumperName)
        textViewLName.text=fullName

        // Show Clock-In Time
        val clockInTime = DateUtils.convertDateStringToTime(
            DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.morningPunchIn
        )
        buttonClockIn.text = if (clockInTime.isNotEmpty()) clockInTime else getString(R.string.clock_in)
//        val isClockInEditable = timeClockAttendanceAdapter.checkIfEditable(clockInTime.isNotEmpty(), AppConstant.ATTENDANCE_MORNING_PUNCH_IN, lumperAttendanceData.id!!)

        // Show Clock-Out Time
        val clockOutTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.eveningPunchOut)
        buttonClockOut.text = if (clockOutTime.isNotEmpty()) clockOutTime else getString(R.string.clock_out)
//        val isClockOutEditable = timeClockAttendanceAdapter.checkIfEditable(clockOutTime.isNotEmpty(), AppConstant.ATTENDANCE_EVENING_PUNCH_OUT, lumperAttendanceData.id!!)

        // Show Lunch-In Time
        val lunchInTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.lunchPunchIn)
        buttonLunchIn.text = if (lunchInTime.isNotEmpty()) lunchInTime else getString(R.string.out_to_lunch)
//        val isLunchInEditable = timeClockAttendanceAdapter.checkIfEditable(lunchInTime.isNotEmpty(), AppConstant.ATTENDANCE_LUNCH_PUNCH_IN, lumperAttendanceData.id!!)

        // Show Lunch-Out Time
        val lunchOutTime = DateUtils.convertDateStringToTime(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.lunchPunchOut)
        buttonLunchOut.text = if (lunchOutTime.isNotEmpty()) lunchOutTime else getString(R.string.back_to_work)
//        val isLunchOutEditable = timeClockAttendanceAdapter.checkIfEditable(lunchOutTime.isNotEmpty(), AppConstant.ATTENDANCE_LUNCH_PUNCH_OUT, lumperAttendanceData.id!!)

//        buttonClockIn.isEnabled = isClockInEditable

        /* ClockOut Button will only be enabled in these cases:
            1. ClockIn Time is Punched
            2. LunchIn Time is Not Punched
            3. LunchIn Time & LunchOut Time are punched */
//        buttonClockOut.isEnabled = isClockOutEditable && !isClockInEditable && (isLunchInEditable || !(!isLunchInEditable && isLunchOutEditable))

        /* LunchIn Button will only be enabled in these cases:
            1. ClockIn Time is Punched
            2. ClockOut Time is Not Punched */
//        buttonLunchIn.isEnabled = isLunchInEditable && !isClockInEditable && isClockOutEditable

        /* LunchOut Button will only be enabled in these cases:
            1. LunchIn Time is Punched
            2. ClockIn Time is Punched */
//        buttonLunchOut.isEnabled = isLunchOutEditable && !isLunchInEditable && !isClockInEditable

    }

    private fun initiateUpdateRecord(lumperId: String?, isPresent: Boolean = true) {
        if (!lumperId.isNullOrEmpty() && !updateData.containsKey(lumperId)) {
            updateData[lumperId] = AttendanceDetail()
            updateData[lumperId]?.lumperId = lumperId
            updateData[lumperId]?.isPresent = isPresent
        }
    }


    private fun clockInButtonClicked() {
        val selectedStartTime = System.currentTimeMillis()
        val lumperEndTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.eveningPunchOut)

        if (lumperEndTime > 0) {
            if (!DateUtils.isFutureTime(selectedStartTime, lumperEndTime)) {
                showErrorDialog(getString(R.string.work_start_less_work_end_warning_message))
                return
            }
        }
        initiateUpdateRecord(lumpersInfo!!.lumperId)
        updateData[lumpersInfo!!.lumperId]?.morningPunchIn = "$selectedStartTime"
        buttonClockIn.text = DateUtils.convertMillisecondsToTimeString(selectedStartTime)
        updateData[lumpersInfo!!.lumperId]?.isMorningPunchInChanged = true
    }
    private fun clockOutButtonClicked() {
        val selectedEndTime = System.currentTimeMillis()
        val lumperStartTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.morningPunchIn)
        val lumperLunchInTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.lunchPunchIn)

        if (!DateUtils.isFutureTime(lumperStartTime, selectedEndTime)) {
            showErrorDialog(getString(R.string.work_end_greater_work_start_warning_message))
        } else if (lumperLunchInTime > 0 && !DateUtils.isFutureTime(lumperStartTime, selectedEndTime)
        ) {
            showErrorDialog(getString(R.string.work_end_greater_break_start_warning_message))
        } else {
            initiateUpdateRecord(lumpersInfo!!.lumperId)
            updateData[lumpersInfo!!.lumperId]?.eveningPunchOut = "$selectedEndTime"
            buttonClockOut.text = DateUtils.convertMillisecondsToTimeString(selectedEndTime)
            updateData[lumpersInfo!!.lumperId]?.isEveningPunchOutChanged = true
        }
    }

    private fun lunchInButtonClicked() {
        val selectedBreakInTime = System.currentTimeMillis()
        val lumperStartTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.morningPunchIn)
        val lumperLunchOutTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.lunchPunchOut)
        val lumperEndTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.eveningPunchOut)
        if (lumperLunchOutTime > 0) {
            if (!DateUtils.isFutureTime(lumperStartTime, selectedBreakInTime)) {
                showErrorDialog(getString(R.string.break_start_greater_work_start_warning_message))
                return
            } else if (!DateUtils.isFutureTime(selectedBreakInTime, lumperLunchOutTime)) {
                showErrorDialog(getString(R.string.break_start_less_break_end_warning_message))
                return
            } else if (lumperEndTime > 0 && !DateUtils.isFutureTime(selectedBreakInTime, lumperEndTime)) {
                showErrorDialog(getString(R.string.break_start_less_work_end_warning_message))
                return
            }
        }
        initiateUpdateRecord(lumpersInfo!!.lumperId)
        updateData[lumpersInfo!!.lumperId]?.lunchPunchIn = "$selectedBreakInTime"
        buttonLunchIn.text = DateUtils.convertMillisecondsToTimeString(selectedBreakInTime)
        updateData[lumpersInfo!!.lumperId]?.isLunchPunchInChanged = true
    }

    private fun lunchOutButtonClicked() {
        val selectedBreakOutTime = System.currentTimeMillis()
        val lumperStartTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.morningPunchIn)
        val lumperLunchInTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.lunchPunchIn)
        val lumperEndTime = DateUtils.convertUTCDateStringToMilliseconds(DateUtils.PATTERN_API_RESPONSE, lumperAttendanceData?.eveningPunchOut)


        if (!DateUtils.isFutureTime(lumperStartTime, lumperLunchInTime)) {
            showErrorDialog(getString(R.string.break_end_greater_work_start_warning_message))
        } else if (!DateUtils.isFutureTime(lumperLunchInTime, selectedBreakOutTime)) {
            showErrorDialog(getString(R.string.break_end_greater_break_start_warning_message))
        } else if (lumperEndTime > 0 && !DateUtils.isFutureTime(
                selectedBreakOutTime,
                lumperEndTime
            )
        ) {
            showErrorDialog(getString(R.string.break_end_less_work_end_warning_message))
        } else {
            initiateUpdateRecord(lumpersInfo!!.lumperId)
            updateData[lumpersInfo!!.lumperId]?.lunchPunchOut = "$selectedBreakOutTime"
            buttonLunchOut.text = DateUtils.convertMillisecondsToTimeString(selectedBreakOutTime)
            updateData[lumpersInfo!!.lumperId]?.isLunchPunchOutChanged = true
        }
    }

//    private fun closeBottomSheet() {
//        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        bottomSheetBackground.visibility = View.GONE
//    }

    private fun showUpdateConfirmationDialog() {
        CustomProgressBar.getInstance().showWarningDialog(getString(R.string.save_attendance_alert_message), this, object : CustomDialogWarningListener {
            override fun onConfirmClick() {
//                closeBottomSheet()
                lumperWorkDetailPresenter.saveAttendanceDetails(updateData.values.distinct())
            }

            override fun onCancelClick() {
            }
        })
    }

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSignature.id -> startIntent(AddSignatureActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                buttonSave.id -> showConfirmationDialog()
                buttonCancelRequest.id -> onBackPressed()
//                layoutEditTimeClock.id -> showBottomSheetWithData(LUMPER_EDIT_TIEM)
//                layoutEditLumerNote.id -> showBottomSheetWithData(LUMPER_EDIT_NOTE)
//                bottomSheetBackground.id -> closeBottomSheet()
//                buttonCancelNote.id -> closeBottomSheet()
//                buttonSubmit.id -> {showUpdateConfirmationDialog() }
//                buttonClockIn.id -> clockInButtonClicked()
//                buttonClockOut.id -> clockOutButtonClicked()
//                buttonLunchIn.id -> lunchInButtonClicked()
//                buttonLunchOut.id -> lunchOutButtonClicked()
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumperWorkDetails(lumperDaySheetList: ArrayList<LumperDaySheet>, lumperAttendanceData: AttendanceDetail) {
        this.lumperDaySheetList=lumperDaySheetList
        this.lumperAttendanceData=lumperAttendanceData
        val isCurrentDate = DateUtils.isCurrentDate(selectedTime)
        layoutSaveCancelButton.visibility = View.GONE

        showLumperTimeDetails(lumperAttendanceData)
        lumperWorkDetailAdapter.updateWorkDetails(lumperDaySheetList)

        var inCompleteWorkItemsCount = 0
        for (lumperDaySheet in lumperDaySheetList) {
            if (lumperDaySheet.workItemDetail?.status != AppConstant.WORK_ITEM_STATUS_COMPLETED
                && lumperDaySheet.workItemDetail?.status != AppConstant.WORK_ITEM_STATUS_CANCELLED
            ) {
                inCompleteWorkItemsCount++
            }
        }

        if (lumperDaySheetList.size > 0) {
            updateUIVisibility(
                getDefaultOrValue(lumperDaySheetList[0].lumpersTimeSchedule?.sheetSigned), isCurrentDate, inCompleteWorkItemsCount,
                lumperDaySheetList[0].lumpersTimeSchedule?.lumperSignatureInfo?.lumperSignatureUrl
            )
        } else {
            updateUIVisibility(false, isCurrentDate, inCompleteWorkItemsCount)
        }
    }
    override fun showDataSavedMessage() {
        CustomProgressBar.getInstance().showSuccessDialog(getString(R.string.attendance_saved_success_message),
            this, object : CustomDialogListener {
                override fun onConfirmClick() {
                    lumperWorkDetailPresenter.getLumperWorkDetails(getDefaultOrValue(lumpersInfo?.lumperId), Date(selectedTime))
                }
            })
    }

    private fun showLumperTimeDetails(lumperAttendanceData: AttendanceDetail) {
        val isPresent = getDefaultOrValue(lumperAttendanceData.isPresent)
        if (isPresent) {
            val morningPunchIn = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.morningPunchIn
            )
            val eveningPunchOut = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.eveningPunchOut
            )
            textViewShiftTime.text = String.format(
                "%s - %s",
                if (morningPunchIn.isNotEmpty()) morningPunchIn else "NA",
                if (eveningPunchOut.isNotEmpty()) eveningPunchOut else "NA"
            )

            if(!lumperAttendanceData.morningPunchIn.isNullOrEmpty()&& !lumperAttendanceData.eveningPunchOut.isNullOrEmpty())
                textViewShiftTotalTime.text=String.format("Total Time: %s",getDateTimeCalculeted(lumperAttendanceData.morningPunchIn!!, lumperAttendanceData.eveningPunchOut!!))
            else textViewLunchTotalTime.visibility=View.GONE

            val lunchPunchIn = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.lunchPunchIn
            )
            val lunchPunchOut = DateUtils.convertDateStringToTime(
                DateUtils.PATTERN_API_RESPONSE,
                lumperAttendanceData.lunchPunchOut
            )
            textViewLunchTime.text = String.format(
                "%s - %s",
                if (lunchPunchIn.isNotEmpty()) lunchPunchIn else "NA",
                if (lunchPunchOut.isNotEmpty()) lunchPunchOut else "NA"
            )
            if(!lumperAttendanceData.lunchPunchIn.isNullOrEmpty()&&!lumperAttendanceData.lunchPunchOut.isNullOrEmpty())
                textViewLunchTotalTime.text=String.format("Total Time: %s",getDateTimeCalculeted(lumperAttendanceData.lunchPunchIn!!, lumperAttendanceData.lunchPunchOut!!))
            else textViewLunchTotalTime.visibility=View.GONE
        }
        if (!lumperAttendanceData.attendanceNote.isNullOrEmpty()) {
            linearLayout.visibility = View.VISIBLE
            editTextNotes.text = lumperAttendanceData.attendanceNote
        } else linearLayout.visibility = View.GONE
    }

    override fun lumperSignatureSaved() {
        signatureFilePath=""
        isDataSave(true)
        setResult(RESULT_OK)
    }

    /** Adapter Listeners */
    override fun onBOItemClick(workItemDetail: WorkItemDetail, parameters: ArrayList<String>) {
        val bundle = Bundle()
        bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, parameters)
        bundle.putSerializable(ARG_BUILDING_PARAMETER_VALUES, workItemDetail.buildingOps)
        startIntent(BuildingOperationsViewActivity::class.java, bundle = bundle)
    }

    override fun onNotesItemClick(notes: String?) {
        notes?.let {
            CustomProgressBar.getInstance().showInfoDialog(getString(R.string.note), notes, activity)
        }
    }

    override fun afterTextChanged(text: Editable?) {
        if (getDefaultOrValue(lumperAttendanceData?.attendanceNote) != text.toString()) {
            // Update in API Request Object
            changeNotesRecord(lumpersInfo!!.lumperId)
            updateData[lumpersInfo!!.lumperId]?.attendanceNote = text.toString()

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}
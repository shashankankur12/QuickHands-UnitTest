package com.quickhandslogistics.views.lumperSheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.lumperSheet.LumperWorkDetailPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.LoginActivity
import com.quickhandslogistics.views.common.AddSignatureActivity
import com.quickhandslogistics.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.views.lumperSheet.LumperSheetFragment.Companion.ARG_LUMPER_INFO
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETER_VALUES
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.activity_lumper_work_detail.*
import kotlinx.android.synthetic.main.activity_lumper_work_detail.mainConstraintLayout
import kotlinx.android.synthetic.main.bottom_sheet_create_lumper_request.constraintLayoutBottomSheetRequestLumpers
import kotlinx.android.synthetic.main.bottom_sheet_lumper_work_detail.*
import kotlinx.android.synthetic.main.content_lumper_work_detail.*
import kotlinx.android.synthetic.main.content_lumper_work_detail.layoutEditTimeClock
import kotlinx.android.synthetic.main.content_lumper_work_detail.layoutSaveCancelButton
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.bottom_sheet_lumper_work_detail.textViewShiftTime as textViewShiftTime1
import kotlinx.android.synthetic.main.content_lumper_work_detail.textViewLunchTime as textViewLunchTime1

class LumperWorkDetailActivity : BaseActivity(), View.OnClickListener, LumperWorkDetailContract.View,
    LumperWorkDetailContract.View.OnAdapterItemClickListener {

    private var signatureFilePath = ""
    private var selectedTime: Long = 0
    private var lumpersInfo: LumpersInfo? = null

    private lateinit var lumperWorkDetailPresenter: LumperWorkDetailPresenter
    private lateinit var lumperWorkDetailAdapter: LumperWorkDetailAdapter
    private  var lumperDaySheetList: ArrayList<LumperDaySheet> = ArrayList()
    private  var lumperAttendanceData: AttendanceDetail = AttendanceDetail()
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

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
        sheetBehavior = BottomSheetBehavior.from(constraintLayoutBottomSheetRequestLumpers)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        lumpersInfo?.let { employeeData ->
            UIUtils.showEmployeeProfileImage(activity, employeeData.lumperImageUrl, circleImageViewProfile)
            textViewLumperName.text = getDefaultOrValue(employeeData.lumperName)
            textViewEmployeeId.text = UIUtils.getDisplayEmployeeID(employeeData.lumperEmployeeId)
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
        layoutEditTimeClock.setOnClickListener(this)
        layoutEditLumerNote.setOnClickListener(this)
        bottomSheetBackground.setOnClickListener(this)
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
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            closeBottomSheet()
        } else {
            super.onBackPressed()
        }
    }

    private fun showBottomSheetWithData(type: String) {
        if (type.equals(LUMPER_EDIT_NOTE)){
            layoutTimeClockNote.visibility=View.VISIBLE
            layoutEditLumperTimeClock.visibility=View.GONE
        }else if (type.equals(LUMPER_EDIT_TIEM)){
            layoutTimeClockNote.visibility=View.GONE
            layoutEditLumperTimeClock.visibility=View.VISIBLE
        }

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

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSignature.id -> startIntent(AddSignatureActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                buttonSave.id -> showConfirmationDialog()
                buttonSave.id -> showConfirmationDialog()
                layoutEditTimeClock.id -> showBottomSheetWithData(LUMPER_EDIT_TIEM)
                layoutEditLumerNote.id -> showBottomSheetWithData(LUMPER_EDIT_NOTE)
                bottomSheetBackground.id -> closeBottomSheet()
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
                textViewShiftTotalTime.text=DateUtils.getDateTimeCalculeted(lumperAttendanceData.morningPunchIn!!, lumperAttendanceData.eveningPunchOut!!)
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
                textViewLunchTotalTime.text=DateUtils.getDateTimeCalculeted(lumperAttendanceData.lunchPunchIn!!, lumperAttendanceData.lunchPunchOut!!)
            else textViewLunchTotalTime.visibility=View.GONE
        }
        editTextNotes.text = if (!lumperAttendanceData.attendanceNote.isNullOrEmpty())lumperAttendanceData.attendanceNote else "Add Note"
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
}
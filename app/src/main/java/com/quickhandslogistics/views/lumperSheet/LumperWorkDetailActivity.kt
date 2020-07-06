package com.quickhandslogistics.views.lumperSheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.adapters.lumperSheet.LumperWorkDetailAdapter
import com.quickhandslogistics.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.presenters.lumperSheet.LumperWorkDetailPresenter
import com.quickhandslogistics.utils.*
import com.quickhandslogistics.utils.ValueUtils.getDefaultOrValue
import com.quickhandslogistics.views.BaseActivity
import com.quickhandslogistics.views.common.AddSignatureActivity
import com.quickhandslogistics.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.views.lumperSheet.LumperSheetFragment.Companion.ARG_LUMPER_INFO
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_BUILDING_PARAMETER_VALUES
import com.quickhandslogistics.views.schedule.ScheduleFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import kotlinx.android.synthetic.main.activity_lumper_work_detail.*
import java.io.File
import java.util.*

class LumperWorkDetailActivity : BaseActivity(), View.OnClickListener, LumperWorkDetailContract.View,
    LumperWorkDetailContract.View.OnAdapterItemClickListener {

    private var signatureFilePath = ""
    private var selectedTime: Long = 0
    private var lumpersInfo: LumpersInfo? = null

    private lateinit var lumperWorkDetailPresenter: LumperWorkDetailPresenter
    private lateinit var lumperWorkDetailAdapter: LumperWorkDetailAdapter

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
        lumperWorkDetailPresenter.getLumperWorkDetails(getDefaultOrValue(lumpersInfo?.lumperId), Date(selectedTime))
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

    private fun initializeUI() {
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
    }

    private fun showLocalSignatureOnUI(signatureFilePath: String?) {
        if (!signatureFilePath.isNullOrEmpty()) {
            this.signatureFilePath = signatureFilePath
            Glide.with(activity).load(File(signatureFilePath)).into(imageViewSignature)
            imageViewSignature.visibility = View.VISIBLE
            textViewAddSignature.visibility = View.GONE
            buttonSave.visibility = View.VISIBLE
        } else {
            this.signatureFilePath = ""
            imageViewSignature.visibility = View.GONE
            textViewAddSignature.visibility = View.VISIBLE
            buttonSave.visibility = View.GONE
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

    /** Native Views Listeners */
    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSignature.id -> startIntent(AddSignatureActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                buttonSave.id -> showConfirmationDialog()
            }
        }
    }

    /** Presenter Listeners */
    override fun showAPIErrorMessage(message: String) {
        SnackBarFactory.createSnackBar(activity, mainConstraintLayout, message)
    }

    override fun showLumperWorkDetails(lumperDaySheetList: ArrayList<LumperDaySheet>) {
        val isCurrentDate = DateUtils.isCurrentDate(selectedTime)
        buttonSave.visibility = View.GONE

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

    override fun lumperSignatureSaved() {
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
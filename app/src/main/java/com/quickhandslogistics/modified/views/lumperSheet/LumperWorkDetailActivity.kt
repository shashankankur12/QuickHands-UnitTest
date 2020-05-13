package com.quickhandslogistics.modified.views.lumperSheet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.adapters.lumperSheet.LumperWorkDetailAdapter
import com.quickhandslogistics.modified.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.modified.controls.SpaceDividerItemDecorator
import com.quickhandslogistics.modified.data.lumperSheet.LumperDaySheet
import com.quickhandslogistics.modified.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.presenters.lumperSheet.LumperWorkDetailPresenter
import com.quickhandslogistics.modified.views.BaseActivity
import com.quickhandslogistics.modified.views.common.AddSignatureActivity
import com.quickhandslogistics.modified.views.common.BuildingOperationsViewActivity
import com.quickhandslogistics.modified.views.lumperSheet.LumperSheetFragment.Companion.ARG_LUMPER_INFO
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_BUILDING_PARAMETERS
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_BUILDING_PARAMETER_VALUES
import com.quickhandslogistics.modified.views.schedule.ScheduleMainFragment.Companion.ARG_SELECTED_DATE_MILLISECONDS
import com.quickhandslogistics.utils.*
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
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )*/
        setContentView(R.layout.activity_lumper_work_detail)
        setupToolbar(getString(R.string.lumper_work_detail))

        intent.extras?.let { bundle ->
            lumpersInfo = bundle.getParcelable(ARG_LUMPER_INFO) as LumpersInfo?
            selectedTime = bundle.getLong(ARG_SELECTED_DATE_MILLISECONDS, 0)
        }

        initializeUI()

        lumperWorkDetailPresenter = LumperWorkDetailPresenter(this, resources)
        lumperWorkDetailPresenter.getLumperWorkDetails(ValueUtils.getDefaultOrValue(lumpersInfo?.lumperId), Date(selectedTime))
    }

    private fun initializeUI() {
        lumpersInfo?.let { employeeData ->
            if (!StringUtils.isNullOrEmpty(employeeData.lumperImageUrl)) {
                Glide.with(activity).load(employeeData.lumperImageUrl).placeholder(R.drawable.dummy).error(R.drawable.dummy).into(circleImageViewProfile)
            } else {
                Glide.with(activity).clear(circleImageViewProfile);
            }

            textViewLumperName.text = ValueUtils.getDefaultOrValue(employeeData.lumperName)

            if (StringUtils.isNullOrEmpty(employeeData.lumperEmployeeId)) {
                textViewEmployeeId.visibility = View.GONE
            } else {
                textViewEmployeeId.visibility = View.VISIBLE
                textViewEmployeeId.text = String.format("(Emp ID: %s)", employeeData.lumperEmployeeId)
            }
        }

        recyclerViewLumperWork.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(SpaceDividerItemDecorator(15))
            lumperWorkDetailAdapter = LumperWorkDetailAdapter(resources, this@LumperWorkDetailActivity)
            adapter = lumperWorkDetailAdapter
        }

        textViewAddSignature.setOnClickListener(this)
        buttonSave.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                textViewAddSignature.id -> {
                    startIntent(AddSignatureActivity::class.java, requestCode = AppConstant.REQUEST_CODE_CHANGED)
                }
                buttonSave.id -> {
                    CustomProgressBar.getInstance().showWarningDialog(
                        activityContext = activity, listener = object : CustomDialogWarningListener {
                            override fun onConfirmClick() {
                                lumperWorkDetailPresenter.saveLumperSignature(lumpersInfo?.lumperId!!, Date(selectedTime), signatureFilePath)
                            }

                            override fun onCancelClick() {
                            }
                        })
                }
            }
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

    override fun onBOItemClick(workItemDetail: WorkItemDetail) {
        val bundle = Bundle()
        bundle.putStringArrayList(ARG_BUILDING_PARAMETERS, workItemDetail.buildingDetailData?.parameters)
        bundle.putSerializable(ARG_BUILDING_PARAMETER_VALUES, workItemDetail.buildingOps)
        startIntent(BuildingOperationsViewActivity::class.java, bundle = bundle)
    }

    override fun onNotesItemClick(notes: String?) {
        notes?.let {
            CustomProgressBar.getInstance().showInfoDialog(getString(R.string.string_note), notes, activity)
        }
    }

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
                ValueUtils.getDefaultOrValue(lumperDaySheetList[0].lumpersTimeSchedule?.sheetSigned),
                isCurrentDate, inCompleteWorkItemsCount
            )
        } else {
            updateUIVisibility(false, isCurrentDate, inCompleteWorkItemsCount)
        }
    }

    override fun lumperSignatureSaved() {
        setResult(RESULT_OK)
    }

    private fun updateUIVisibility(signed: Boolean, currentDate: Boolean, inCompleteWorkItemsCount: Int) {
        imageViewSignature.visibility = View.GONE
        textViewSignature.visibility = if (signed) View.VISIBLE else View.GONE

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
}

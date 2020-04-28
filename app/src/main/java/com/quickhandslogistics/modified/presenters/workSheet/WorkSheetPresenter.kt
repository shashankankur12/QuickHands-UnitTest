package com.quickhandslogistics.modified.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.WorkSheetContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import com.quickhandslogistics.modified.data.workSheet.WorkSheetListAPIResponse
import com.quickhandslogistics.modified.models.workSheet.WorkSheetModel
import com.quickhandslogistics.utils.SharedPref

class WorkSheetPresenter(
    private var workSheetView: WorkSheetContract.View?,
    private val resources: Resources, sharedPref: SharedPref
) : WorkSheetContract.Presenter, WorkSheetContract.Model.OnFinishedListener {

    private val workSheetModel = WorkSheetModel(sharedPref)

    override fun fetchWorkSheetList() {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        workSheetModel.fetchHeaderInfo(this)
        workSheetModel.fetchWorkSheetList(this)
    }

    override fun onDestroy() {
        workSheetView = null
    }

    override fun initiateCancellingWorkSchedules(selectedLumperIdsList: ArrayList<String>) {
        workSheetView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        workSheetModel.cancelAllWorkSchedules(
            selectedLumperIdsList,
            this
        )
    }

    override fun onFailure(message: String) {
        workSheetView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            workSheetView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            workSheetView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchWorkSheet(workSheetListAPIResponse: WorkSheetListAPIResponse) {
        workSheetView?.hideProgressDialog()
        workSheetListAPIResponse.data?.let { data ->
            val onGoingWorkItems = ArrayList<WorkItemDetail>()
            onGoingWorkItems.addAll(data.inProgress!!)
            onGoingWorkItems.addAll(data.onHold!!)
            onGoingWorkItems.addAll(data.scheduled!!)
            workSheetView?.showWorkSheets(onGoingWorkItems, data.cancelled!!, data.completed!!)
        }
    }

    override fun onSuccessCancelWorkSchedules() {
        workSheetView?.hideProgressDialog()
        workSheetView?.cancellingWorkScheduleFinished()
    }

    override fun onSuccessGetHeaderInfo(buildingName: String, date: String) {
        workSheetView?.showHeaderInfo(buildingName, date)
    }
}
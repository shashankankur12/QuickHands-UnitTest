package com.quickhandslogistics.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.WorkSheetItemDetailContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.models.workSheet.WorkSheetItemDetailModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class WorkSheetItemDetailPresenter(private var workSheetItemDetailView: WorkSheetItemDetailContract.View?, private val resources: Resources) :
    WorkSheetItemDetailContract.Presenter, WorkSheetItemDetailContract.Model.OnFinishedListener {

    private val workSheetItemDetailModel = WorkSheetItemDetailModel()

    /** View Listeners */
    override fun onDestroy() {
        workSheetItemDetailView = null
    }

    override fun fetchWorkItemDetail(workItemId: String) {
        workSheetItemDetailView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetItemDetailModel.fetchWorkItemDetail(workItemId, this)
    }

    override fun changeWorkItemStatus(workItemId: String, status: String) {
        workSheetItemDetailView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetItemDetailModel.changeWorkItemStatus(workItemId, status, this)
    }

    override fun updateWorkItemNotes(workItemId: String, notesQHLCustomer: String, notesQHL: String) {
        workSheetItemDetailView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        workSheetItemDetailModel.updateWorkItemNotes(workItemId, notesQHLCustomer, notesQHL, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        workSheetItemDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            workSheetItemDetailView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            workSheetItemDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        workSheetItemDetailView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            workSheetItemDetailView?.showLoginScreen()
        }
    }

    override fun onSuccess(response: WorkItemDetailAPIResponse) {
        workSheetItemDetailView?.hideProgressDialog()
        response.data?.container?.let { container ->
            workSheetItemDetailView?.showWorkItemDetail(container)
        }
    }

    override fun onSuccessChangeStatus(workItemId: String) {
        workSheetItemDetailView?.statusChangedSuccessfully()
        workSheetItemDetailModel.fetchWorkItemDetail(workItemId, this)
    }

    override fun onSuccessUpdateNotes(workItemId: String) {
        workSheetItemDetailView?.notesSavedSuccessfully()
        workSheetItemDetailModel.fetchWorkItemDetail(workItemId, this)
    }
}
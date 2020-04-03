package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.UnScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.models.schedule.UnScheduledWorkItemDetailModel
import com.quickhandslogistics.utils.SharedPref

class UnScheduledWorkItemDetailPresenter(
    private var unScheduledWorkItemDetailView: UnScheduledWorkItemDetailContract.View?,
    private val resources: Resources,
    sharedPref: SharedPref
) : UnScheduledWorkItemDetailContract.Presenter,
    UnScheduledWorkItemDetailContract.Model.OnFinishedListener {

    private val unScheduledWorkItemDetailModel = UnScheduledWorkItemDetailModel(sharedPref)

    override fun onDestroy() {
        unScheduledWorkItemDetailView = null
    }

    override fun changeWorkItemStatus(workItemId: String, workItemType: String) {
        unScheduledWorkItemDetailView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        unScheduledWorkItemDetailModel.changeWorkItemStatus(workItemId, workItemType, this)
    }

    override fun onFailure(message: String) {
        unScheduledWorkItemDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            unScheduledWorkItemDetailView?.showAPIErrorMessage(resources.getString(R.string.internal_server_error))
        } else {
            unScheduledWorkItemDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessChangeStatus() {
        unScheduledWorkItemDetailView?.hideProgressDialog()
        unScheduledWorkItemDetailView?.workItemStatusChanged()
    }
}
package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.modified.models.schedule.ScheduledWorkItemDetailModel

class ScheduledWorkItemDetailPresenter(
    private var scheduledWorkItemDetailView: ScheduledWorkItemDetailContract.View?,
    private val resources: Resources
) : ScheduledWorkItemDetailContract.Presenter,
    ScheduledWorkItemDetailContract.Model.OnFinishedListener {

    private val scheduledWorkItemDetailModel = ScheduledWorkItemDetailModel()

    override fun onDestroy() {
        scheduledWorkItemDetailView = null
    }

    override fun fetchWorkItemDetail(workItemId: String) {
        scheduledWorkItemDetailView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        scheduledWorkItemDetailModel.fetchWorkItemDetail(workItemId, this)
    }

    override fun onFailure(message: String) {
        scheduledWorkItemDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            scheduledWorkItemDetailView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            scheduledWorkItemDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(response: WorkItemDetailAPIResponse) {
        scheduledWorkItemDetailView?.hideProgressDialog()
        response.data?.workItemDetail?.let { workItemDetail ->
            scheduledWorkItemDetailView?.showWorkItemDetail(workItemDetail)
        }
    }
}
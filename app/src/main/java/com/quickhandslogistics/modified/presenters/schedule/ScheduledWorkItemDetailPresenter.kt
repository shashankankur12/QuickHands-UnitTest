package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduledWorkItemDetailContract
import com.quickhandslogistics.modified.data.schedule.WorkItemDetailAPIResponse
import com.quickhandslogistics.modified.models.schedule.ScheduledWorkItemDetailModel
import com.quickhandslogistics.utils.StringUtils

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
            workItemDetail.assignedLumpersList?.sortWith(Comparator { lumper1, lumper2 ->
                if (!StringUtils.isNullOrEmpty(lumper1.firstName)
                    && !StringUtils.isNullOrEmpty(lumper2.firstName)
                ) {
                    lumper1.firstName?.toLowerCase()!!.compareTo(lumper2.firstName?.toLowerCase()!!)
                } else {
                    0
                }
            })
            scheduledWorkItemDetailView?.showWorkItemDetail(workItemDetail)
        }
    }
}
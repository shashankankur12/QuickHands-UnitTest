package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.modified.models.schedule.UnScheduleDetailModel
import com.quickhandslogistics.modified.views.controls.ScheduleUtils.getScheduleTypeName

class UnScheduleDetailPresenter(
    private var unScheduleDetailView: UnScheduleDetailContract.View?,
    private val resources: Resources
) :
    UnScheduleDetailContract.Presenter, UnScheduleDetailContract.Model.OnFinishedListener {

    private val unScheduleDetailModel: UnScheduleDetailModel = UnScheduleDetailModel()

    override fun getScheduleDetail(scheduleIdentityId: String) {
        unScheduleDetailView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        unScheduleDetailModel.fetchScheduleDetail(scheduleIdentityId, this)
    }

    override fun onFailure(message: String) {
        unScheduleDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            unScheduleDetailView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            unScheduleDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(scheduleDetailAPIResponse: ScheduleDetailAPIResponse) {
        unScheduleDetailView?.hideProgressDialog()
        scheduleDetailAPIResponse.data?.schedules?.let { scheduleDetail ->
            var scheduleTypeNames = ""
            scheduleTypeNames = getScheduleTypeName(
                scheduleDetail.scheduleTypes?.liveLoads,
                scheduleTypeNames,
                resources.getString(R.string.string_live_loads)
            )
            scheduleTypeNames = getScheduleTypeName(
                scheduleDetail.scheduleTypes?.drops,
                scheduleTypeNames,
                resources.getString(R.string.string_drops)
            )
            scheduleTypeNames = getScheduleTypeName(
                scheduleDetail.scheduleTypes?.outbounds,
                scheduleTypeNames,
                resources.getString(R.string.string_out_bonds)
            )
            scheduleDetail.scheduleTypeNames = scheduleTypeNames
            unScheduleDetailView?.showScheduleData(scheduleDetail)
        }
    }
}
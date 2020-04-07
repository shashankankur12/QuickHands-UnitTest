package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.modified.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.modified.models.schedule.ScheduleDetailModel
import com.quickhandslogistics.modified.views.controls.ScheduleUtils
import com.quickhandslogistics.modified.views.controls.ScheduleUtils.getScheduleTypeName

class ScheduleDetailPresenter(
    private var scheduleDetailView: ScheduleDetailContract.View?,
    private val resources: Resources
) :
    ScheduleDetailContract.Presenter, ScheduleDetailContract.Model.OnFinishedListener {

    private val scheduleDetailModel: ScheduleDetailModel = ScheduleDetailModel()

    override fun getScheduleDetail(scheduleIdentityId: String) {
        scheduleDetailView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        scheduleDetailModel.fetchScheduleDetail(scheduleIdentityId, this)
    }

    override fun onFailure(message: String) {
        scheduleDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            scheduleDetailView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            scheduleDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(scheduleDetailAPIResponse: ScheduleDetailAPIResponse) {
        scheduleDetailView?.hideProgressDialog()
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
            scheduleDetailView?.showScheduleData(scheduleDetail)
        }
    }
}
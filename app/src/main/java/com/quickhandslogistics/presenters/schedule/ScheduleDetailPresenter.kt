package com.quickhandslogistics.presenters.schedule

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.schedule.ScheduleDetailContract
import com.quickhandslogistics.utils.ScheduleUtils.getScheduleTypeName
import com.quickhandslogistics.data.schedule.ScheduleDetailAPIResponse
import com.quickhandslogistics.models.schedule.ScheduleDetailModel
import java.util.*

class ScheduleDetailPresenter(private var scheduleDetailView: ScheduleDetailContract.View?, private val resources: Resources) :
    ScheduleDetailContract.Presenter, ScheduleDetailContract.Model.OnFinishedListener {

    private val scheduleDetailModel = ScheduleDetailModel()

    /** View Listeners */
    override fun onDestroy() {
        scheduleDetailView = null
    }

    override fun getScheduleDetail(scheduleIdentityId: String, selectedDate: Date) {
        scheduleDetailView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        scheduleDetailModel.fetchScheduleDetail(scheduleIdentityId, selectedDate, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        scheduleDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            scheduleDetailView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            scheduleDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess(scheduleDetailAPIResponse: ScheduleDetailAPIResponse) {
        scheduleDetailView?.hideProgressDialog()
        scheduleDetailAPIResponse.data?.schedules?.let { scheduleDetail ->
            var scheduleTypeNames = ""
            scheduleTypeNames = getScheduleTypeName(scheduleDetail.scheduleTypes?.liveLoads, scheduleTypeNames, resources.getString(R.string.live_loads))
            scheduleTypeNames = getScheduleTypeName(scheduleDetail.scheduleTypes?.drops, scheduleTypeNames, resources.getString(R.string.drops))
            scheduleTypeNames = getScheduleTypeName(scheduleDetail.scheduleTypes?.outbounds, scheduleTypeNames, resources.getString(R.string.out_bounds))
            scheduleDetail.scheduleTypeNames = scheduleTypeNames
            scheduleDetailView?.showScheduleData(scheduleDetail)
        }
    }
}
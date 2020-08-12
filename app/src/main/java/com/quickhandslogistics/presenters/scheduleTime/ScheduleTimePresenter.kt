package com.quickhandslogistics.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.models.scheduleTime.ScheduleTimeModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment.Companion.CANCEL_SCHEDULE_LUMPER
import java.util.*

class ScheduleTimePresenter(private var scheduleTimeView: ScheduleTimeContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    ScheduleTimeContract.Presenter, ScheduleTimeContract.Model.OnFinishedListener {

    private val scheduleTimeModel = ScheduleTimeModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        scheduleTimeView = null
    }

    override fun getSchedulesTimeByDate(date: Date) {
        scheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        scheduleTimeModel.fetchHeaderInfo(date, this)
        scheduleTimeModel.fetchSchedulesTimeByDate(date, this)
    }

    override fun cancelScheduleLumpers(lumperId: String, date: Date) {
        scheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        scheduleTimeModel.cancelScheduleLumpers(lumperId, date, this)
    }

    override fun editScheduleLumpers(lumperId: String, date: Date ,timeMilsec :Long) {
        scheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        scheduleTimeModel.editScheduleLumpers(lumperId, date,timeMilsec, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        scheduleTimeView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            scheduleTimeView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            scheduleTimeView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        scheduleTimeView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            scheduleTimeView?.showLoginScreen()
        }
    }

    override fun onSuccess(selectedDate: Date, scheduleTimeAPIResponse: GetScheduleTimeAPIResponse) {
        scheduleTimeView?.hideProgressDialog()

        scheduleTimeAPIResponse.data?.let { data ->
            scheduleTimeView?.showScheduleTimeData(selectedDate, data.scheduledLumpers!!, data.tempLumperIds!!,data.notes )
            scheduleTimeView?.showNotesData(data.notes)
        }
    }


    override fun onSuccessGetHeaderInfo(dateString: String) {
        scheduleTimeView?.showDateString(dateString)
    }

    override fun onSuccessRequest(date: Date, typeScheduleLumper: String) {
        scheduleTimeView?.hideProgressDialog()
        if (typeScheduleLumper.equals(CANCEL_SCHEDULE_LUMPER))
            scheduleTimeView?.showSuccessDialog(resources.getString(R.string.request_cancel_success_message), date)
        else
            scheduleTimeView?.showSuccessDialog(resources.getString(R.string.request_update_success_message), date)
    }
}
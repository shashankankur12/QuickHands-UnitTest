package com.quickhandslogistics.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.ScheduleTimeContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.schedule.GetPastFutureDateResponse
import com.quickhandslogistics.data.scheduleTime.leadinfo.GetLeadInfoResponse
import com.quickhandslogistics.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeDetail
import com.quickhandslogistics.data.scheduleTime.ScheduleTimeNoteRequest
import com.quickhandslogistics.models.scheduleTime.ScheduleTimeModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.views.scheduleTime.ScheduleTimeFragment.Companion.CANCEL_SCHEDULE_LUMPER
import java.util.*
import kotlin.collections.ArrayList

class ScheduleTimePresenter(private var scheduleTimeView: ScheduleTimeContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    ScheduleTimeContract.Presenter, ScheduleTimeContract.Model.OnFinishedListener {

    private val scheduleTimeModel = ScheduleTimeModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        scheduleTimeView = null
    }

    override fun getSchedulesTimeByDate(date: Date) {
        scheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        scheduleTimeModel.fetchPastFutureDate( this)
        scheduleTimeModel.fetchHeaderInfo(date, this)
        scheduleTimeModel.fetchSchedulesTimeByDate(date, this)
        scheduleTimeModel.fetchLeadScheduleByDate(date, this)
    }

    override fun cancelScheduleLumpers(lumperId: String, date: Date, cancelReason: String) {
        scheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        scheduleTimeModel.cancelScheduleLumpers(lumperId, date,cancelReason, this)
    }

    override fun editScheduleLumpers(
        lumperId: String,
        date: Date,
        timeMilsec: Long,
        request: ScheduleTimeNoteRequest
    ) {
        scheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        scheduleTimeModel.editScheduleLumpers(lumperId, date,timeMilsec,request, this)
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
            val scheduleLumperList:ArrayList<ScheduleTimeDetail> =ArrayList()
            data.permanentScheduledLumpers?.let { scheduleLumperList.addAll(it) }

            data.tempScheduledLumpers?.let {
                val iterate = it.listIterator()
                while (iterate.hasNext()) {
                    val oldValue = iterate.next()
                    oldValue.lumperInfo!!.isTemporaryAssigned = true
                    iterate.set(oldValue)
                }
                 scheduleLumperList.addAll(it)
            }

            scheduleTimeView?.showScheduleTimeData(selectedDate, scheduleLumperList, data.tempLumperIds!!,data.notes )
            scheduleTimeView?.showNotesData(data.notes)
        }
    }

    override fun onSuccessLeadInfo(getLeadInfoResponse: GetLeadInfoResponse) {
        scheduleTimeView?.showLeadInfo(getLeadInfoResponse.data)
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

    override fun onSuccessPastFutureDate(response: GetPastFutureDateResponse?) {
        response?.data?.let {
            scheduleTimeView?.showPastFutureDate(it)
        }
    }
}
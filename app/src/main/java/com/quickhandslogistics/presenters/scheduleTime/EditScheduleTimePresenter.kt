package com.quickhandslogistics.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.models.scheduleTime.EditScheduleTimeModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class EditScheduleTimePresenter(private var editScheduleTimeView: EditScheduleTimeContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    EditScheduleTimeContract.Presenter, EditScheduleTimeContract.Model.OnFinishedListener {

    private val editScheduleTimeModel = EditScheduleTimeModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        editScheduleTimeView = null
    }

    override fun getHeaderDateString(date: Date) {
        editScheduleTimeModel.fetchHeaderInfo(date, this)
    }

    override fun initiateScheduleTime(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, selectedDate: Date) {
        editScheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        editScheduleTimeModel.assignScheduleTime(scheduledLumpersIdsTimeMap, notes, selectedDate, this)
    }

    override fun cancelScheduleLumpers(lumperId: String, date: Date, position: Int) {
        editScheduleTimeModel.cancelScheduleLumpers(lumperId, date,position , this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        editScheduleTimeView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            editScheduleTimeView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            editScheduleTimeView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        editScheduleTimeView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            editScheduleTimeView?.showLoginScreen()
        }
    }

    override fun onSuccessScheduleTime() {
        editScheduleTimeView?.hideProgressDialog()
        editScheduleTimeView?.scheduleTimeFinished()
    }

    override fun onSuccessGetHeaderInfo(dateString: String) {
        editScheduleTimeView?.showDateString(dateString)
    }

    override fun onSuccessRequest(position: Int) {
        editScheduleTimeView?.showSuccessDialog(resources.getString(R.string.request_placed_success_message), position)
    }
}
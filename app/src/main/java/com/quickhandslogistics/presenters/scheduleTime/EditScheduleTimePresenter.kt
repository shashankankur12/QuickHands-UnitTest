package com.quickhandslogistics.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.models.scheduleTime.EditScheduleTimeModel
import java.util.*

class EditScheduleTimePresenter(private var editScheduleTimeView: EditScheduleTimeContract.View?, private val resources: Resources) :
    EditScheduleTimeContract.Presenter, EditScheduleTimeContract.Model.OnFinishedListener {

    private val editScheduleTimeModel = EditScheduleTimeModel()

    /** View Listeners */
    override fun onDestroy() {
        editScheduleTimeView = null
    }

    override fun initiateScheduleTime(scheduledLumpersIdsTimeMap: HashMap<String, Long>, notes: String, selectedDate: Date) {
        editScheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        editScheduleTimeModel.assignScheduleTime(scheduledLumpersIdsTimeMap, notes, selectedDate, this)
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

    override fun onSuccessScheduleTime() {
        editScheduleTimeView?.hideProgressDialog()
        editScheduleTimeView?.scheduleTimeFinished()
    }
}
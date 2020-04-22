package com.quickhandslogistics.modified.presenters.scheduleTime

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.scheduleTime.EditScheduleTimeContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.models.scheduleTime.EditScheduleTimeModel
import com.quickhandslogistics.utils.SharedPref

class EditScheduleTimePresenter(
    private var editScheduleTimeView: EditScheduleTimeContract.View?,
    private val resources: Resources,
    sharedPref: SharedPref
) : EditScheduleTimeContract.Presenter, EditScheduleTimeContract.Model.OnFinishedListener {

    private val editScheduleTimeModel = EditScheduleTimeModel(sharedPref)

    override fun fetchLumpersList() {
        editScheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        editScheduleTimeModel.fetchLumpersList(this)
    }

    override fun onDestroy() {
        editScheduleTimeView = null
    }

    override fun initiateScheduleTime(
        selectedLumperIdsList: ArrayList<String>,
        workItemId: String,
        workItemType: String
    ) {
        editScheduleTimeView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        editScheduleTimeModel.assignScheduleTime(
            workItemId,
            workItemType,
            selectedLumperIdsList,
            this
        )
    }

    override fun onFailure(message: String) {
        editScheduleTimeView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            editScheduleTimeView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            editScheduleTimeView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessFetchLumpers(allLumpersResponse: AllLumpersResponse) {
        editScheduleTimeView?.hideProgressDialog()
        editScheduleTimeView?.showLumpersData(allLumpersResponse.data!!)
    }

    override fun onSuccessScheduleTime() {
        editScheduleTimeView?.hideProgressDialog()
        editScheduleTimeView?.scheduleTimeFinished()
    }
}
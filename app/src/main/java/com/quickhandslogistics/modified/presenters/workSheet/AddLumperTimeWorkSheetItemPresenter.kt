package com.quickhandslogistics.modified.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.AddLumperTimeWorkSheetItemContract
import com.quickhandslogistics.modified.models.workSheet.AddLumperTimeWorkSheetItemModel

class AddLumperTimeWorkSheetItemPresenter(
    private var addLumperTimeWorkSheetItemView: AddLumperTimeWorkSheetItemContract.View?, private val resources: Resources
) : AddLumperTimeWorkSheetItemContract.Presenter, AddLumperTimeWorkSheetItemContract.Model.OnFinishedListener {

    private val addLumperTimeWorkSheetItemModel = AddLumperTimeWorkSheetItemModel()

    /** View Listeners */
    override fun onDestroy() {
        addLumperTimeWorkSheetItemView = null
    }

    override fun saveLumperTimings(
        id: String, workItemId: String, selectedStartTime: Long, selectedEndTime: Long,
        selectedBreakInTime: Long, selectedBreakOutTime: Long, waitingTime: String
    ) {
        addLumperTimeWorkSheetItemView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        addLumperTimeWorkSheetItemModel.saveLumperTimings(
            id, workItemId, selectedStartTime, selectedEndTime,
            selectedBreakInTime, selectedBreakOutTime, waitingTime, this
        )
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        addLumperTimeWorkSheetItemView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            addLumperTimeWorkSheetItemView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            addLumperTimeWorkSheetItemView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess() {
        addLumperTimeWorkSheetItemView?.hideProgressDialog()
        addLumperTimeWorkSheetItemView?.lumpersTimingSaved()
    }
}
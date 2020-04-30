package com.quickhandslogistics.modified.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.workSheet.AddLumperTimeWorkSheetItemContract
import com.quickhandslogistics.modified.models.workSheet.AddLumperTimeWorkSheetItemModel
import com.quickhandslogistics.utils.SharedPref

class AddLumperTimeWorkSheetItemPresenter(
    private var addLumperTimeWorkSheetItemView: AddLumperTimeWorkSheetItemContract.View?,
    private val resources: Resources, sharedPref: SharedPref
) : AddLumperTimeWorkSheetItemContract.Presenter,
    AddLumperTimeWorkSheetItemContract.Model.OnFinishedListener {

    private val addLumperTimeWorkSheetItemModel = AddLumperTimeWorkSheetItemModel(sharedPref)

    override fun onDestroy() {
        addLumperTimeWorkSheetItemView = null
    }

    override fun saveLumperTimings(
        id: String, workItemId: String, selectedStartTime: Long, selectedEndTime: Long,
        selectedBreakInTime: Long, selectedBreakOutTime: Long, waitingTime: String
    ) {
        addLumperTimeWorkSheetItemView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        addLumperTimeWorkSheetItemModel.saveLumperTimings(
            id, workItemId, selectedStartTime, selectedEndTime,
            selectedBreakInTime, selectedBreakOutTime, waitingTime, this
        )
    }

    override fun onFailure(message: String) {
        addLumperTimeWorkSheetItemView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            addLumperTimeWorkSheetItemView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            addLumperTimeWorkSheetItemView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccess() {
        addLumperTimeWorkSheetItemView?.hideProgressDialog()
        addLumperTimeWorkSheetItemView?.lumpersTimingSaved()
    }
}
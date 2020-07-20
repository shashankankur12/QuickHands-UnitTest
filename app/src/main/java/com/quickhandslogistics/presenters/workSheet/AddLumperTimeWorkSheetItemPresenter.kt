package com.quickhandslogistics.presenters.workSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.workSheet.AddLumperTimeWorkSheetItemContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.models.workSheet.AddLumperTimeWorkSheetItemModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class AddLumperTimeWorkSheetItemPresenter(
    private var addLumperTimeWorkSheetItemView: AddLumperTimeWorkSheetItemContract.View?, private val resources: Resources
) : AddLumperTimeWorkSheetItemContract.Presenter, AddLumperTimeWorkSheetItemContract.Model.OnFinishedListener {

    private val addLumperTimeWorkSheetItemModel = AddLumperTimeWorkSheetItemModel()

    /** View Listeners */
    override fun onDestroy() {
        addLumperTimeWorkSheetItemView = null
    }

    override fun saveLumperTimings(
        id: String,
        workItemId: String,
        selectedStartTime: Long,
        selectedEndTime: Long,
        selectedBreakInTime: Long,
        selectedBreakOutTime: Long,
        waitingTime: String,
        partWorkDone: Int
    ) {
        addLumperTimeWorkSheetItemView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        addLumperTimeWorkSheetItemModel.saveLumperTimings(
            id, workItemId, selectedStartTime, selectedEndTime,
            selectedBreakInTime, selectedBreakOutTime, waitingTime, partWorkDone, this
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

    override fun onErrorCode(errorCode: ErrorResponse) {
        addLumperTimeWorkSheetItemView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            addLumperTimeWorkSheetItemView?.showLoginScreen()
        }
    }

    override fun onSuccess() {
        addLumperTimeWorkSheetItemView?.hideProgressDialog()
        addLumperTimeWorkSheetItemView?.lumpersTimingSaved()
    }
}
package com.quickhandslogistics.presenters.lumperSheet

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.lumperSheet.LumperWorkDetailContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.models.lumperSheet.LumperWorkDetailModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class LumperWorkDetailPresenter(private var lumperWorkDetailView: LumperWorkDetailContract.View?, private val resources: Resources) :
    LumperWorkDetailContract.Presenter, LumperWorkDetailContract.Model.OnFinishedListener {

    private val lumperWorkDetailModel = LumperWorkDetailModel()

    /** View Listeners */
    override fun onDestroy() {
        lumperWorkDetailView = null
    }

    override fun getLumperWorkDetails(lumperId: String, selectedDate: Date) {
        lumperWorkDetailView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperWorkDetailModel.fetchLumperWorkDetails(lumperId, selectedDate, this)
    }

    override fun saveLumperSignature(lumperId: String, date: Date, signatureFilePath: String) {
        lumperWorkDetailView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        lumperWorkDetailModel.saveLumperSignature(lumperId, date, signatureFilePath, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        lumperWorkDetailView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            lumperWorkDetailView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            lumperWorkDetailView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        lumperWorkDetailView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            lumperWorkDetailView?.showLoginScreen()
        }
    }

    override fun onSuccess(response: LumperWorkDetailAPIResponse) {
        lumperWorkDetailView?.hideProgressDialog()
        lumperWorkDetailView?.showLumperWorkDetails(response.data?.lumperDaySheet!!)
    }

    override fun onSuccessSaveLumperSignature(lumperId: String, date: Date) {
        lumperWorkDetailView?.lumperSignatureSaved()
        lumperWorkDetailModel.fetchLumperWorkDetails(lumperId, date, this)
    }
}
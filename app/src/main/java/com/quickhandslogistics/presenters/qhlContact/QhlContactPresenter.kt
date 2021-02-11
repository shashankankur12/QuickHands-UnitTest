package com.quickhandslogistics.presenters.qhlContact

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.qhlContact.QhlContactContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.models.qhlContact.QhlContactModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class QhlContactPresenter(private var customerContactContractView: QhlContactContract.View?, private val resources: Resources, sharedPref: SharedPref) : QhlContactContract.Presenter, QhlContactContract.Model.OnFinishedListener {
    private val qhlContactModel = QhlContactModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        customerContactContractView=null
    }

    override fun fetchQhlContactList() {
        customerContactContractView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        qhlContactModel.fetchHeaderInfo(this)
        qhlContactModel.fetchQhlContactList(this)
    }

    /** Model Result Listeners */
    override fun onSuccess(response: LumperListAPIResponse) {

    }

    override fun onSuccessGetHeaderInfo(leadProfileData: LeadProfileData?) {

    }

    override fun onFailure(message: String) {
        customerContactContractView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            customerContactContractView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            customerContactContractView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        customerContactContractView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            customerContactContractView?.showLoginScreen()
        }
    }

}
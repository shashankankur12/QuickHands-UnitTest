package com.quickhandslogistics.presenters.qhlContact

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.qhlContact.QhlContactContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.qhlContact.QhlContactListResponse
import com.quickhandslogistics.data.qhlContact.QhlOfficeInfoResponse
import com.quickhandslogistics.models.qhlContact.QhlContactModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class QhlContactPresenter(private var qhlContactContractView: QhlContactContract.View?, private val resources: Resources, sharedPref: SharedPref) : QhlContactContract.Presenter, QhlContactContract.Model.OnFinishedListener {
    private val qhlContactModel = QhlContactModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        qhlContactContractView=null
    }

    override fun fetchQhlContactList() {
        qhlContactContractView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        qhlContactModel.fetchQhlHeaderInfo(this)
        qhlContactModel.fetchQhlContactList(this)
    }

    /** Model Result Listeners */
    override fun onSuccess(response: QhlContactListResponse) {
        qhlContactContractView?.hideProgressDialog()
        val qhlContactList=response.qhlContactList
        qhlContactList?.let { qhlContactContractView?.qhlContactList(it) }
    }

    override fun onSuccessGetHeaderInfo(qhlOfficeInfoResponse: QhlOfficeInfoResponse?) {
        val qhlOfficeInfo= qhlOfficeInfoResponse?.data
        qhlContactContractView?.showQhlHeaderInfo(qhlOfficeInfo)
    }

    override fun onFailure(message: String) {
        qhlContactContractView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            qhlContactContractView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            qhlContactContractView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        qhlContactContractView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            qhlContactContractView?.showLoginScreen()
        }
    }

}
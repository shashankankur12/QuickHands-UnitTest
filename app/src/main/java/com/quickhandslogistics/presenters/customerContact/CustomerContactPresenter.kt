package com.quickhandslogistics.presenters.customerContact

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.customerContact.CustomerContactContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.data.qhlContact.QhlContactListResponse
import com.quickhandslogistics.models.customerContact.CustomerContactModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class CustomerContactPresenter(private var customerContactContractView: CustomerContactContract.View?, private val resources: Resources, sharedPref: SharedPref) : CustomerContactContract.Presenter, CustomerContactContract.Model.OnFinishedListener {
    private val customerContactModel = CustomerContactModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        customerContactContractView=null
    }

    override fun fetchCustomerContactList() {
        customerContactContractView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        customerContactModel.fetchHeaderInfo(this)
        customerContactModel.fetchCustomerContactList(this)
    }

    /** Model Result Listeners */
    override fun onSuccess(response: QhlContactListResponse) {
        customerContactContractView?.hideProgressDialog()
        val customerContactList=response.qhlContactList
        customerContactList?.let { customerContactContractView?.showCustomerContactData(it) }
    }

    override fun onSuccessGetHeaderInfo(buildingDetails: BuildingDetailData?) {
        customerContactContractView?.showHeaderInfo(buildingDetails)
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
package com.quickhandslogistics.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.DashBoardContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.models.DashBoardModel
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

class DashBoardPresenter(private var dashBoardView: DashBoardContract.View?, private val resources: Resources, sharedPref: SharedPref) :
    DashBoardContract.Presenter, DashBoardContract.Model.OnFinishedListener {

    private val dashBoardModel = DashBoardModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        dashBoardView = null
    }

    override fun loadLeadProfileData() {
        dashBoardModel.fetchLeadProfileData(this)
    }

    override fun performLogout() {
        dashBoardView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        dashBoardModel.performLogout(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        dashBoardView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            dashBoardView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            dashBoardView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        dashBoardView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        if (!TextUtils.isEmpty(sharedPref.getString(AppConstant.PREFERENCE_REGISTRATION_TOKEN, ""))) {
            sharedPref.performLogout()
            dashBoardView?.showLoginScreen()
        }
    }

    override fun onLoadLeadProfile(leadProfileData: LeadProfileData) {
        dashBoardView?.showLeadProfile(leadProfileData)
    }

    override fun onFetchLeadProfileSuccess(response: LeadProfileAPIResponse) {
        response.data?.let {
            dashBoardModel.processLeadProfileData(it, this)
        }
    }

    override fun onSuccessLogout() {
        dashBoardView?.hideProgressDialog()
        dashBoardView?.showLoginScreen()
    }
}
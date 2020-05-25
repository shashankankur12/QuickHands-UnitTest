package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.models.DashBoardModel
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
package com.quickhandslogistics.modified.presenters

import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.models.DashBoardModel
import com.quickhandslogistics.utils.SharedPref

class DashBoardPresenter(private var dashBoardView: DashBoardContract.View?, sharedPref: SharedPref) :
    DashBoardContract.Presenter, DashBoardContract.Model.OnFinishedListener {

    private val dashBoardModel = DashBoardModel(sharedPref)

    /** View Listeners */
    override fun onDestroy() {
        dashBoardView = null
    }

    override fun loadLeadProfileData() {
        dashBoardModel.fetchLeadProfileData(this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
    }

    override fun onLoadLeadProfile(leadProfileData: LeadProfileData) {
        dashBoardView?.showLeadProfile(leadProfileData)
    }

    override fun onFetchLeadProfileSuccess(response: LeadProfileAPIResponse) {
        response.data?.let {
            dashBoardModel.processLeadProfileData(it, this)
        }
    }
}
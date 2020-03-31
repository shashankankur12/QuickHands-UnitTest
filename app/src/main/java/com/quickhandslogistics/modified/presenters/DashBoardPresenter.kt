package com.quickhandslogistics.modified.presenters

import android.content.res.Resources
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.models.DashBoardModel
import com.quickhandslogistics.utils.SharedPref

class DashBoardPresenter internal constructor(
    private var dashBoardView: DashBoardContract.View?,
    private var resources: Resources,
    sharedPref: SharedPref
) :
    DashBoardContract.Presenter, DashBoardContract.Model.OnFinishedListener {
    private val dashBoardModel: DashBoardModel = DashBoardModel(sharedPref)

    override fun onDestroy() {
        dashBoardView = null
    }

    override fun loadLeadProfileData() {
        dashBoardModel.fetchLeadProfileData(this)
    }

    override fun onLoadLeadProfile(leadProfileData: LeadProfileData) {
        dashBoardView?.showLeadProfile(leadProfileData)
    }
}
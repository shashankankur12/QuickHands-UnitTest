package com.quickhandslogistics.modified.presenters

import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.models.DashBoardModel
import com.quickhandslogistics.utils.SharedPref

class DashBoardPresenter internal constructor(
    private var dashBoardView: DashBoardContract.View?,
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

    override fun onLoadLeadProfile(
        fullName: String,
        email: String,
        employeeId: String,
        profileImageUrl: String
    ) {
        dashBoardView?.loadLeadProfile(fullName, email, employeeId, profileImageUrl)
    }
}
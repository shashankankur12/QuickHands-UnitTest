package com.quickhandslogistics.modified.presenters

import com.quickhandslogistics.modified.contracts.LeadProfileContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.modified.models.LeadProfileModel
import com.quickhandslogistics.utils.SharedPref

class LeadProfilePresenter internal constructor(
    private var leadProfileView: LeadProfileContract.View?,
    sharedPref: SharedPref
) :
    LeadProfileContract.Presenter, LeadProfileContract.Model.OnFinishedListener {

    private val leadProfileModel: LeadProfileModel = LeadProfileModel(sharedPref)

    override fun onDestroy() {
        leadProfileView = null
    }

    override fun loadLeadProfileData() {
        leadProfileModel.fetchLeadProfileData(this)
    }

    override fun onLoadLeadProfile(employeeData: LeadProfileData) {
        leadProfileView?.loadLeadProfile(employeeData)
    }
}
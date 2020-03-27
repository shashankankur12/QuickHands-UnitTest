package com.quickhandslogistics.modified.presenters

import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.LeadProfileContract
import com.quickhandslogistics.modified.data.login.UserData
import com.quickhandslogistics.modified.data.profile.LeadProfileData
import com.quickhandslogistics.modified.data.profile.ProfileResponse
import com.quickhandslogistics.modified.models.LeadProfileModel
import com.quickhandslogistics.utils.SharedPref

class LeadProfilePresenter internal constructor(
    private var leadProfileView: LeadProfileContract.View?,
    sharedPref: SharedPref
) :
    LeadProfileContract.Presenter, LeadProfileContract.Model.OnFinishedListener {

    private val leadProfileModel: LeadProfileModel = LeadProfileModel()

    override fun onDestroy() {
        leadProfileView = null
    }

    override fun loadLeadProfileData() {
        leadProfileModel.fetchLeadProfileData(this)
    }

    override fun onFailure(message: String) {
            leadProfileView?.showAPIErrorMessage(message)
    }

    override fun onSuccess(profileResponse: ProfileResponse) {
        leadProfileView?.showLumpersData(profileResponse.data)
    }
}
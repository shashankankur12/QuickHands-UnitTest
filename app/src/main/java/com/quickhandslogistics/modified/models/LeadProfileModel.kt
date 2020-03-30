package com.quickhandslogistics.modified.models

import com.quickhandslogistics.modified.contracts.LeadProfileContract
import com.quickhandslogistics.modified.data.Dashboard.DashBoardData
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref

class LeadProfileModel(private val sharedPref: SharedPref) : LeadProfileContract.Model {

    override fun fetchLeadProfileData(onFinishedListener: LeadProfileContract.Model.OnFinishedListener) {
        val leadProfile =
            sharedPref.getClassObject(PREFERENCE_LEAD_PROFILE, DashBoardData::class.java) as DashBoardData?
        leadProfile?.let {
            onFinishedListener.onLoadLeadProfile(leadProfile)
        }
    }
}
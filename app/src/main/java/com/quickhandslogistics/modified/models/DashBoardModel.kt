package com.quickhandslogistics.modified.models

import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref

class DashBoardModel(private val sharedPref: SharedPref) : DashBoardContract.Model {

    override fun fetchLeadProfileData(onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(
            PREFERENCE_LEAD_PROFILE,
            LeadProfileData::class.java
        ) as LeadProfileData?

        if (leadProfile != null) {
            onFinishedListener.onLoadLeadProfile(leadProfile)
        }
    }
}
package com.quickhandslogistics.modified.models

import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.login.UserData
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.ValueUtils

class DashBoardModel(private val sharedPref: SharedPref) : DashBoardContract.Model {

    override fun fetchLeadProfileData(onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        val leadProfile =
            sharedPref.getClassObject(PREFERENCE_LEAD_PROFILE, UserData::class.java) as UserData?
        leadProfile?.let {
            onFinishedListener.onLoadLeadProfile(
                "${ValueUtils.getDefaultOrValue(leadProfile.firstName)} ${ValueUtils.getDefaultOrValue(
                    leadProfile.lastName
                )}",
                ValueUtils.getDefaultOrValue(leadProfile.email),
                ValueUtils.getDefaultOrValue(leadProfile.employeeId),
                ValueUtils.getDefaultOrValue(leadProfile.profileImageUrl)
            )
        }
    }
}
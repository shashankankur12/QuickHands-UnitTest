package com.quickhandslogistics.modified.models

import android.util.Log
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref

class DashBoardModel(private val sharedPref: SharedPref) : DashBoardContract.Model {

    override fun fetchLeadProfileData(onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        DataManager.getLeadProfile(object : ResponseListener<LeadProfileAPIResponse> {
            override fun onSuccess(response: LeadProfileAPIResponse) {
                if (response.success) {
                    onFinishedListener.onFetchLeadProfileSuccess(response)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(DashBoardModel::class.simpleName, error.localizedMessage!!)
                }
            }
        })

        val leadProfile = sharedPref.getClassObject(
            PREFERENCE_LEAD_PROFILE,
            LeadProfileData::class.java
        ) as LeadProfileData?

        if (leadProfile != null) {
            onFinishedListener.onLoadLeadProfile(leadProfile)
        }
    }

    override fun processLeadProfileData(
        leadProfileData: LeadProfileData,
        onFinishedListener: DashBoardContract.Model.OnFinishedListener
    ) {
        sharedPref.setClassObject(PREFERENCE_LEAD_PROFILE, leadProfileData)
        sharedPref.setString(
            AppConstant.PREFERENCE_BUILDING_ID,
            leadProfileData.buildingDetailData?.id
        )
        onFinishedListener.onLoadLeadProfile(leadProfileData)
    }
}
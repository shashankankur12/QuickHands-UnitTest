package com.quickhandslogistics.modified.models

import android.util.Log
import com.quickhandslogistics.modified.contracts.LeadProfileContract
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeadProfileModel(private val sharedPref: SharedPref) : LeadProfileContract.Model {

    override fun fetchLeadProfileDataAPI(onFinishedListener: LeadProfileContract.Model.OnFinishedListener) {
        DataManager.getService().getLeadProfile(getAuthToken()).enqueue(object : Callback<LeadProfileAPIResponse> {
            override fun onResponse(call: Call<LeadProfileAPIResponse>, response: Response<LeadProfileAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onFetchLeadProfileSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LeadProfileAPIResponse>, t: Throwable) {
                Log.e(LoginModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun fetchLeadProfileDataLocal(onFinishedListener: LeadProfileContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        leadProfile?.let {
            onFinishedListener.onLoadLeadProfile(leadProfile)
        }
    }

    override fun processLeadProfileData(leadProfileData: LeadProfileData, onFinishedListener: LeadProfileContract.Model.OnFinishedListener) {
        sharedPref.setClassObject(PREFERENCE_LEAD_PROFILE, leadProfileData)
        sharedPref.setString(AppConstant.PREFERENCE_BUILDING_ID, leadProfileData.buildingDetailData?.id)
        onFinishedListener.onLoadLeadProfile(leadProfileData)
    }
}
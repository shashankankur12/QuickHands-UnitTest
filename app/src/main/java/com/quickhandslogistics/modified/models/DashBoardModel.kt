package com.quickhandslogistics.modified.models

import android.util.Log
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.BaseResponse
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

class DashBoardModel(private val sharedPref: SharedPref) : DashBoardContract.Model {

    override fun fetchLeadProfileData(onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        DataManager.getService().getLeadProfile(getAuthToken()).enqueue(object : Callback<LeadProfileAPIResponse> {
            override fun onResponse(call: Call<LeadProfileAPIResponse>, response: Response<LeadProfileAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onFetchLeadProfileSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LeadProfileAPIResponse>, t: Throwable) {
                Log.e(DashBoardModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })

        val leadProfile = sharedPref.getClassObject(PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        if (leadProfile != null) {
            onFinishedListener.onLoadLeadProfile(leadProfile)
        }
    }

    override fun processLeadProfileData(leadProfileData: LeadProfileData, onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        sharedPref.setClassObject(PREFERENCE_LEAD_PROFILE, leadProfileData)
        sharedPref.setString(AppConstant.PREFERENCE_BUILDING_ID, leadProfileData.buildingDetailData?.id)
        onFinishedListener.onLoadLeadProfile(leadProfileData)
    }

    override fun performLogout(onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        DataManager.getService().logout(getAuthToken()).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    // Clear the local Shared Preference Data
                    sharedPref.performLogout()

                    onFinishedListener.onSuccessLogout()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(DashBoardModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
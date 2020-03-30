package com.quickhandslogistics.modified.models

import android.util.Log
import com.quickhandslogistics.modified.contracts.DashBoardContract
import com.quickhandslogistics.modified.data.Dashboard.DashBoardProfileResponse
import com.quickhandslogistics.modified.data.Dashboard.DashboardLeadProfileData
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_BUILDING_NAME
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref

class DashBoardModel(private val sharedPref: SharedPref) : DashBoardContract.Model {

    override fun fetchLeadProfileData(onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        DataManager.getLeadProfile(object : ResponseListener<DashBoardProfileResponse> {
            override fun onSuccess(responseDashBoard: DashBoardProfileResponse) {
                if (responseDashBoard.success) {
                    onFinishedListener.onSuccess(responseDashBoard)

                } else {
                    onFinishedListener.onFailure(responseDashBoard.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(LoginModel::class.simpleName, error.localizedMessage!!)
                    onFinishedListener.onFailure()
                } else if (error is String) {
                    onFinishedListener.onFailure(error)
                }
            }
        })
    }

    override fun processEmployeeData(dashboardData: DashboardLeadProfileData, onFinishedListener: DashBoardContract.Model.OnFinishedListener) {
        sharedPref.setClassObject(PREFERENCE_LEAD_PROFILE, dashboardData)
        sharedPref.setString(PREFERENCE_BUILDING_NAME, dashboardData.buildingAssignedAsLead?.buildingName)
    }
}
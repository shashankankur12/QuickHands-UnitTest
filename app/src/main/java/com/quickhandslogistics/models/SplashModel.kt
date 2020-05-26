package com.quickhandslogistics.models

import android.os.Handler
import android.util.Log
import com.quickhandslogistics.contracts.SplashContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant.Companion.PREFERENCE_LEAD_PROFILE
import com.quickhandslogistics.utils.SharedPref
import com.quickhandslogistics.utils.ValueUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashModel(private val sharedPref: SharedPref) : SplashContract.Model {

    override fun waitForSometime(delay: Long, onFinishedListener: SplashContract.Model.OnFinishedListener) {
        Handler().postDelayed({
            val leadProfile = sharedPref.getClassObject(PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
            leadProfile?.also {
                onFinishedListener.onFinished(isLoggedIn = ValueUtils.getDefaultOrValue(leadProfile.isActive))
            } ?: run {
                onFinishedListener.onFinished(isLoggedIn = false)
            }
        }, delay)
    }

    override fun performLogout(onFinishedListener: SplashContract.Model.OnFinishedListener) {
        DataManager.getService().logout(getAuthToken()).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                // Clear the local Shared Preference Data
                sharedPref.performLogout()

                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessLogout()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(SplashModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
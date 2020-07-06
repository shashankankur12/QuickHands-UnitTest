package com.quickhandslogistics.models.lumpers

import android.util.Log
import com.quickhandslogistics.contracts.lumpers.LumpersContract
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LumpersModel(private val sharedPref: SharedPref) : LumpersContract.Model {

    override fun fetchHeaderInfo(onFinishedListener: LumpersContract.Model.OnFinishedListener) {
        val leadProfile = sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java) as LeadProfileData?
        val date = DateUtils.getCurrentDateStringByEmployeeShift(pattern = DateUtils.PATTERN_NORMAL)
        val dateShiftDetail = "$date - ${ScheduleUtils.getShiftDetailString(leadProfile)}"
        onFinishedListener.onSuccessGetHeaderInfo(dateShiftDetail)
    }

    override fun fetchLumpersList(onFinishedListener: LumpersContract.Model.OnFinishedListener) {
        val dateString = DateUtils.getCurrentDateStringByEmployeeShift()

        DataManager.getService().getAllLumpersData(getAuthToken(), dateString).enqueue(object : Callback<LumperListAPIResponse> {
            override fun onResponse(call: Call<LumperListAPIResponse>, response: Response<LumperListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LumperListAPIResponse>, t: Throwable) {
                Log.e(LumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
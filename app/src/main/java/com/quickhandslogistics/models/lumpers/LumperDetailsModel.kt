package com.quickhandslogistics.models.lumpers

import android.util.Log
import com.quickhandslogistics.contracts.lumpers.LumperDetailsContract
import com.quickhandslogistics.data.lumpers.BuildingDetailsResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LumperDetailsModel(private val sharedPref: SharedPref) : LumperDetailsContract.Model {

    override fun fetchBuildingInfo(originalBuildingId: String?, onFinishedListener: LumperDetailsContract.Model.OnFinishedListener) {
        DataManager.getService().getBuildingDetails(getAuthToken(), originalBuildingId!!).enqueue(object : Callback<BuildingDetailsResponse> {
            override fun onResponse(call: Call<BuildingDetailsResponse>, response: Response<BuildingDetailsResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body())
                }
            }

            override fun onFailure(call: Call<BuildingDetailsResponse>, t: Throwable) {
                Log.e(LumperDetailsModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
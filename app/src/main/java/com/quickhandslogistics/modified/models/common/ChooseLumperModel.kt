package com.quickhandslogistics.modified.models.common

import android.util.Log
import com.quickhandslogistics.modified.contracts.common.ChooseLumperContract
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseLumperModel : ChooseLumperContract.Model {

    override fun fetchLumpersList(onFinishedListener: ChooseLumperContract.Model.OnFinishedListener) {
        DataManager.getService().getAllLumpersData(getAuthToken(), 1, AppConstant.API_PAGE_SIZE).enqueue(object : Callback<LumperListAPIResponse> {
            override fun onResponse(call: Call<LumperListAPIResponse>, response: Response<LumperListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<LumperListAPIResponse>, t: Throwable) {
                Log.e(ChooseLumperModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
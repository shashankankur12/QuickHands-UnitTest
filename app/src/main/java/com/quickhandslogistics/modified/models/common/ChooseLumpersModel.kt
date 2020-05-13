package com.quickhandslogistics.modified.models.common

import android.util.Log
import com.quickhandslogistics.modified.contracts.common.ChooseLumpersContract
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseLumpersModel : ChooseLumpersContract.Model {

    override fun fetchLumpersList(pageIndex: Int, onFinishedListener: ChooseLumpersContract.Model.OnFinishedListener) {
        DataManager.getService().getAllLumpersData(getAuthToken(), pageIndex, AppConstant.API_PAGE_SIZE).enqueue(object : Callback<LumperListAPIResponse> {
            override fun onResponse(call: Call<LumperListAPIResponse>, response: Response<LumperListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!, pageIndex)
                }
            }

            override fun onFailure(call: Call<LumperListAPIResponse>, t: Throwable) {
                Log.e(ChooseLumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
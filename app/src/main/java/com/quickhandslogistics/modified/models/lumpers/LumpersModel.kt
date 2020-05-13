package com.quickhandslogistics.modified.models.lumpers

import android.util.Log
import com.quickhandslogistics.modified.contracts.lumpers.LumpersContract
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import com.quickhandslogistics.utils.AppConstant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LumpersModel : LumpersContract.Model {

    override fun fetchLumpersList(pageIndex: Int, onFinishedListener: LumpersContract.Model.OnFinishedListener) {
        DataManager.getService().getAllLumpersData(getAuthToken(), pageIndex, AppConstant.API_PAGE_SIZE).enqueue(object : Callback<LumperListAPIResponse> {
            override fun onResponse(call: Call<LumperListAPIResponse>, response: Response<LumperListAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!, pageIndex)
                }
            }

            override fun onFailure(call: Call<LumperListAPIResponse>, t: Throwable) {
                Log.e(LumpersModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
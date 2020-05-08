package com.quickhandslogistics.modified.models.common

import android.util.Log
import com.quickhandslogistics.modified.contracts.common.ChooseLumperContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseLumperModel : ChooseLumperContract.Model {

    override fun fetchLumpersList(onFinishedListener: ChooseLumperContract.Model.OnFinishedListener) {
        DataManager.getService().getAllLumpersData(getAuthToken()).enqueue(object : Callback<AllLumpersResponse> {
            override fun onResponse(call: Call<AllLumpersResponse>, response: Response<AllLumpersResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccess(response.body()!!)
                }
            }

            override fun onFailure(call: Call<AllLumpersResponse>, t: Throwable) {
                Log.e(ChooseLumperModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
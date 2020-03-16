package com.quickhandslogistics.modified.models

import android.util.Log
import com.quickhandslogistics.modified.contracts.ChooseLumperContract
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener

class ChooseLumperModel : ChooseLumperContract.Model {

    override fun fetchLumpersList(onFinishedListener: ChooseLumperContract.Model.OnFinishedListener) {
        DataManager.getAllLumpersData(object : ResponseListener<AllLumpersResponse> {
            override fun onSuccess(response: AllLumpersResponse) {
                if (response.success) {
                    onFinishedListener.onSuccess(response)
                } else {
                    onFinishedListener.onFailure(response.message)
                }
            }

            override fun onError(error: Any) {
                if (error is Throwable) {
                    Log.e(LoginModel::class.simpleName, error.localizedMessage!!)
                }
                onFinishedListener.onFailure()
            }
        })
    }
}
package com.quickhandslogistics.models.addContainer

import android.util.Log
import com.quickhandslogistics.contracts.addContainer.AddContainerContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.addContainer.AddContainerRequest
import com.quickhandslogistics.data.addContainer.ContainerDetails
import com.quickhandslogistics.models.workSheet.AllWorkScheduleCancelModel
import com.quickhandslogistics.network.DataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddContainerModel: AddContainerContract.Model {

    override fun addTodayWorkContainer(scheduleNote: String,uploadContainer: ArrayList<ContainerDetails>, liveLoadContainer: ArrayList<ContainerDetails>, dropOffContainer: ArrayList<ContainerDetails>, onFinishedListener: AddContainerContract.Model.OnFinishedListener) {

        val scheduleNote = if (!scheduleNote.isNullOrEmpty()) scheduleNote else null
        val request = AddContainerRequest(uploadContainer,liveLoadContainer,dropOffContainer, scheduleNote)

        DataManager.getService().addSchedulesWorkItem(DataManager.getAuthToken(), request).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (DataManager.isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessAddTodayWorkContainer(response.body())
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(AllWorkScheduleCancelModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
package com.quickhandslogistics.models.buildingOperations

import android.util.Log
import com.quickhandslogistics.contracts.buildingOperations.BuildingOperationsContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.network.DataManager
import com.quickhandslogistics.network.DataManager.getAuthToken
import com.quickhandslogistics.network.DataManager.isSuccessResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingOperationsModel : BuildingOperationsContract.Model {

    override fun fetchBuildingOperationDetails(workItemId: String, onFinishedListener: BuildingOperationsContract.Model.OnFinishedListener) {
        DataManager.getService().getBuildingOperationsDetail(getAuthToken(), workItemId).enqueue(object : Callback<BuildingOperationAPIResponse> {
            override fun onResponse(call: Call<BuildingOperationAPIResponse>, response: Response<BuildingOperationAPIResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessGetBuildingOperations(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BuildingOperationAPIResponse>, t: Throwable) {
                Log.e(BuildingOperationsModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }

    override fun saveBuildingOperationDetails(
        workItemId: String, data: HashMap<String, String>, onFinishedListener: BuildingOperationsContract.Model.OnFinishedListener
    ) {
        DataManager.getService().saveBuildingOperationsDetail(getAuthToken(), workItemId, data).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (isSuccessResponse(response.isSuccessful, response.body(), response.errorBody(), onFinishedListener)) {
                    onFinishedListener.onSuccessSaveBuildingOperations()
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e(BuildingOperationsModel::class.simpleName, t.localizedMessage!!)
                onFinishedListener.onFailure()
            }
        })
    }
}
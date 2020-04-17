package com.quickhandslogistics.modified.models.buildingOperations

import android.util.Log
import com.quickhandslogistics.modified.contracts.buildingOperations.BuildingOperationsContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.modified.network.DataManager
import com.quickhandslogistics.network.ResponseListener

class BuildingOperationsModel : BuildingOperationsContract.Model {

    override fun fetchBuildingOperationDetails(
        workItemId: String,
        onFinishedListener: BuildingOperationsContract.Model.OnFinishedListener
    ) {
        DataManager.getBuildingOperationsDetail(workItemId,
            object : ResponseListener<BuildingOperationAPIResponse> {
                override fun onSuccess(response: BuildingOperationAPIResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessGetBuildingOperations(response)
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(BuildingOperationsModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }

    override fun saveBuildingOperationDetails(
        workItemId: String, data: HashMap<String, String>,
        onFinishedListener: BuildingOperationsContract.Model.OnFinishedListener
    ) {
        DataManager.saveBuildingOperationsDetail(workItemId, data,
            object : ResponseListener<BaseResponse> {
                override fun onSuccess(response: BaseResponse) {
                    if (response.success) {
                        onFinishedListener.onSuccessSaveBuildingOperations()
                    } else {
                        onFinishedListener.onFailure(response.message)
                    }
                }

                override fun onError(error: Any) {
                    if (error is Throwable) {
                        Log.e(BuildingOperationsModel::class.simpleName, error.localizedMessage!!)
                        onFinishedListener.onFailure()
                    } else if (error is String) {
                        onFinishedListener.onFailure(error)
                    }
                }
            })
    }
}
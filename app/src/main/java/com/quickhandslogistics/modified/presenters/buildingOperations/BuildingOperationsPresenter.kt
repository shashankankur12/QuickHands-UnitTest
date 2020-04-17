package com.quickhandslogistics.modified.presenters.buildingOperations

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.buildingOperations.BuildingOperationsContract
import com.quickhandslogistics.modified.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.modified.models.buildingOperations.BuildingOperationsModel
import java.util.HashMap

class BuildingOperationsPresenter(
    private var buildingOperationsView: BuildingOperationsContract.View?,
    private val resources: Resources
) : BuildingOperationsContract.Presenter, BuildingOperationsContract.Model.OnFinishedListener {

    private val buildingOperationsModel: BuildingOperationsModel = BuildingOperationsModel()

    override fun fetchBuildingOperationDetails(workItemId: String) {
        buildingOperationsView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        buildingOperationsModel.fetchBuildingOperationDetails(workItemId, this)
    }

    override fun saveBuildingOperationsData(workItemId: String, data: HashMap<String, String>) {
        buildingOperationsView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        buildingOperationsModel.saveBuildingOperationDetails(workItemId, data, this)
    }

    override fun onDestroy() {
        buildingOperationsView = null
    }

    override fun onFailure(message: String) {
        buildingOperationsView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            buildingOperationsView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong))
        } else {
            buildingOperationsView?.showAPIErrorMessage(message)
        }
    }

    override fun onSuccessGetBuildingOperations(buildingOperationAPIResponse: BuildingOperationAPIResponse) {
        buildingOperationsView?.hideProgressDialog()
        buildingOperationsView?.showBuildingOperationsData(buildingOperationAPIResponse.data)
    }

    override fun onSuccessSaveBuildingOperations() {
        buildingOperationsView?.buildingOperationsDataSaved()
    }
}
package com.quickhandslogistics.presenters.buildingOperations

import android.content.res.Resources
import android.text.TextUtils
import com.quickhandslogistics.R
import com.quickhandslogistics.contracts.buildingOperations.BuildingOperationsContract
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.models.buildingOperations.BuildingOperationsModel
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class BuildingOperationsPresenter(private var buildingOperationsView: BuildingOperationsContract.View?, private val resources: Resources) :
    BuildingOperationsContract.Presenter, BuildingOperationsContract.Model.OnFinishedListener {

    private val buildingOperationsModel = BuildingOperationsModel()

    /** View Listeners */
    override fun onDestroy() {
        buildingOperationsView = null
    }

    override fun fetchBuildingOperationDetails(workItemId: String) {
        buildingOperationsView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        buildingOperationsModel.fetchBuildingOperationDetails(workItemId, this)
    }

    override fun saveBuildingOperationsData(workItemId: String, data: HashMap<String, String>) {
        buildingOperationsView?.showProgressDialog(resources.getString(R.string.api_loading_alert_message))
        buildingOperationsModel.saveBuildingOperationDetails(workItemId, data, this)
    }

    /** Model Result Listeners */
    override fun onFailure(message: String) {
        buildingOperationsView?.hideProgressDialog()
        if (TextUtils.isEmpty(message)) {
            buildingOperationsView?.showAPIErrorMessage(resources.getString(R.string.something_went_wrong_message))
        } else {
            buildingOperationsView?.showAPIErrorMessage(message)
        }
    }

    override fun onErrorCode(errorCode: ErrorResponse) {
        buildingOperationsView?.hideProgressDialog()
        var sharedPref = SharedPref.getInstance()
        sharedPref.performLogout()
        buildingOperationsView?.showLoginScreen()
    }

    override fun onSuccessGetBuildingOperations(buildingOperationAPIResponse: BuildingOperationAPIResponse) {
        buildingOperationsView?.hideProgressDialog()
        buildingOperationsView?.showBuildingOperationsData(buildingOperationAPIResponse.data)
    }

    override fun onSuccessSaveBuildingOperations() {
        buildingOperationsView?.buildingOperationsDataSaved()
        buildingOperationsView?.hideProgressDialog()
    }
}
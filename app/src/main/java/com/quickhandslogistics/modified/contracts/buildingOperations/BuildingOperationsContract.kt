package com.quickhandslogistics.modified.contracts.buildingOperations

import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class BuildingOperationsContract {
    interface Model {
        fun fetchBuildingOperationDetails(
            workItemId: String, onFinishedListener: OnFinishedListener
        )

        fun saveBuildingOperationDetails(
            workItemId: String, data: HashMap<String, String>,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccessGetBuildingOperations(buildingOperationAPIResponse: BuildingOperationAPIResponse)
            fun onSuccessSaveBuildingOperations()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showBuildingOperationsData(data: HashMap<String, String>?)
        fun buildingOperationsDataSaved()

        interface OnAdapterItemClickListener {
            fun onItemClick(employeeData: EmployeeData)
            fun onPhoneViewClick(lumperName: String, phone: String)
        }
    }

    interface Presenter {
        fun fetchBuildingOperationDetails(workItemId: String)
        fun saveBuildingOperationsData(workItemId: String, data: HashMap<String, String>)
        fun onDestroy()
    }
}
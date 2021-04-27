package com.quickhandslogistics.contracts.lumpers

import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumpers.BuildingDetailsResponse

class LumperDetailsContract {
    interface Model {
        fun fetchBuildingInfo(originalBuildingId: String?, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onSuccess(response: BuildingDetailsResponse?)
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showBuildingInfo(employeeDataList: BuildingDetailData)
        fun showLoginScreen()
    }

    interface Presenter : BaseContract.Presenter {
        fun fetchBuildingInfo(originalBuildingId: String?)
    }
}
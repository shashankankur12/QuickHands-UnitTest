package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.Dashboard.DashBoardProfileResponse
import com.quickhandslogistics.modified.data.Dashboard.DashboardLeadProfileData

class DashBoardContract {

    interface Model {
        fun processEmployeeData(dashboardData: DashboardLeadProfileData, onFinishedListener: OnFinishedListener)

        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(dashBoardProfileResponse: DashBoardProfileResponse)
        }
    }

    interface View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataListDashboard: DashboardLeadProfileData)
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
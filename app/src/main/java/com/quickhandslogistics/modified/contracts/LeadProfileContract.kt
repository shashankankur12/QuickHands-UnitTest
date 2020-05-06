package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData

class LeadProfileContract {
    interface Model {
        fun fetchLeadProfileDataAPI(onFinishedListener: OnFinishedListener)
        fun fetchLeadProfileDataLocal(onFinishedListener: OnFinishedListener)
        fun processLeadProfileData(
            leadProfileData: LeadProfileData, onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onLoadLeadProfile(employeeData: LeadProfileData)
            fun onFetchLeadProfileSuccess(response: LeadProfileAPIResponse)
            fun onFailure(message: String = "")
        }
    }

    interface View : BaseContract.View {
        fun loadLeadProfile(employeeData: LeadProfileData)
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
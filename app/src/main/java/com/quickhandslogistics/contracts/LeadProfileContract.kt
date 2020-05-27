package com.quickhandslogistics.contracts

import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData

class LeadProfileContract {
    interface Model {
        fun fetchLeadProfileDataAPI(onFinishedListener: OnFinishedListener)
        fun fetchLeadProfileDataLocal(onFinishedListener: OnFinishedListener)
        fun processLeadProfileData(leadProfileData: LeadProfileData, onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onLoadLeadProfile(employeeData: LeadProfileData)
            fun onFetchLeadProfileSuccess(response: LeadProfileAPIResponse)
        }
    }

    interface View : BaseContract.View {
        fun loadLeadProfile(employeeData: LeadProfileData)
    }

    interface Presenter : BaseContract.Presenter {
        fun loadLeadProfileData()
    }
}
package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.profile.LeadProfileData
import com.quickhandslogistics.modified.data.profile.ProfileResponse


class LeadProfileContract {
    interface Model {
        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        interface OnFinishedListener {
            fun onFailure(message: String = "")
            fun onSuccess(profileResponse: ProfileResponse)
        }
    }

    interface View {
        fun showAPIErrorMessage(message: String)
        fun showLumpersData(employeeDataList: LeadProfileData)
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
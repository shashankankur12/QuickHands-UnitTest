package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.dashboard.LeadProfileData

class LeadProfileContract {
    interface Model {
        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        interface OnFinishedListener {
            fun onLoadLeadProfile(employeeData: LeadProfileData)
        }
    }

    interface View {
        fun loadLeadProfile(employeeData: LeadProfileData)
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
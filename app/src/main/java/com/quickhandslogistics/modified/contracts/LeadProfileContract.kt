package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.Dashboard.DashboardLeadProfileData

class LeadProfileContract {
    interface Model {
        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        interface OnFinishedListener {
            fun onLoadLeadProfile(employeeData: DashboardLeadProfileData)
        }
    }

    interface View {
        fun loadLeadProfile(employeeData: DashboardLeadProfileData)
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
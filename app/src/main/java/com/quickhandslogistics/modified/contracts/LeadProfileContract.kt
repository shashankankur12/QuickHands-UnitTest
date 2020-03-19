package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class LeadProfileContract {
    interface Model {
        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        interface OnFinishedListener {
            fun onLoadLeadProfile(employeeData: EmployeeData)
        }
    }

    interface View {
        fun loadLeadProfile(employeeData: EmployeeData)
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
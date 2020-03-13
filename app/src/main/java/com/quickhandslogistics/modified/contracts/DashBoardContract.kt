package com.quickhandslogistics.modified.contracts

class DashBoardContract {
    interface Model {
        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        interface OnFinishedListener {
            fun onLoadLeadProfile(fullName: String, email: String, employeeId: String)
        }
    }

    interface View {
        fun loadLeadProfile(fullName: String, email: String, employeeId: String)
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
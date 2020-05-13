package com.quickhandslogistics.modified.contracts.schedule

class ScheduleMainContract {
    interface View {
        interface OnScheduleFragmentInteractionListener {
            fun hideProgressDialog()
            fun showProgressDialog(message: String)
            fun fetchUnScheduledWorkItems()
        }
    }
}
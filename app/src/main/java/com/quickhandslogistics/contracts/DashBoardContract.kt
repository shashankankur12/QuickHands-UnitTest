package com.quickhandslogistics.contracts

import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileData

class DashBoardContract {

    interface Model {
        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        fun processLeadProfileData(leadProfileData: LeadProfileData, onFinishedListener: OnFinishedListener)
        fun performLogout(onFinishedListener: OnFinishedListener)

        interface OnFinishedListener : BaseContract.Model.OnFinishedListener {
            fun onLoadLeadProfile(leadProfileData: LeadProfileData)
            fun onFetchLeadProfileSuccess(response: LeadProfileAPIResponse)
            fun onSuccessLogout()
        }
    }

    interface View : BaseContract.View {
        fun showAPIErrorMessage(message: String)
        fun showLeadProfile(leadProfileData: LeadProfileData)
        fun showLoginScreen()

        interface OnFragmentInteractionListener {
            fun onNewFragmentReplaced(title: String)
            fun invalidateScheduleTimeNotes(notes: String)
            fun invalidateCancelAllSchedulesOption(isShown: Boolean)
            fun onLogoutOptionSelected()
        }
    }

    interface Presenter : BaseContract.Presenter {
        fun loadLeadProfileData()
        fun performLogout()
    }
}
package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileData

class DashBoardContract {

    interface Model {
        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        fun processLeadProfileData(
            leadProfileData: LeadProfileData,
            onFinishedListener: OnFinishedListener
        )

        interface OnFinishedListener {
            fun onLoadLeadProfile(leadProfileData: LeadProfileData)
            fun onFetchLeadProfileSuccess(response: LeadProfileAPIResponse)
        }
    }

    interface View {
        fun showLeadProfile(leadProfileData: LeadProfileData)
        interface OnFragmentInteractionListener {
            fun onNewFragmentReplaced(title: String)
            fun invalidateScheduleTimeNotes(notes: String)
            fun invalidateCancelAllSchedulesOption(isShown: Boolean)
        }
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
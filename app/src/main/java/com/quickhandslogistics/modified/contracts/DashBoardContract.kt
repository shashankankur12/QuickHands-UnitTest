package com.quickhandslogistics.modified.contracts

import com.quickhandslogistics.modified.data.dashboard.LeadProfileData

class DashBoardContract {

    interface Model {
        fun fetchLeadProfileData(onFinishedListener: OnFinishedListener)
        interface OnFinishedListener {
            fun onLoadLeadProfile(leadProfileData: LeadProfileData)
        }
    }

    interface View {
        fun showLeadProfile(leadProfileData: LeadProfileData)
        interface OnFragmentInteractionListener {
            fun onNewFragmentReplaced(title: String)
            fun invalidateScheduleTimeNotes(notes: String)
        }
    }

    interface Presenter {
        fun onDestroy()
        fun loadLeadProfileData()
    }
}
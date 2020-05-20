package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.contracts.BaseContract

class ScheduleMainContract {
    interface View {
        interface OnFragmentInteractionListener : BaseContract.View {
            fun fetchUnScheduledWorkItems()
        }
    }
}
package com.quickhandslogistics.modified.contracts.schedule

class ScheduleDetailContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onWorkItemClick(sameDay: Boolean)
        }
    }
}
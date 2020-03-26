package com.quickhandslogistics.modified.contracts.schedule

class ScheduleContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick()
            fun onImageItemClick()
        }
    }
}
package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.lumpers.LumperData

class ScheduleContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick()
        }
    }
}
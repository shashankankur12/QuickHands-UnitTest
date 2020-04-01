package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail
import java.util.*

class ScheduleDetailContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onLumperImagesClick()
            fun onWorkItemClick()
        }
    }
}
package com.quickhandslogistics.modified.contracts.schedule

import com.quickhandslogistics.modified.data.schedule.WorkItemDetail

class UnScheduleDetailContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onLumperImagesClick()
            fun onWorkItemClick(
                workItemDetail: WorkItemDetail,
                workItemType: String
            )
            fun onAddLumpersItemClick(workItemDetail: WorkItemDetail)
        }
    }
}
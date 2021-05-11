package com.quickhandslogistics.contracts.schedule

import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail

class ScheduleWorkItemContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick(workItemId: String, workItemTypeDisplayName: String, origin: String)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
            fun onNoteClick(workItemDetail: WorkItemDetail)
        }
    }
}
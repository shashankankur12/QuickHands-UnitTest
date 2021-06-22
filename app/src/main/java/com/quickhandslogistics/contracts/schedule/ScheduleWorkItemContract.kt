package com.quickhandslogistics.contracts.schedule

import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail

class ScheduleWorkItemContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick(workItem: WorkItemDetail)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
            fun onNoteClick(workItemDetail: WorkItemDetail)
        }
    }
}
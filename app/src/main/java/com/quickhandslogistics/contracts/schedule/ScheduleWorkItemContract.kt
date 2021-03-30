package com.quickhandslogistics.contracts.schedule

import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails

class ScheduleWorkItemContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick(workItemId: String, workItemTypeDisplayName: String)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
            fun onNoteClick(workItemDetail: WorkItemContainerDetails)
        }
    }
}
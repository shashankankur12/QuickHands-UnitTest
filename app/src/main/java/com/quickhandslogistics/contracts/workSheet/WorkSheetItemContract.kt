package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail

class WorkSheetItemContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick(workItemDetail: WorkItemDetail)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
            fun onNoteClick(workItemDetail: WorkItemDetail)
        }
    }
}
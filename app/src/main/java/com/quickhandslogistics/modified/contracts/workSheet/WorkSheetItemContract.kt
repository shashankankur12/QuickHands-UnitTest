package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class WorkSheetItemContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick(workItemId: String, workItemTypeDisplayName: String)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }
}
package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.data.lumpers.EmployeeData

class WorkSheetItemContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onItemClick(workItemId: String, workItemTypeDisplayName: String)
            fun onLumperImagesClick(lumpersList: ArrayList<EmployeeData>)
        }
    }
}
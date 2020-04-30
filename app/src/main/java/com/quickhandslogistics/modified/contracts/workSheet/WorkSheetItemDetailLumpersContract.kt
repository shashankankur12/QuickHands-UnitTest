package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class WorkSheetItemDetailLumpersContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onAddTimeClick(employeeData: EmployeeData)
        }
    }
}
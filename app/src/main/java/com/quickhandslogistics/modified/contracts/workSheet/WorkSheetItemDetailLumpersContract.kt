package com.quickhandslogistics.modified.contracts.workSheet

import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import com.quickhandslogistics.modified.data.workSheet.LumpersTimeSchedule

class WorkSheetItemDetailLumpersContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onAddTimeClick(employeeData: EmployeeData, timingData: LumpersTimeSchedule?)
        }
    }
}
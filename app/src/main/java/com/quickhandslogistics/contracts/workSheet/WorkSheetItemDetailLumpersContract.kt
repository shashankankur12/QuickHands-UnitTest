package com.quickhandslogistics.contracts.workSheet

import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule

class WorkSheetItemDetailLumpersContract {
    interface View {
        interface OnAdapterItemClickListener {
            fun onAddTimeClick(employeeData: LumperAttendanceData, timingData: LumpersTimeSchedule?)
            fun onRemoveLumperClick(employeeData: LumperAttendanceData, adapterPosition: Int) {

            }
        }
    }
}
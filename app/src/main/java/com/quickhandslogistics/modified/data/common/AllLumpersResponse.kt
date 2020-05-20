package com.quickhandslogistics.modified.data.common

import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class AllLumpersResponse : BaseResponse() {
    val data: ArrayList<EmployeeData>? = null
        get() = ScheduleUtils.sortEmployeesList(field)
}
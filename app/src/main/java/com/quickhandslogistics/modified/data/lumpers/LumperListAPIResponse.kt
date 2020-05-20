package com.quickhandslogistics.modified.data.lumpers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.utils.ScheduleUtils
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.PaginationResponse

class LumperListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data : PaginationResponse() {
        @SerializedName("records")
        @Expose
        var employeeDataList: ArrayList<EmployeeData>? = null
            get() = ScheduleUtils.sortEmployeesList(field)
    }
}
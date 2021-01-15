package com.quickhandslogistics.data.lumperSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.lumpers.EmployeeData

class LumperWorkDetailAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("lumperDaySheet")
        @Expose
        var lumperDaySheet: ArrayList<LumperDaySheet>? = null
            get() = if (!field.isNullOrEmpty()) field else ArrayList()

        @SerializedName("lumperInfo")
        @Expose
        var employeeData: EmployeeData? = null

        @SerializedName("lumperTimeClockDetails")
        @Expose
        var lumperAttendanceData: AttendanceDetail? = null
    }
}
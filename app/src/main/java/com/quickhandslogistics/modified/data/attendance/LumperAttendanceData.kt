package com.quickhandslogistics.modified.data.attendance

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class LumperAttendanceData : EmployeeData() {

    @SerializedName("lumperAttendance")
    @Expose
    var attendanceDetail: AttendanceDetail? = null
}
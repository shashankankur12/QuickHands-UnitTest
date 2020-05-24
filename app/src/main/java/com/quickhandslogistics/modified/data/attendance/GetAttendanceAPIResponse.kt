package com.quickhandslogistics.modified.data.attendance

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.PaginationResponse
import com.quickhandslogistics.utils.ScheduleUtils

class GetAttendanceAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("permanentLumpersAttendances")
        @Expose
        var permanentLumpersList: ArrayList<LumperAttendanceData>? = null
            get() = ScheduleUtils.sortEmployeesAttendanceList(field)

        @SerializedName("tempLumperAttendances")
        @Expose
        var temporaryLumpers: ArrayList<LumperAttendanceData>? = null
            get() = ScheduleUtils.sortEmployeesAttendanceList(field, isTemporaryLumpers = true)
    }
}
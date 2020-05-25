package com.quickhandslogistics.data.lumpers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.utils.ScheduleUtils

class LumperListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("permanentLumpers")
        @Expose
        var permanentLumpersList: ArrayList<EmployeeData>? = null
            get() = ScheduleUtils.sortEmployeesList(field)

        @SerializedName("tempLumpers")
        @Expose
        var temporaryLumpers: ArrayList<EmployeeData>? = null
            get() = ScheduleUtils.sortEmployeesList(field, isTemporaryLumpers = true)
    }
}
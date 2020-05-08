package com.quickhandslogistics.modified.data.lumperSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

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
    }
}
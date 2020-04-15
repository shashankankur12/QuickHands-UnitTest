package com.quickhandslogistics.modified.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse

class UnScheduleListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("records")
        @Expose
        var records: Records? = null
    }

    class Records {
        @SerializedName("tod")
        @Expose
        var todayScheduleList: ArrayList<ScheduleDetail>? = null

        @SerializedName("tom")
        @Expose
        var tomorrowScheduleList: ArrayList<ScheduleDetail>? = null
    }
}
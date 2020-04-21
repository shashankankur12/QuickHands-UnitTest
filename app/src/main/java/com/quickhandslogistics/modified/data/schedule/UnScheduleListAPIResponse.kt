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
        @SerializedName("day1")
        @Expose
        var day1ScheduleList: ArrayList<ScheduleDetail>? = null

        @SerializedName("day2")
        @Expose
        var day2ScheduleList: ArrayList<ScheduleDetail>? = null

        @SerializedName("day3")
        @Expose
        var day3ScheduleList: ArrayList<ScheduleDetail>? = null

        @SerializedName("day4")
        @Expose
        var day4ScheduleList: ArrayList<ScheduleDetail>? = null

        @SerializedName("day5")
        @Expose
        var day5ScheduleList: ArrayList<ScheduleDetail>? = null
    }
}
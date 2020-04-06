package com.quickhandslogistics.modified.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse

class ScheduleDetailAPIResponse : BaseResponse() {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("schedules")
        @Expose
        var schedules: Schedules? = null
    }
}

class Schedules {
    @SerializedName("scheduleIdentity")
    @Expose
    var scheduleIdentity: String? = null

    @SerializedName("live")
    @Expose
    var live: List<WorkItemDetail>? = null

    @SerializedName("drop")
    @Expose
    var drop: List<WorkItemDetail>? = null

    @SerializedName("outbounds")
    @Expose
    var outbounds: List<WorkItemDetail>? = null
}
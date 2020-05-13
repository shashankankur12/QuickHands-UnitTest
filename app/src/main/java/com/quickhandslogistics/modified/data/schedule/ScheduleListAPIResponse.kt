package com.quickhandslogistics.modified.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.PaginationResponse

class ScheduleListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data : PaginationResponse() {
        @SerializedName("records")
        @Expose
        var scheduleDetailsList: ArrayList<ScheduleDetail>? = null
    }
}
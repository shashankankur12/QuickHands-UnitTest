package com.quickhandslogistics.modified.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse

class ScheduleAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("workItems")
        @Expose
        var workItems: WorkItems? = null
    }

    inner class WorkItems {
        @SerializedName("dropTypeWorkItem")
        @Expose
        var dropTypeWorkItem: List<WorkItemDetail>? = null

        @SerializedName("liveTypeWorkItem")
        @Expose
        var liveTypeWorkItem: List<WorkItemDetail>? = null
    }
}
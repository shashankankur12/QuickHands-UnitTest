package com.quickhandslogistics.modified.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import java.io.Serializable

class ScheduleAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("workItems")
        @Expose
        var scheduleDetailsList: ArrayList<ScheduleDetail>? = null
    }
}

class ScheduleDetail : Serializable {
    @SerializedName("scheduleIdentity")
    @Expose
    var scheduleIdentity: String? = null

    @SerializedName("buildingName")
    @Expose
    var buildingName: String? = null

    @SerializedName("startDate")
    @Expose
    var startDate: String? = null

    @SerializedName("scheduleTypes")
    @Expose
    var scheduleTypes: ScheduleTypes? = null

    @SerializedName("totalNumberOfWorkItems")
    @Expose
    var totalNumberOfWorkItems: Int? = null

    var scheduleTypeNames: String = ""
}

class ScheduleTypes : Serializable {
    @SerializedName("liveLoads")
    @Expose
    var liveLoads: ArrayList<WorkItemDetail>? = null

    @SerializedName("drops")
    @Expose
    var drops: ArrayList<WorkItemDetail>? = null

    @SerializedName("outbounds")
    @Expose
    var outbounds: ArrayList<WorkItemDetail>? = null
}
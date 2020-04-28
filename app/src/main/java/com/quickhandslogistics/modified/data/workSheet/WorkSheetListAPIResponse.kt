package com.quickhandslogistics.modified.data.workSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail

class WorkSheetListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("cancelled")
        @Expose
        var cancelled: List<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()

        @SerializedName("onHold")
        @Expose
        var onHold: List<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()

        @SerializedName("inProgress")
        @Expose
        var inProgress: List<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()

        @SerializedName("scheduled")
        @Expose
        var scheduled: List<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()

        @SerializedName("completed")
        @Expose
        var completed: List<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()
    }
}
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
        var cancelled: ArrayList<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()

        @SerializedName("onHold")
        @Expose
        var onHold: ArrayList<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()

        @SerializedName("inProgress")
        @Expose
        var inProgress: ArrayList<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()

        @SerializedName("scheduled")
        @Expose
        var scheduled: ArrayList<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()

        @SerializedName("completed")
        @Expose
        var completed: ArrayList<WorkItemDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field
            } else ArrayList()
    }
}
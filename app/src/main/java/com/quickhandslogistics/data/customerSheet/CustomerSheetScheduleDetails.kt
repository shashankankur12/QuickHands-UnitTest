package com.quickhandslogistics.data.customerSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.schedule.WorkItemDetail

class CustomerSheetScheduleDetails {
    @SerializedName("cancelled")
    @Expose
    var cancelled: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("onHold")
    @Expose
    var onHold: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("inProgress")
    @Expose
    var inProgress: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("scheduled")
    @Expose
    var scheduled: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("completed")
    @Expose
    var completed: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()
}
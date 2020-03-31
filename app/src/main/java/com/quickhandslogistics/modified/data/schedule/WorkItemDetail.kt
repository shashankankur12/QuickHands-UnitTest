package com.quickhandslogistics.modified.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WorkItemDetail {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("workItemType")
    @Expose
    var workItemType: String? = null

    @SerializedName("sequence")
    @Expose
    var sequence: Int? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: String? = null

    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("scheduleIdentity")
    @Expose
    var scheduleIdentity: String? = null

    @SerializedName("startDate")
    @Expose
    var startDate: String? = null

    @SerializedName("endDate")
    @Expose
    var endDate: String? = null

    @SerializedName("isScheduledByLead")
    @Expose
    var isScheduledByLead: Boolean? = null

    @SerializedName("scheduleForWeek")
    @Expose
    var scheduleForWeek: Boolean? = null

    @SerializedName("scheduleForMonth")
    @Expose
    var scheduleForMonth: Boolean? = null

    @SerializedName("specificDates")
    @Expose
    var specificDates: List<Any>? = null

    @SerializedName("isCompleted")
    @Expose
    var isCompleted: Boolean? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("numberOfDrops")
    @Expose
    var numberOfDrops: Int? = null
}
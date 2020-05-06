package com.quickhandslogistics.modified.data.customerSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.schedule.WorkItemDetail

class CustomerSheetListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("scheduleDetails")
        @Expose
        var scheduleDetails: ScheduleDetails? = null

        @SerializedName("customerSheet")
        @Expose
        var customerSheet: CustomerSheetData? = null
    }

    class ScheduleDetails {
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


    class CustomerSheetData {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("numberOfItemsOnHold")
        @Expose
        var numberOfItemsOnHold: Int? = null

        @SerializedName("numberOfPendingItems")
        @Expose
        var numberOfPendingItems: Int? = null

        @SerializedName("numberOfCompletedItems")
        @Expose
        var numberOfCompletedItems: Int? = null

        @SerializedName("numberOfCancelledItems")
        @Expose
        var numberOfCancelledItems: Int? = null

        @SerializedName("day")
        @Expose
        var day: String? = null

        @SerializedName("isFinal")
        @Expose
        var isFinal: Boolean? = null

        @SerializedName("isSigned")
        @Expose
        var isSigned: Boolean? = null

        @SerializedName("note")
        @Expose
        var note: String? = null

        @SerializedName("customerRepresentativeSignatureUrl")
        @Expose
        var signatureUrl: String? = null

        @SerializedName("customerRepresentativeName")
        @Expose
        var customerRepresentativeName: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null
    }
}
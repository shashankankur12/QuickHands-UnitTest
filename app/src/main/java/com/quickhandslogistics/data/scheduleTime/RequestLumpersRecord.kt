package com.quickhandslogistics.data.scheduleTime

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RequestLumpersRecord {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("requestedLumpersCount")
    @Expose
    var requestedLumpersCount: Int? = null

    @SerializedName("buildingId")
    @Expose
    var buildingId: String? = null

    @SerializedName("districtManagerId")
    @Expose
    var districtManagerId: String? = null

    @SerializedName("leadId")
    @Expose
    var leadId: String? = null

    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("notesForDM")
    @Expose
    var notesForDM: String? = null

    @SerializedName("lumpersAllocated")
    @Expose
    var lumpersAllocated: List<String>? = null

    @SerializedName("requestStatus")
    @Expose
    var requestStatus: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}
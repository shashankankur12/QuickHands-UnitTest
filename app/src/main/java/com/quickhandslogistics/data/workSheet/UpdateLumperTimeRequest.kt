package com.quickhandslogistics.data.workSheet

import com.google.gson.annotations.SerializedName

data class UpdateLumperTimeRequest(
    @SerializedName("lumperId") val lumperId: String,
    @SerializedName("containerId") val workItemId: String,
    @SerializedName("timingDetails") val timingDetails: TimingDetails,
    @SerializedName("partWork") var partWork: Int? = null
)

class TimingDetails {
    @SerializedName("startTime") var startTime: Long? = null
    @SerializedName("endTime") var endTime: Long? = null
    @SerializedName("breakTimeStart") var breakTimeStart: Long? = null
    @SerializedName("breakTimeEnd") var breakTimeEnd: Long? = null
    @SerializedName("waitingTime") var waitingTime: Int? = null
}
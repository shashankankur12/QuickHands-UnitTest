package com.quickhandslogistics.data.lumperSheet

import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.workSheet.PauseTimeRequest

class LumperParameterCorrection {
    @SerializedName("partWork")
    var partWork: Int? = null
    @SerializedName("startTime")
    var startTime: Long? = null
    @SerializedName("endTime")
    var endTime: Long? = null
    @SerializedName("waitingTime")
    var waitingTime: Int? = null
    @SerializedName("breakTimes")
    var breakTimeRequests: ArrayList<PauseTimeRequest>? = null
}


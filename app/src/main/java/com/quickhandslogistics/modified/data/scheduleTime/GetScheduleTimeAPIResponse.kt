package com.quickhandslogistics.modified.data.scheduleTime

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse

class GetScheduleTimeAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {

        @SerializedName("notes")
        @Expose
        var notes: String? = null

        @SerializedName("scheduledLumpers")
        @Expose
        var scheduledLumpers: ArrayList<ScheduleTimeDetail>? = null
            get() = if (!field.isNullOrEmpty()) {
                field!!.sortWith(Comparator { scheduleTimeDetail1, scheduleTimeDetail2 ->
                    if (!scheduleTimeDetail1.lumperInfo?.firstName.isNullOrEmpty() && !scheduleTimeDetail2.lumperInfo?.firstName.isNullOrEmpty()) {
                        scheduleTimeDetail1.lumperInfo?.firstName?.toLowerCase()!!.compareTo(scheduleTimeDetail2.lumperInfo?.firstName?.toLowerCase()!!)
                    } else {
                        0
                    }
                })
                field
            } else ArrayList()
    }
}
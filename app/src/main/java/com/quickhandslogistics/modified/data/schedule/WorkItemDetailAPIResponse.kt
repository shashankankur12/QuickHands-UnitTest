package com.quickhandslogistics.modified.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.workSheet.LumpersTimeSchedule

class WorkItemDetailAPIResponse : BaseResponse() {

    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("workItem")
        @Expose
        var workItemDetail: WorkItemDetail? = null

        @SerializedName("lumpersTimeSchedule")
        @Expose
        var lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>? = null
    }
}
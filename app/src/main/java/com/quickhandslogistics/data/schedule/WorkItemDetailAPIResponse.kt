package com.quickhandslogistics.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule

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

        @SerializedName("tempLumperIds")
        @Expose
        val tempLumperIds: ArrayList<String>? = null
            get() = if (!field.isNullOrEmpty()) field else ArrayList()
    }
}
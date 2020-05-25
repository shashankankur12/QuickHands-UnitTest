package com.quickhandslogistics.data.lumperSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule

class LumperDaySheet {
    @SerializedName("lumperWorkTimingInfo")
    @Expose
    var lumpersTimeSchedule: LumpersTimeSchedule? = null

    @SerializedName("lumperWorkItemInfo")
    @Expose
    var workItemDetail: WorkItemDetail? = null
}
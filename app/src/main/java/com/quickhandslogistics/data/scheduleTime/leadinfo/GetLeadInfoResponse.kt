package com.quickhandslogistics.data.scheduleTime.leadinfo

import LeadWorkInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse

class GetLeadInfoResponse: BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: LeadWorkInfo? = null

}
package com.quickhandslogistics.data.reports

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse

class ReportResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: String? = null
}
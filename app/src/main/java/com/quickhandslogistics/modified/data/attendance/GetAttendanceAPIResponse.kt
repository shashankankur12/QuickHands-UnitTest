package com.quickhandslogistics.modified.data.attendance

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse

class GetAttendanceAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: ArrayList<LumperAttendanceData>? = null
}
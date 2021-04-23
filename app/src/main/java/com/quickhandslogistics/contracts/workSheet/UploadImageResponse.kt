package com.quickhandslogistics.contracts.workSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse

class UploadImageResponse: BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: String? = null
}
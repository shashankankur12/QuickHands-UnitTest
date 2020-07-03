package com.quickhandslogistics.data.scheduleTime

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.PaginationResponse

class RequestLumpersListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data : PaginationResponse() {
        @SerializedName("records")
        @Expose
        var records: ArrayList<RequestLumpersRecord>? = null
    }
}
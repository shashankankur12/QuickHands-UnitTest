package com.quickhandslogistics.data.schedule

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse

class GetPastFutureDateResponse:BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: ArrayList<PastFutureDates>? = null
        get() = if (field.isNullOrEmpty()) ArrayList() else field
}
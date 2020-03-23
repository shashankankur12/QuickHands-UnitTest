package com.quickhandslogistics.modified.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ErrorResponse {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: Any? = null

    @SerializedName("errCode")
    @Expose
    var errCode: Int? = null
}
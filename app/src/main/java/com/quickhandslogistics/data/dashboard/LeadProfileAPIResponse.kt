package com.quickhandslogistics.data.dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse

class LeadProfileAPIResponse : BaseResponse() {

    @SerializedName("data")
    @Expose
    var data: LeadProfileData? = null
}
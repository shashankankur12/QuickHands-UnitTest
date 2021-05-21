package com.quickhandslogistics.data.qhlContact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.lumperSheet.LumperWorkDetailAPIResponse

class QhlOfficeInfoResponse : BaseResponse(){
    @SerializedName("data")
    @Expose
    var data: ArrayList<QhlOfficeInfo>? = null
}
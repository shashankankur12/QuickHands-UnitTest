package com.quickhandslogistics.data.lumpers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.dashboard.BuildingDetailData

class BuildingDetailsResponse:BaseResponse (){
    @SerializedName("data")
    @Expose
    var data: BuildingDetailData? = null
}
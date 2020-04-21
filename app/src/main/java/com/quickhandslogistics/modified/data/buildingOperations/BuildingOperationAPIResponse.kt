package com.quickhandslogistics.modified.data.buildingOperations

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse

class BuildingOperationAPIResponse : BaseResponse() {

    @SerializedName("data")
    @Expose
    var data: HashMap<String, String>? = null
}
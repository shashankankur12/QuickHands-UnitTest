package com.quickhandslogistics.data.customerSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse

class CustomerSheetListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("scheduleDetails")
        @Expose
        var scheduleDetails: CustomerSheetScheduleDetails? = null

        @SerializedName("customerSheet")
        @Expose
        var customerSheet: CustomerSheetData? = null
    }
}
package com.quickhandslogistics.modified.data.lumperSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.utils.StringUtils
import com.quickhandslogistics.utils.ValueUtils

class LumperSheetListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("lumpersInfo")
        @Expose
        val lumpersInfo: ArrayList<LumpersInfo>? = null
            get() = if (!field.isNullOrEmpty()) {
                field.sortWith(Comparator { lumper1, lumper2 ->
                    if (!StringUtils.isNullOrEmpty(lumper1.lumperName)) {
                        lumper1.lumperName?.toLowerCase()!!.compareTo(lumper2.lumperName?.toLowerCase()!!)
                    } else {
                        0
                    }
                })
                field
            } else ArrayList()

        @SerializedName("isSheetSubmitted")
        @Expose
        var isSheetSubmitted: Boolean? = null
            get() = ValueUtils.getDefaultOrValue(field)
    }
}
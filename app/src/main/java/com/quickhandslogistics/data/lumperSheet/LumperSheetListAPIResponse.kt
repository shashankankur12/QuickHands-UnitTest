package com.quickhandslogistics.data.lumperSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
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
                    // Sort the list by Pending and Signed signature.
                    (ValueUtils.getDefaultOrValue(lumper1.sheetSigned)).compareTo((ValueUtils.getDefaultOrValue(lumper2.sheetSigned)))
                })
                field
            } else ArrayList()

        @SerializedName("isSheetSubmitted")
        @Expose
        var isSheetSubmitted: Boolean? = null
            get() = ValueUtils.getDefaultOrValue(field)

        @SerializedName("tempLumperIds")
        @Expose
        val tempLumperIds: ArrayList<String>? = null
            get() = if (!field.isNullOrEmpty()) field else ArrayList()
    }
}
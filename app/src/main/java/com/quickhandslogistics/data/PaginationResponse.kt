package com.quickhandslogistics.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class PaginationResponse {
    @SerializedName("itemCount")
    @Expose
    var itemCount: Int? = null

    @SerializedName("total")
    @Expose
    var total: Int? = null

    @SerializedName("next")
    @Expose
    var next: Int? = null

    @SerializedName("previous")
    @Expose
    var previous: Int? = null

    @SerializedName("pageCount")
    @Expose
    var pageCount: Int? = null
}
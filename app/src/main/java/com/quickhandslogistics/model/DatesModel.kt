package com.quickhandslogistics.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DatesModel : Serializable {

    @SerializedName("date")
    @Expose
    var date: Int? = null
    @SerializedName("isSelected")
    @Expose
    var selected: Boolean? = null
    @SerializedName("isEventAvailable")
    @Expose
    var eventAvailable: Boolean? = null
}

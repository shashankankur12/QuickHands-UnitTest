package com.quickhandslogistics.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ScheduledEvents : Serializable {

    @SerializedName("id")
    @Expose
    var id : String ?= null

    @SerializedName("name")
    @Expose
    var name : String ?= null

    @SerializedName("location")
    @Expose
    var location : String ?= null
}

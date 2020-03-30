package com.quickhandslogistics.modified.data.Dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DashBoardData : DashboardLeadProfileData(), Serializable {
    @SerializedName("token")
    @Expose
    var token: String? = null
}
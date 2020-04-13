package com.quickhandslogistics.modified.data.dashboard

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.lumpers.EmployeeData
import java.io.Serializable

class LeadProfileData : EmployeeData(), Parcelable {
    @SerializedName("buildingAssignedAsLead")
    @Expose
    var buildingDetailData: BuildingDetailData? = null
}
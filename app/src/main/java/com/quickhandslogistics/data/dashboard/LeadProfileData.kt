package com.quickhandslogistics.data.dashboard

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.lumpers.EmployeeData

class LeadProfileData : EmployeeData(), Parcelable {
    @SerializedName("buildingAssignedAsLead")
    @Expose
    var buildingDetailData: BuildingDetailData? = null
}
package com.quickhandslogistics.modified.data.Dashboard

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class BuildingAssignedAsLead : Serializable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("buildingName")
    @Expose
    var buildingName: String? = null

    @SerializedName("buildingCity")
    @Expose
    var buildingCity: String? = null

    @SerializedName("buildingState")
    @Expose
    var buildingState: String? = null

    @SerializedName("buildingZipcode")
    @Expose
    var buildingZipcode: String? = null

    @SerializedName("buildingAddress")
    @Expose
    var buildingAddress: String? = null

    @SerializedName("buildingNumber")
    @Expose
    var buildingNumber: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("parameters")
    @Expose
    var parameters: String? = null

    @SerializedName("addedBy")
    @Expose
    var addedBy: String? = null

    @SerializedName("isBuildingVerified")
    @Expose
    var isBuildingVerified: Boolean? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}
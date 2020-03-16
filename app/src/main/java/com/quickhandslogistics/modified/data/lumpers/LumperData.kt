package com.quickhandslogistics.modified.data.lumpers

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LumperData : Serializable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("profileImageUrl")
    @Expose
    var profileImageUrl: String? = null

    @SerializedName("isEmailVerified")
    @Expose
    var isEmailVerified: Boolean? = null

    @SerializedName("isPhoneVerified")
    @Expose
    var isPhoneVerified: Boolean? = null

    @SerializedName("role")
    @Expose
    var role: String? = null

    @SerializedName("employeeId")
    @Expose
    var employeeId: Any? = null

    @SerializedName("shift")
    @Expose
    var shift: String? = null

    @SerializedName("shiftHours")
    @Expose
    var shiftHours: Any? = null

    @SerializedName("workSchedule")
    @Expose
    var workSchedule: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("primaryBuilding")
    @Expose
    var primaryBuilding: Any? = null

    @SerializedName("abilityToTravelBetweenBuildings")
    @Expose
    var abilityToTravelBetweenBuildings: Boolean? = null

    @SerializedName("milesRadiusFromPrimaryBuilding")
    @Expose
    var milesRadiusFromPrimaryBuilding: String? = null

    @SerializedName("hiringDate")
    @Expose
    var hiringDate: Any? = null

    @SerializedName("jobDescription")
    @Expose
    var jobDescription: String? = null

    @SerializedName("lastDayWorked")
    @Expose
    var lastDayWorked: Any? = null

    @SerializedName("fullTime")
    @Expose
    var fullTime: Boolean? = null
}
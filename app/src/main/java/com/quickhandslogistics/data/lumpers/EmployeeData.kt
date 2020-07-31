package com.quickhandslogistics.data.lumpers

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class EmployeeData() : Parcelable {
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

    @SerializedName("department")
    @Expose
    var department: String? = null

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
    var employeeId: String? = null

    @SerializedName("shift")
    @Expose
    var shift: String? = null

    @SerializedName("shiftHours")
    @Expose
    var shiftHours: String? = null

    @SerializedName("workSchedule")
    @Expose
    var workSchedule: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("primaryBuilding")
    @Expose
    var primaryBuilding: String? = null

    @SerializedName("abilityToTravelBetweenBuildings")
    @Expose
    var abilityToTravelBetweenBuildings: Boolean? = null

    @SerializedName("milesRadiusFromPrimaryBuilding")
    @Expose
    var milesRadiusFromPrimaryBuilding: String? = null

    @SerializedName("hiringDate")
    @Expose
    var hiringDate: String? = null

    @SerializedName("jobDescription")
    @Expose
    var jobDescription: String? = null

    @SerializedName("scheduleNotes")
    @Expose
    var scheduleNotes: String? = null

    @SerializedName("lastDayWorked")
    @Expose
    var lastDayWorked: String? = null

    @SerializedName("fullTime")
    @Expose
    var fullTime: Boolean? = null

    var isTemporaryAssigned: Boolean = false

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        email = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
        phone = parcel.readString()
        department = parcel.readString()
        isActive = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        profileImageUrl = parcel.readString()
        isEmailVerified = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isPhoneVerified = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        role = parcel.readString()
        employeeId = parcel.readString()
        shift = parcel.readString()
        shiftHours = parcel.readString()
        workSchedule = parcel.readString()
        title = parcel.readString()
        primaryBuilding = parcel.readString()
        abilityToTravelBetweenBuildings = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        milesRadiusFromPrimaryBuilding = parcel.readString()
        hiringDate = parcel.readString()
        jobDescription = parcel.readString()
        scheduleNotes = parcel.readString()
        lastDayWorked = parcel.readString()
        fullTime = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isTemporaryAssigned = parcel.readValue(Boolean::class.java.classLoader) as Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(phone)
        parcel.writeString(department)
        parcel.writeValue(isActive)
        parcel.writeString(profileImageUrl)
        parcel.writeValue(isEmailVerified)
        parcel.writeValue(isPhoneVerified)
        parcel.writeString(role)
        parcel.writeString(employeeId)
        parcel.writeString(shift)
        parcel.writeString(shiftHours)
        parcel.writeString(workSchedule)
        parcel.writeString(title)
        parcel.writeString(primaryBuilding)
        parcel.writeValue(abilityToTravelBetweenBuildings)
        parcel.writeString(milesRadiusFromPrimaryBuilding)
        parcel.writeString(hiringDate)
        parcel.writeString(jobDescription)
        parcel.writeString(scheduleNotes)
        parcel.writeString(lastDayWorked)
        parcel.writeValue(fullTime)
        parcel.writeValue(isTemporaryAssigned)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmployeeData> {
        override fun createFromParcel(parcel: Parcel): EmployeeData {
            return EmployeeData(parcel)
        }

        override fun newArray(size: Int): Array<EmployeeData?> {
            return arrayOfNulls(size)
        }
    }
}
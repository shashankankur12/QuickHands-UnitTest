package com.quickhandslogistics.data.scheduleTime.leadinfo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RequestLumperInfo() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("originalBuildingId")
    @Expose
    var originalBuildingId: String? = null

    @SerializedName("tempBuildingId")
    @Expose
    var tempBuildingId: String? = null

    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("lumperId")
    @Expose
    var lumperId : String? = null

    @SerializedName("firstName")
    @Expose
    var firstName : String? = null

    @SerializedName("lastName")
    @Expose
    var lastName : String? = null

    @SerializedName("employeeId")
    @Expose
    var employeeId : String? = null

    @SerializedName("shift")
    @Expose
    var shift    : String? = null

    @SerializedName("department")
    @Expose
    var department    : String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt    : String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt    : String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        originalBuildingId = parcel.readString()
        tempBuildingId = parcel.readString()
        day = parcel.readString()
        lumperId = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
        employeeId = parcel.readString()
        shift = parcel.readString()
        department = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(originalBuildingId)
        parcel.writeString(tempBuildingId)
        parcel.writeString(day)
        parcel.writeString(lumperId)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(employeeId)
        parcel.writeString(shift)
        parcel.writeString(department)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RequestLumperInfo> {
        override fun createFromParcel(parcel: Parcel): RequestLumperInfo {
            return RequestLumperInfo(parcel)
        }

        override fun newArray(size: Int): Array<RequestLumperInfo?> {
            return arrayOfNulls(size)
        }
    }
}
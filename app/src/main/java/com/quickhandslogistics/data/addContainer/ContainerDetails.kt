package com.quickhandslogistics.data.addContainer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.attendance.AttendanceDetail

class ContainerDetails() : Parcelable {

    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("workItemType")
    @Expose
    var workItemType: String? = null

    @SerializedName("sequence")
    @Expose
    var sequence: String? = null

    @SerializedName("numberOfDrops")
    @Expose
    var numberOfDrops: String? = null

    @SerializedName("isDisabled")
    @Expose
    var isDisabled: Boolean? = null

    @SerializedName("readonlypermission")
    @Expose
    var readonlypermission: Boolean? = null


    constructor(parcel: Parcel) : this() {
        startTime = parcel.readString()
        workItemType = parcel.readString()
        sequence = parcel.readString()
        numberOfDrops = parcel.readString()
        isDisabled = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        readonlypermission = parcel.readValue(Boolean::class.java.classLoader) as? Boolean

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(startTime)
        parcel.writeString(workItemType)
        parcel.writeString(sequence)
        parcel.writeString(numberOfDrops)
        parcel.writeValue(isDisabled)
        parcel.writeValue(readonlypermission)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AttendanceDetail> {
        override fun createFromParcel(parcel: Parcel): AttendanceDetail {
            return AttendanceDetail(parcel)
        }

        override fun newArray(size: Int): Array<AttendanceDetail?> {
            return arrayOfNulls(size)
        }
    }
}
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

    @Transient
    var workItemType: String? = null

    @SerializedName("quantity")
    @Expose
    var sequence: String? = null

    @SerializedName("numberOfDrops")
    @Expose
    var numberOfDrops: String? = null

    constructor(parcel: Parcel) : this() {
        startTime = parcel.readString()
        workItemType = parcel.readString()
        sequence = parcel.readString()
        numberOfDrops = parcel.readString()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(startTime)
        parcel.writeString(workItemType)
        parcel.writeString(sequence)
        parcel.writeString(numberOfDrops)
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
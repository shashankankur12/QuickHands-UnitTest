package com.quickhandslogistics.data.attendance

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AttendanceDetail() : Parcelable {

    @Expose
    var lumperId: String? = null

    @SerializedName("morningPunchIn")
    @Expose
    var morningPunchIn: String? = null

    @SerializedName("eveningPunchOut")
    @Expose
    var eveningPunchOut: String? = null
    @SerializedName("lunchPunchIn")
    @Expose
    var lunchPunchIn: String? = null

    @SerializedName("lunchPunchOut")
    @Expose
    var lunchPunchOut: String? = null

    @SerializedName("isPresent")
    @Expose
    var isPresent: Boolean? = null

    @SerializedName("attendanceNote")
    @Expose
    var attendanceNote: String? = null

    @Transient
    var isPresentChanged = false

    @Transient
    var isMorningPunchInChanged = false

    @Transient
    var isEveningPunchOutChanged = false

    @Transient
    var isLunchPunchInChanged = false

    @Transient
    var isLunchPunchOutChanged = false

    constructor(parcel: Parcel) : this() {
        lumperId = parcel.readString()
        morningPunchIn = parcel.readString()
        eveningPunchOut = parcel.readString()
        lunchPunchIn = parcel.readString()
        lunchPunchOut = parcel.readString()
        isPresent = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        attendanceNote = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lumperId)
        parcel.writeString(morningPunchIn)
        parcel.writeString(eveningPunchOut)
        parcel.writeString(lunchPunchIn)
        parcel.writeString(lunchPunchOut)
        parcel.writeValue(isPresent)
        parcel.writeString(attendanceNote)
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

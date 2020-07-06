package com.quickhandslogistics.data.dashboard

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ShiftDetail() : Parcelable {
    @SerializedName("startTime")
    @Expose
    var startTime: Long? = null

    @SerializedName("endTime")
    @Expose
    var endTime: Long? = null

    constructor(parcel: Parcel) : this() {
        startTime = parcel.readValue(Int::class.java.classLoader) as? Long
        endTime = parcel.readValue(Int::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(startTime)
        parcel.writeValue(endTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShiftDetail> {
        override fun createFromParcel(parcel: Parcel): ShiftDetail {
            return ShiftDetail(parcel)
        }

        override fun newArray(size: Int): Array<ShiftDetail?> {
            return arrayOfNulls(size)
        }
    }
}
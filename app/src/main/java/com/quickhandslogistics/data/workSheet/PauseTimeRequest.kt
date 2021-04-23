package com.quickhandslogistics.data.workSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PauseTimeRequest() : Parcelable {
    @SerializedName("startTime")
    @Expose
    var startTime: Long? = null

    @SerializedName("endTime")
    @Expose
    var endTime: Long? = null

    constructor(parcel: Parcel) : this() {
        startTime = parcel.readLong()
        endTime = parcel.readLong()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(startTime)
        parcel.writeValue(endTime)
    }

    companion object CREATOR : Parcelable.Creator<PauseTime> {
        override fun createFromParcel(parcel: Parcel): PauseTime {
            return PauseTime(parcel)
        }

        override fun newArray(size: Int): Array<PauseTime?> {
            return arrayOfNulls(size)
        }
    }
}
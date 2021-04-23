package com.quickhandslogistics.data.workSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PauseTime () : Parcelable {
    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("endTime")
    @Expose
    var endTime: String? = null

    constructor(parcel: Parcel) : this() {
        startTime = parcel.readString()
        endTime = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(startTime)
        parcel.writeString(endTime)
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
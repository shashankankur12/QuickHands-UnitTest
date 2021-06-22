package com.quickhandslogistics.data.schedule

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PastFutureDates() : Parcelable {

    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    constructor(parcel: Parcel) : this() {
        day = parcel.readString()
        code = parcel.readValue(Int::class.java.classLoader) as? Int
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(day)
        parcel.writeValue(code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PastFutureDates> {
        override fun createFromParcel(parcel: Parcel): PastFutureDates {
            return PastFutureDates(parcel)
        }

        override fun newArray(size: Int): Array<PastFutureDates?> {
            return arrayOfNulls(size)
        }
    }
}
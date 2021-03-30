package com.quickhandslogistics.data.qhlContact

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class QhlOfficeInfo() : Parcelable {
    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("opens")
    @Expose
    var opens: String? = null

    @SerializedName("closes")
    @Expose
    var closes: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    constructor(parcel: Parcel) : this() {
        address = parcel.readString()
        phone = parcel.readString()
        opens = parcel.readString()
        closes = parcel.readString()
        email = parcel.readString()
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeString(opens)
        parcel.writeString(closes)
        parcel.writeString(email)
    }

    companion object CREATOR : Parcelable.Creator<QhlOfficeInfo> {
        override fun createFromParcel(parcel: Parcel): QhlOfficeInfo {
            return QhlOfficeInfo(parcel)
        }

        override fun newArray(size: Int): Array<QhlOfficeInfo?> {
            return arrayOfNulls(size)
        }
    }

}
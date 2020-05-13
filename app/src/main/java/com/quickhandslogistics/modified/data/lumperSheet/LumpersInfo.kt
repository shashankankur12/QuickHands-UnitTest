package com.quickhandslogistics.modified.data.lumperSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LumpersInfo() : Parcelable {
    @SerializedName("lumperId")
    @Expose
    var lumperId: String? = null

    @SerializedName("lumperName")
    @Expose
    var lumperName: String? = null

    @SerializedName("lumperEmployeeId")
    @Expose
    var lumperEmployeeId: String? = null

    @SerializedName("lumperImageUrl")
    @Expose
    var lumperImageUrl: String? = null

    @SerializedName("sheetSigned")
    @Expose
    var sheetSigned: Boolean? = null

    constructor(parcel: Parcel) : this() {
        lumperId = parcel.readString()
        lumperName = parcel.readString()
        lumperEmployeeId = parcel.readString()
        lumperImageUrl = parcel.readString()
        sheetSigned = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lumperId)
        parcel.writeString(lumperName)
        parcel.writeString(lumperEmployeeId)
        parcel.writeString(lumperImageUrl)
        parcel.writeValue(sheetSigned)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LumpersInfo> {
        override fun createFromParcel(parcel: Parcel): LumpersInfo {
            return LumpersInfo(parcel)
        }

        override fun newArray(size: Int): Array<LumpersInfo?> {
            return arrayOfNulls(size)
        }
    }
}
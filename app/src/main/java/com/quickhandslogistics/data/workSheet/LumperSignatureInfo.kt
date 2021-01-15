package com.quickhandslogistics.data.workSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LumperSignatureInfo() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("lumperSignatureUrl")
    @Expose
    var lumperSignatureUrl: String? = null

    @SerializedName("buildingId")
    @Expose
    var buildingId: String? = null

    @SerializedName("shift")
    @Expose
    var shift: String? = null

    @SerializedName("department")
    @Expose
    var department: String? = null

    @SerializedName("lumperId")
    @Expose
    var lumperId: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        lumperSignatureUrl = parcel.readString()
        buildingId = parcel.readString()
        shift = parcel.readString()
        department = parcel.readString()
        lumperId = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(lumperSignatureUrl)
        parcel.writeString(buildingId)
        parcel.writeString(shift)
        parcel.writeString(department)
        parcel.writeString(lumperId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LumperSignatureInfo> {
        override fun createFromParcel(parcel: Parcel): LumperSignatureInfo {
            return LumperSignatureInfo(parcel)
        }

        override fun newArray(size: Int): Array<LumperSignatureInfo?> {
            return arrayOfNulls(size)
        }
    }

}
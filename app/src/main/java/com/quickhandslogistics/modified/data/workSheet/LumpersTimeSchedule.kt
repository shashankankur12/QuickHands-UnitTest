package com.quickhandslogistics.modified.data.workSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LumpersTimeSchedule() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("lumperId")
    @Expose
    var lumperId: String? = null

    @SerializedName("workItemId")
    @Expose
    var workItemId: String? = null

    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("waitingTime")
    @Expose
    var waitingTime: String? = null

    @SerializedName("endTime")
    @Expose
    var endTime: String? = null

    @SerializedName("breakTimeStart")
    @Expose
    var breakTimeStart: String? = null

    @SerializedName("breakTimeEnd")
    @Expose
    var breakTimeEnd: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("sheetSigned")
    @Expose
    var sheetSigned: Boolean? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        lumperId = parcel.readString()
        workItemId = parcel.readString()
        startTime = parcel.readString()
        waitingTime = parcel.readString()
        endTime = parcel.readString()
        breakTimeStart = parcel.readString()
        breakTimeEnd = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
        sheetSigned = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(lumperId)
        parcel.writeString(workItemId)
        parcel.writeString(startTime)
        parcel.writeString(waitingTime)
        parcel.writeString(endTime)
        parcel.writeString(breakTimeStart)
        parcel.writeString(breakTimeEnd)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeValue(sheetSigned)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LumpersTimeSchedule> {
        override fun createFromParcel(parcel: Parcel): LumpersTimeSchedule {
            return LumpersTimeSchedule(parcel)
        }

        override fun newArray(size: Int): Array<LumpersTimeSchedule?> {
            return arrayOfNulls(size)
        }
    }
}
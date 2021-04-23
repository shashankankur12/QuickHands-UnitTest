package com.quickhandslogistics.data.scheduleTime

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.scheduleTime.leadinfo.RequestLumperInfo

class RequestLumpersRecord() :Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("requestedLumpersCount")
    @Expose
    var requestedLumpersCount: Int? = null

    @SerializedName("buildingId")
    @Expose
    var buildingId: String? = null

    @SerializedName("districtManagerId")
    @Expose
    var districtManagerId: String? = null

    @SerializedName("leadId")
    @Expose
    var leadId: String? = null

    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("notesForLumper")
    @Expose
    var notesForLumper: String? = null

    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("notesForDM")
    @Expose
    var notesForDM: String? = null

    @SerializedName("lumpersAllocated")
    @Expose
    var lumpersAllocated: List<String>? = null

    @SerializedName("requestStatus")
    @Expose
    var requestStatus: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("tempLumpers")
    @Expose
    var tempLumpers: ArrayList<RequestLumperInfo>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        requestedLumpersCount = parcel.readValue(Int::class.java.classLoader) as? Int
        buildingId = parcel.readString()
        districtManagerId = parcel.readString()
        leadId = parcel.readString()
        day = parcel.readString()
        notesForDM = parcel.readString()
        lumpersAllocated = parcel.createStringArrayList()
        requestStatus = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
        startTime = parcel.readString()
        notesForLumper = parcel.readString()
        tempLumpers = parcel.createTypedArrayList(RequestLumperInfo)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeValue(requestedLumpersCount)
        parcel.writeString(buildingId)
        parcel.writeString(districtManagerId)
        parcel.writeString(leadId)
        parcel.writeString(day)
        parcel.writeString(notesForDM)
        parcel.writeStringList(lumpersAllocated)
        parcel.writeString(requestStatus)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(startTime)
        parcel.writeString(notesForLumper)
        parcel.writeTypedList(tempLumpers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RequestLumpersRecord> {
        override fun createFromParcel(parcel: Parcel): RequestLumpersRecord {
            return RequestLumpersRecord(parcel)
        }

        override fun newArray(size: Int): Array<RequestLumpersRecord?> {
            return arrayOfNulls(size)
        }
    }
}
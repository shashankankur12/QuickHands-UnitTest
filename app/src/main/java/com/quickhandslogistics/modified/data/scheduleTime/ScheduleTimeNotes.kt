package com.quickhandslogistics.modified.data.scheduleTime

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ScheduleTimeNotes() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("lumperSchedulingId")
    @Expose
    var lumperSchedulingId: String? = null

    @SerializedName("requestedLumpersCount")
    @Expose
    var requestedLumpersCount: Int? = null

    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("notesForDM")
    @Expose
    var notesForDM: String? = null

    @SerializedName("notesForLead")
    @Expose
    var notesForLead: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        lumperSchedulingId = parcel.readString()
        requestedLumpersCount = parcel.readValue(Int::class.java.classLoader) as? Int
        day = parcel.readString()
        notesForDM = parcel.readString()
        notesForLead = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(lumperSchedulingId)
        parcel.writeValue(requestedLumpersCount)
        parcel.writeString(day)
        parcel.writeString(notesForDM)
        parcel.writeString(notesForLead)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleTimeNotes> {
        override fun createFromParcel(parcel: Parcel): ScheduleTimeNotes {
            return ScheduleTimeNotes(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleTimeNotes?> {
            return arrayOfNulls(size)
        }
    }
}
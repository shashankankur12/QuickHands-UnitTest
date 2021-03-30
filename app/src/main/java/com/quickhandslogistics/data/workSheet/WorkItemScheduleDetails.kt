package com.quickhandslogistics.data.workSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.ScheduleDetail
import com.quickhandslogistics.data.schedule.WorkItemDetail

class WorkItemScheduleDetails() : Parcelable {

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("shift")
    @Expose
    var shift: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("createdBy")
    @Expose
    var createdBy: String? = null

    @SerializedName("customerId")
    @Expose
    var customerId: String? = null

    @SerializedName("scheduleNote")
    @Expose
    var scheduleNote: String? = null

    @SerializedName("scheduledFrom")
    @Expose
    var scheduledFrom: String? = null

    @SerializedName("scheduledTo")
    @Expose
    var scheduledTo: String? = null

    @SerializedName("isScheduledByLead")
    @Expose
    var isScheduledByLead: Boolean? = null

    @SerializedName("scheduleForWeek")
    @Expose
    var scheduleForWeek: Boolean? = null

    @SerializedName("scheduleForMonth")
    @Expose
    var scheduleForMonth: Boolean? = null

    @SerializedName("specificDates")
    @Expose
    var specificDates: List<String>? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null


    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        shift = parcel.readString()
        type = parcel.readString()
        createdBy = parcel.readString()
        customerId = parcel.readString()
        scheduleNote = parcel.readString()
        scheduledFrom = parcel.readString()
        scheduledTo = parcel.readString()
        isScheduledByLead = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        scheduleForWeek = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        scheduleForMonth = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        specificDates = parcel.createStringArrayList()
        createdAt = parcel.readString()!!
        updatedAt = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(shift)
        parcel.writeString(type)
        parcel.writeString(createdBy)
        parcel.writeString(customerId)
        parcel.writeString(scheduleNote)
        parcel.writeString(scheduledFrom)
        parcel.writeString(scheduledTo)
        parcel.writeValue(isScheduledByLead)
        parcel.writeValue(scheduleForWeek)
        parcel.writeValue(scheduleForMonth)
        parcel.writeStringList(specificDates)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkItemScheduleDetails> {
        override fun createFromParcel(parcel: Parcel): WorkItemScheduleDetails {
            return WorkItemScheduleDetails(parcel)
        }

        override fun newArray(size: Int): Array<WorkItemScheduleDetails?> {
            return arrayOfNulls(size)
        }
    }
}
package com.quickhandslogistics.data.customerSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.ContainerGroupNote
import java.io.Serializable

class CustomerSheetScheduleDetails() : Parcelable {
    @SerializedName("cancelled")
    @Expose
    var cancelled: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("onHold")
    @Expose
    var onHold: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("inProgress")
    @Expose
    var inProgress: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("scheduled")
    @Expose
    var scheduled: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("completed")
    @Expose
    var completed: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("unfinished")
    @Expose
    var unfinished: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("notopen")
    @Expose
    var notOpen: ArrayList<WorkItemDetail>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()

    @SerializedName("cancelledNotes")
    @Expose
    var containerGroupNote: ContainerGroupNote? = null

    @SerializedName("unfinishedNotes")
    @Expose
    var unfinishedNotes: ContainerGroupNote? = null

    @SerializedName("notOpenNotes")
    @Expose
    var notOpenNotes: ContainerGroupNote? = null

    constructor(parcel: Parcel) : this() {
        cancelled = parcel.createTypedArrayList(WorkItemDetail)
        onHold = parcel.createTypedArrayList(WorkItemDetail)
        inProgress = parcel.createTypedArrayList(WorkItemDetail)
        scheduled = parcel.createTypedArrayList(WorkItemDetail)
        completed = parcel.createTypedArrayList(WorkItemDetail)
        unfinished = parcel.createTypedArrayList(WorkItemDetail)
        notOpen = parcel.createTypedArrayList(WorkItemDetail)
        containerGroupNote= parcel.readValue(ContainerGroupNote::class.java.classLoader) as ContainerGroupNote?
        unfinishedNotes= parcel.readValue(ContainerGroupNote::class.java.classLoader) as ContainerGroupNote?
        notOpenNotes= parcel.readValue(ContainerGroupNote::class.java.classLoader) as ContainerGroupNote?
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(cancelled)
        parcel.writeTypedList(onHold)
        parcel.writeTypedList(inProgress)
        parcel.writeTypedList(scheduled)
        parcel.writeTypedList(completed)
        parcel.writeTypedList(unfinished)
        parcel.writeTypedList(notOpen)
        parcel.writeValue(containerGroupNote)
        parcel.writeValue(unfinishedNotes)
        parcel.writeValue(notOpenNotes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerSheetScheduleDetails> {
        override fun createFromParcel(parcel: Parcel): CustomerSheetScheduleDetails {
            return CustomerSheetScheduleDetails(parcel)
        }

        override fun newArray(size: Int): Array<CustomerSheetScheduleDetails?> {
            return arrayOfNulls(size)
        }
    }
}
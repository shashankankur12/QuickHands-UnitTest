package com.quickhandslogistics.data.customerSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.schedule.WorkItemDetail
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

    constructor(parcel: Parcel) : this() {
        cancelled = parcel.createTypedArrayList(WorkItemDetail)
        onHold = parcel.createTypedArrayList(WorkItemDetail)
        inProgress = parcel.createTypedArrayList(WorkItemDetail)
        scheduled = parcel.createTypedArrayList(WorkItemDetail)
        completed = parcel.createTypedArrayList(WorkItemDetail)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(cancelled)
        parcel.writeTypedList(onHold)
        parcel.writeTypedList(inProgress)
        parcel.writeTypedList(scheduled)
        parcel.writeTypedList(completed)
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
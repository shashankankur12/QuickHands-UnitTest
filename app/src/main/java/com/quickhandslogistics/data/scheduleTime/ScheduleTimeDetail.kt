package com.quickhandslogistics.data.scheduleTime

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.lumpers.EmployeeData

class ScheduleTimeDetail() : Parcelable {
    @SerializedName("lumperInfo")
    @Expose
    var lumperInfo: EmployeeData? = null

    @SerializedName("reportingTimeAndDay")
    @Expose
    var reportingTimeAndDay: String? = null

    @SerializedName("hasClockedOut")
    @Expose
    var isPresent: Boolean? = null

    constructor(parcel: Parcel) : this() {
        lumperInfo = parcel.readParcelable(EmployeeData::class.java.classLoader)
        reportingTimeAndDay = parcel.readString()
        isPresent = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(lumperInfo, flags)
        parcel.writeString(reportingTimeAndDay)
        parcel.writeValue(isPresent)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleTimeDetail> {
        override fun createFromParcel(parcel: Parcel): ScheduleTimeDetail {
            return ScheduleTimeDetail(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleTimeDetail?> {
            return arrayOfNulls(size)
        }
    }
}
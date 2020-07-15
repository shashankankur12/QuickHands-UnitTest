package com.quickhandslogistics.data.lumperSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.schedule.WorkItemDetail
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule

class LumperDaySheet() :Parcelable  {
    @SerializedName("lumperWorkTimingInfo")
    @Expose
    var lumpersTimeSchedule: LumpersTimeSchedule? = null

    @SerializedName("lumperWorkItemInfo")
    @Expose
    var workItemDetail: WorkItemDetail? = null

    constructor(parcel: Parcel) : this() {
        lumpersTimeSchedule = parcel.readParcelable(LumpersTimeSchedule::class.java.classLoader)
        workItemDetail = parcel.readParcelable(WorkItemDetail::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(lumpersTimeSchedule, flags)
        parcel.writeParcelable(workItemDetail, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LumperDaySheet> {
        override fun createFromParcel(parcel: Parcel): LumperDaySheet {
            return LumperDaySheet(parcel)
        }

        override fun newArray(size: Int): Array<LumperDaySheet?> {
            return arrayOfNulls(size)
        }
    }
}
package com.quickhandslogistics.data.lumperSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.workSheet.LumpersTimeSchedule

class CorrectionRequest() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("lumperId")
    @Expose
    var lumperId: String? = null

    @SerializedName("containerId")
    @Expose
    var containerId: String? = null

    @SerializedName("notesForQHL")
    @Expose
    var notesForQHL: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("correctionNote")
    @Expose
    var correctionNote: String? = null

    @SerializedName("workTiming")
    @Expose
    var workTiming: LumpersTimeSchedule? = null

    @SerializedName("containerParameters")
    @Expose
    var containerParameters: HashMap<String, String>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        lumperId = parcel.readString()
        status = parcel.readString()
        containerId = parcel.readString()
        notesForQHL = parcel.readString()
        correctionNote = parcel.readString()
        workTiming = parcel.readParcelable(LumpersTimeSchedule::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(lumperId)
        parcel.writeString(status)
        parcel.writeString(containerId)
        parcel.writeString(notesForQHL)
        parcel.writeString(correctionNote)
        parcel.writeParcelable(workTiming, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CorrectionRequest> {
        override fun createFromParcel(parcel: Parcel): CorrectionRequest {
            return CorrectionRequest(parcel)
        }

        override fun newArray(size: Int): Array<CorrectionRequest?> {
            return arrayOfNulls(size)
        }
    }

    class WorkTime() : Parcelable {
        @SerializedName("timingDetails")
        @Expose
        var timingDetails: LumpersTimeSchedule? = null

        @SerializedName("partWork")
        @Expose
        var partWork: Int? = null

        constructor(parcel: Parcel) : this() {
            timingDetails = parcel.readParcelable(LumpersTimeSchedule::class.java.classLoader)
            partWork = parcel.readValue(Int::class.java.classLoader) as? Int
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(timingDetails, flags)
            parcel.writeValue(partWork)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<WorkTime> {
            override fun createFromParcel(parcel: Parcel): WorkTime {
                return WorkTime(parcel)
            }

            override fun newArray(size: Int): Array<WorkTime?> {
                return arrayOfNulls(size)
            }
        }
    }
}
package com.quickhandslogistics.modified.data.schedule

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class ScheduleDetail() : Parcelable {
    @SerializedName("scheduleIdentity")
    @Expose
    var scheduleIdentity: String? = null

    @SerializedName("buildingName")
    @Expose
    var buildingName: String? = null

    @SerializedName("scheduledFrom")
    @Expose
    var scheduledFrom: String? = null

    @SerializedName("scheduleTypes")
    @Expose
    var scheduleTypes: ScheduleTypes? = null

    @SerializedName("scheduleNote")
    @Expose
    var scheduleNote: String? = null

    @SerializedName("parameters")
    @Expose
    var parameters: List<String>? = null

    @SerializedName("totalNumberOfWorkItems")
    @Expose
    var totalNumberOfWorkItems: Int? = null

    var scheduleTypeNames: String = ""
    var allAssignedLumpers: ArrayList<EmployeeData> = ArrayList()

    constructor(parcel: Parcel) : this() {
        scheduleIdentity = parcel.readString()
        buildingName = parcel.readString()
        scheduledFrom = parcel.readString()
        scheduleTypes = parcel.readParcelable(ScheduleTypes::class.java.classLoader)
        scheduleNote = parcel.readString()
        parameters = parcel.createStringArrayList()
        totalNumberOfWorkItems = parcel.readValue(Int::class.java.classLoader) as? Int
        scheduleTypeNames = parcel.readString()!!
        allAssignedLumpers = parcel.createTypedArrayList(EmployeeData)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(scheduleIdentity)
        parcel.writeString(buildingName)
        parcel.writeString(scheduledFrom)
        parcel.writeParcelable(scheduleTypes, flags)
        parcel.writeString(scheduleNote)
        parcel.writeStringList(parameters)
        parcel.writeValue(totalNumberOfWorkItems)
        parcel.writeString(scheduleTypeNames)
        parcel.writeTypedList(allAssignedLumpers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScheduleDetail> {
        override fun createFromParcel(parcel: Parcel): ScheduleDetail {
            return ScheduleDetail(parcel)
        }

        override fun newArray(size: Int): Array<ScheduleDetail?> {
            return arrayOfNulls(size)
        }
    }

    class ScheduleTypes() : Parcelable {
        @SerializedName("live")
        @Expose
        var liveLoads: ArrayList<WorkItemDetail>? = null

        @SerializedName("drop")
        @Expose
        var drops: ArrayList<WorkItemDetail>? = null

        @SerializedName("outbounds")
        @Expose
        var outbounds: ArrayList<WorkItemDetail>? = null

        constructor(parcel: Parcel) : this() {
            liveLoads = parcel.createTypedArrayList(WorkItemDetail)
            drops = parcel.createTypedArrayList(WorkItemDetail)
            outbounds = parcel.createTypedArrayList(WorkItemDetail)
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeTypedList(liveLoads)
            parcel.writeTypedList(drops)
            parcel.writeTypedList(outbounds)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ScheduleTypes> {
            override fun createFromParcel(parcel: Parcel): ScheduleTypes {
                return ScheduleTypes(parcel)
            }

            override fun newArray(size: Int): Array<ScheduleTypes?> {
                return arrayOfNulls(size)
            }
        }
    }
}
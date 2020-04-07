package com.quickhandslogistics.modified.data.schedule

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.lumpers.EmployeeData

class ScheduleListAPIResponse : BaseResponse() {
    @SerializedName("data")
    @Expose
    var data: Data? = null

    inner class Data {
        @SerializedName("workItems")
        @Expose
        var scheduleDetailsList: ArrayList<ScheduleDetail>? = null
    }
}

class ScheduleDetail() : Parcelable {
    @SerializedName("scheduleIdentity")
    @Expose
    var scheduleIdentity: String? = null

    @SerializedName("buildingName")
    @Expose
    var buildingName: String? = null

    @SerializedName("startDate")
    @Expose
    var startDate: String? = null

    @SerializedName("scheduleTypes")
    @Expose
    var scheduleTypes: ScheduleTypes? = null

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
        startDate = parcel.readString()
        scheduleTypes = parcel.readParcelable(ScheduleTypes::class.java.classLoader)
        parameters = parcel.createStringArrayList()
        totalNumberOfWorkItems = parcel.readValue(Int::class.java.classLoader) as? Int
        scheduleTypeNames = parcel.readString()!!
        allAssignedLumpers = parcel.createTypedArrayList(EmployeeData)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(scheduleIdentity)
        parcel.writeString(buildingName)
        parcel.writeString(startDate)
        parcel.writeParcelable(scheduleTypes, flags)
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
}

class ScheduleTypes() : Parcelable {
    @SerializedName("live")
    @Expose
    var liveLoads: ArrayList<WorkItemDetail>? = null

    @SerializedName("drops")
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
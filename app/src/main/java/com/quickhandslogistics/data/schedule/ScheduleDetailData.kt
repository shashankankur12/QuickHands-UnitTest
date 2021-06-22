package com.quickhandslogistics.data.schedule

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.customerSheet.CustomerSheet
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.workSheet.WorkItemContainerDetails
import com.quickhandslogistics.data.workSheet.WorkItemScheduleDetails

class ScheduleDetailData() : Parcelable {
    @SerializedName("scheduleIdentity")
    @Expose
    var scheduleIdentity: String? = null

    @SerializedName("buildingName")
    @Expose
    var buildingName: String? = null

    @SerializedName("scheduledFrom")
    @Expose
    var scheduledFrom: String? = null

    @SerializedName("endDateForCurrentWorkItem")
    @Expose
    var endDateForCurrentWorkItem: String? = null

    @SerializedName("live")
    @Expose
    var liveLoads: ArrayList<WorkItemDetail>? = null

    @SerializedName("drop")
    @Expose
    var drops: ArrayList<WorkItemDetail>? = null


    @SerializedName("outbound")
    @Expose
    var outbounds: ArrayList<WorkItemDetail>? = null


    @SerializedName("scheduleNote")
    @Expose
    var scheduleNote: String? = null

    @SerializedName("parameters")
    @Expose
    var parameters: List<String>? = null

    @SerializedName("totalNumberOfWorkItems")
    @Expose
    var totalNumberOfWorkItems: Int? = null

    @SerializedName("customerSheet")
    @Expose
    var customerSheet: CustomerSheet? = null

    var commonStatus: String = ""
    var scheduleTypeNames: String = ""
    var scheduleDepartment: String = ""
    var allAssignedLumpers: ArrayList<EmployeeData> = ArrayList()

    constructor(parcel: Parcel) : this() {
        scheduleIdentity = parcel.readString()
        buildingName = parcel.readString()
        scheduledFrom = parcel.readString()
        endDateForCurrentWorkItem = parcel.readString()
        liveLoads = parcel.createTypedArrayList(WorkItemDetail)
        drops = parcel.createTypedArrayList(WorkItemDetail)
        outbounds = parcel.createTypedArrayList(WorkItemDetail)
        scheduleNote = parcel.readString()
        parameters = parcel.createStringArrayList()
        totalNumberOfWorkItems = parcel.readValue(Int::class.java.classLoader) as? Int
        commonStatus = parcel.readString()!!
        scheduleTypeNames = parcel.readString()!!
        scheduleDepartment = parcel.readString()!!
        allAssignedLumpers = parcel.createTypedArrayList(EmployeeData)!!
        customerSheet = parcel.readParcelable(CustomerSheet::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(scheduleIdentity)
        parcel.writeString(buildingName)
        parcel.writeString(scheduledFrom)
        parcel.writeString(endDateForCurrentWorkItem)
        parcel.writeTypedList(liveLoads)
        parcel.writeTypedList(drops)
        parcel.writeTypedList(outbounds)
        parcel.writeString(scheduleNote)
        parcel.writeStringList(parameters)
        parcel.writeValue(totalNumberOfWorkItems)
        parcel.writeString(commonStatus)
        parcel.writeString(scheduleTypeNames)
        parcel.writeString(scheduleDepartment)
        parcel.writeTypedList(allAssignedLumpers)
        parcel.writeParcelable(customerSheet, flags)
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


        constructor(parcel: Parcel) : this() {

        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {

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
package com.quickhandslogistics.data.workSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.LumperAttendanceData
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.utils.ScheduleUtils

class WorkItemContainerDetails() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("shift")
    @Expose
    var shift: String? = null

    @SerializedName("quantity")
    @Expose
    var quantity: Int? = null

    @SerializedName("department")
    @Expose
    var department: String? = null

    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("startTime")
    @Expose
    var startTime: String? = null

    @SerializedName("isScheduledByLead")
    @Expose
    var isScheduledByLead: Boolean? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("buildingThisWorkItemAssignedTo")
    @Expose
    var buildingThisWorkItemAssignedTo: String? = null

    @SerializedName("notesQHL")
    @Expose
    var notesQHL: String? = null

    @SerializedName("notesQHLCustomer")
    @Expose
    var notesQHLCustomer: String? = null

    @SerializedName("isCompleted")
    @Expose
    var isCompleted: Boolean? = null

    @SerializedName("buildingOps")
    @Expose
    var buildingOps: HashMap<String, String>? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("schedule")
    @Expose
    var schedule: WorkItemScheduleDetails? = null

    @SerializedName("oldWork")
    @Expose
    var oldWork: WorkItemContainerDetails? = null

    @SerializedName("lumperThisWorkItemAssignedTo")
    @Expose
    var assignedLumpersList: ArrayList<LumperAttendanceData>? = null
        get() = ScheduleUtils.sortEmployeesAttendanceList(field)

    @SerializedName("lumpersTimeSchedule")
    @Expose
    var lumpersTimeSchedule: ArrayList<LumpersTimeSchedule>? = null

    @SerializedName("attachments")
    @Expose
    var attachmentsList: List<String>? = null

    @SerializedName("buildingParams")
    @Expose
    var buildingParams: ArrayList<String>? = null



    //useless will remove when no need
    @SerializedName("scheduleIdentity")
    @Expose
    var scheduleIdentity: String? = null

    @SerializedName("scheduledFrom")
    @Expose
    var scheduledFrom: String? = null
    @SerializedName("scheduleNote")
    @Expose
    var scheduleNote: String? = null

    @SerializedName("endDateForThisWorkItem")
    @Expose
    var endDateForThisWorkItem: String? = null

    @SerializedName("scheduleForWeek")
    @Expose
    var scheduleForWeek: Boolean? = null

    @SerializedName("scheduleForMonth")
    @Expose
    var scheduleForMonth: Boolean? = null

    @SerializedName("specificDates")
    @Expose
    var specificDates: List<Any>? = null

    @SerializedName("numberOfDrops")
    @Expose
    var numberOfDrops: Int? = null
    @SerializedName("lumperAttendance")
    @Expose
    var attendanceDetail: AttendanceDetail? = null

    ////////////////////////////////////


    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        department = parcel.readString()
        type = parcel.readString()
        shift = parcel.readString()
        quantity = parcel.readValue(Int::class.java.classLoader) as? Int
        day = parcel.readString()
        startTime = parcel.readString()
        scheduleIdentity = parcel.readString()
        scheduledFrom = parcel.readString()
        scheduleNote = parcel.readString()
        endDateForThisWorkItem = parcel.readString()
        status = parcel.readString()
        notesQHL = parcel.readString()
        notesQHLCustomer = parcel.readString()
        isScheduledByLead = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        scheduleForWeek = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        scheduleForMonth = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isCompleted = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
        numberOfDrops = parcel.readValue(Int::class.java.classLoader) as? Int
        buildingThisWorkItemAssignedTo = parcel.readString()
        schedule = parcel.readParcelable(BuildingDetailData::class.java.classLoader)
        oldWork = parcel.readParcelable(WorkItemContainerDetails::class.java.classLoader)
        assignedLumpersList = parcel.readArrayList(LumperAttendanceData::class.java.classLoader) as ArrayList<LumperAttendanceData>
        lumpersTimeSchedule = parcel.readArrayList(LumperAttendanceData::class.java.classLoader) as ArrayList<LumpersTimeSchedule>
        attendanceDetail = parcel.readParcelable(AttendanceDetail::class.java.classLoader)
        attachmentsList = parcel.createStringArrayList()
        buildingParams = parcel.createStringArrayList()
//        buildingOps = HashMap()
//        readFromParcel(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(department)
        parcel.writeString(type)
        parcel.writeString(shift )
        parcel.writeValue(quantity)
        parcel.writeString(day)
        parcel.writeString(startTime)
        parcel.writeString(scheduleIdentity)
        parcel.writeString(scheduledFrom)
        parcel.writeString(scheduleNote)
        parcel.writeString(endDateForThisWorkItem)
        parcel.writeString(status)
        parcel.writeString(notesQHL)
        parcel.writeString(notesQHLCustomer)
        parcel.writeValue(isScheduledByLead)
        parcel.writeValue(scheduleForWeek)
        parcel.writeValue(scheduleForMonth)
        parcel.writeValue(isCompleted)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeValue(numberOfDrops)
        parcel.writeString(buildingThisWorkItemAssignedTo)
        parcel.writeParcelable(schedule, flags)
        parcel.writeParcelable(oldWork, flags)
        parcel.writeTypedList(assignedLumpersList)
        parcel.writeTypedList(lumpersTimeSchedule)
        parcel.writeParcelable(attendanceDetail, flags)
        parcel.writeStringList(attachmentsList)
        parcel.writeStringList(buildingParams)
        /*buildingOps?.let { data ->
            parcel.writeInt(data.size)
            for (s in data.keys) {
                parcel.writeString(s)
                parcel.writeString(data[s])
            }
        }*/
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WorkItemContainerDetails> {
        override fun createFromParcel(parcel: Parcel): WorkItemContainerDetails {
            return WorkItemContainerDetails(parcel)
        }

        override fun newArray(size: Int): Array<WorkItemContainerDetails?> {
            return arrayOfNulls(size)
        }
    }
}
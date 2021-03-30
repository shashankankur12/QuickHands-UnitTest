package com.quickhandslogistics.data.workSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ContainerGroupNote() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("noteForQHL")
    @Expose
    var noteForQHL: String? = null

    @SerializedName("noteForCustomer")
    @Expose
    var noteForCustomer: String? = null

    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("containerType")
    @Expose
    var containerType: String? = null

    @SerializedName("buildingId")
    @Expose
    var buildingId: String? = null

    @SerializedName("containers")
    @Expose
    var containers: ArrayList<String>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        type = parcel.readString()
        noteForQHL = parcel.readString()
        noteForCustomer = parcel.readString()
        day = parcel.readString()
        containerType = parcel.readString()
        buildingId = parcel.readString()
        containers = parcel.createStringArrayList()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(noteForQHL)
        parcel.writeString(noteForCustomer)
        parcel.writeString(day)
        parcel.writeString(containerType)
        parcel.writeString(buildingId)
        parcel.writeStringList(containers)
    }

    companion object CREATOR : Parcelable.Creator<ContainerGroupNote> {
        override fun createFromParcel(parcel: Parcel): ContainerGroupNote {
            return ContainerGroupNote(parcel)
        }

        override fun newArray(size: Int): Array<ContainerGroupNote?> {
            return arrayOfNulls(size)
        }
    }
}
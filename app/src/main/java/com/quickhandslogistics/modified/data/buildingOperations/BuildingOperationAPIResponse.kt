package com.quickhandslogistics.modified.data.buildingOperations

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.modified.data.BaseResponse

class BuildingOperationAPIResponse() : BaseResponse(), Parcelable {

    @SerializedName("data")
    @Expose
    var data: HashMap<String, String>? = null

    constructor(parcel: Parcel) : this() {
        data = HashMap()
        readFromParcel(parcel)
    }

    private fun readFromParcel(parcel: Parcel) {
        val count = parcel.readInt()
        for (i in 0 until count) {
            data?.put(parcel.readString()!!, parcel.readString()!!)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        data?.let { data ->
            parcel.writeInt(data.size)
            for (s in data.keys) {
                parcel.writeString(s)
                parcel.writeString(data[s])
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BuildingOperationAPIResponse> {
        override fun createFromParcel(parcel: Parcel): BuildingOperationAPIResponse {
            return BuildingOperationAPIResponse(parcel)
        }

        override fun newArray(size: Int): Array<BuildingOperationAPIResponse?> {
            return arrayOfNulls(size)
        }
    }
}
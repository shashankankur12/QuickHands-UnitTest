package com.quickhandslogistics.data.customerSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomerSheet(): Parcelable {
    @SerializedName("isSigned")
    @Expose
    var isSigned: Boolean? = null

    @SerializedName("note")
    @Expose
    var note: String? = null

    @SerializedName("signature")
    @Expose
    var signatureUrl: String? = null

    @SerializedName("representativeName")
    @Expose
    var customerRepresentativeName: String? = null


    constructor(parcel: Parcel) : this() {
        isSigned = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        note = parcel.readString()
        signatureUrl = parcel.readString()
        customerRepresentativeName = parcel.readString()

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(isSigned)
        parcel.writeString(note)
        parcel.writeString(signatureUrl)
        parcel.writeString(customerRepresentativeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerSheet> {
        override fun createFromParcel(parcel: Parcel): CustomerSheet {
            return CustomerSheet(parcel)
        }

        override fun newArray(size: Int): Array<CustomerSheet?> {
            return arrayOfNulls(size)
        }
    }
}
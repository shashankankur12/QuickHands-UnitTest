package com.quickhandslogistics.data.customerSheet

import android.os.Parcel
import android.os.Parcelable

class LocalCustomerSheetData() : Parcelable {
    var isSigned: Boolean? = null

    var note: String? = null

    var signatureFilePath: String? = null

    var customerRepresentativeName: String? = null

    constructor(parcel: Parcel) : this() {
        isSigned = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        note = parcel.readString()
        signatureFilePath = parcel.readString()
        customerRepresentativeName = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(isSigned)
        parcel.writeString(note)
        parcel.writeString(signatureFilePath)
        parcel.writeString(customerRepresentativeName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalCustomerSheetData> {
        override fun createFromParcel(parcel: Parcel): LocalCustomerSheetData {
            return LocalCustomerSheetData(parcel)
        }

        override fun newArray(size: Int): Array<LocalCustomerSheetData?> {
            return arrayOfNulls(size)
        }
    }
}
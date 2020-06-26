package com.quickhandslogistics.data.customerSheet

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CustomerSheetData() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("numberOfItemsOnHold")
    @Expose
    var numberOfItemsOnHold: Int? = null

    @SerializedName("numberOfPendingItems")
    @Expose
    var numberOfPendingItems: Int? = null

    @SerializedName("numberOfCompletedItems")
    @Expose
    var numberOfCompletedItems: Int? = null

    @SerializedName("numberOfCancelledItems")
    @Expose
    var numberOfCancelledItems: Int? = null

    @SerializedName("day")
    @Expose
    var day: String? = null

    @SerializedName("isFinal")
    @Expose
    var isFinal: Boolean? = null

    @SerializedName("isSigned")
    @Expose
    var isSigned: Boolean? = null

    @SerializedName("note")
    @Expose
    var note: String? = null

    @SerializedName("customerRepresentativeSignatureUrl")
    @Expose
    var signatureUrl: String? = null

    @SerializedName("customerRepresentativeName")
    @Expose
    var customerRepresentativeName: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        numberOfItemsOnHold = parcel.readValue(Int::class.java.classLoader) as? Int
        numberOfPendingItems = parcel.readValue(Int::class.java.classLoader) as? Int
        numberOfCompletedItems = parcel.readValue(Int::class.java.classLoader) as? Int
        numberOfCancelledItems = parcel.readValue(Int::class.java.classLoader) as? Int
        day = parcel.readString()
        isFinal = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isSigned = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        note = parcel.readString()
        signatureUrl = parcel.readString()
        customerRepresentativeName = parcel.readString()
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeValue(numberOfItemsOnHold)
        parcel.writeValue(numberOfPendingItems)
        parcel.writeValue(numberOfCompletedItems)
        parcel.writeValue(numberOfCancelledItems)
        parcel.writeString(day)
        parcel.writeValue(isFinal)
        parcel.writeValue(isSigned)
        parcel.writeString(note)
        parcel.writeString(signatureUrl)
        parcel.writeString(customerRepresentativeName)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerSheetData> {
        override fun createFromParcel(parcel: Parcel): CustomerSheetData {
            return CustomerSheetData(parcel)
        }

        override fun newArray(size: Int): Array<CustomerSheetData?> {
            return arrayOfNulls(size)
        }
    }
}
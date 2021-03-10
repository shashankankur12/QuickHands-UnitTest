package com.quickhandslogistics.data.dashboard

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomerDetail() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("companyName")
    @Expose
    var companyName: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("companyAdminName")
    @Expose
    var companyAdminName: String? = null

    @SerializedName("companyState")
    @Expose
    var companyState: String? = null

    @SerializedName("companyCity")
    @Expose
    var companyCity: String? = null

    @SerializedName("companyZipcode")
    @Expose
    var companyZipcode: String? = null

    @SerializedName("companyAddress")
    @Expose
    var companyAddress: String? = null

    @SerializedName("phone")
    @Expose
    var phone: String? = null

    @SerializedName("officialEmail")
    @Expose
    var officialEmail: String? = null

    @SerializedName("role")
    @Expose
    var role: String? = null

    @SerializedName("addedBy")
    @Expose
    var addedBy: String? = null

    @SerializedName("isCompanyVerified")
    @Expose
    var isCompanyVerified: Boolean? = null

    @SerializedName("isEmailVerified")
    @Expose
    var isEmailVerified: Boolean? = null

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null

    @SerializedName("logoUrl")
    @Expose
    var logoUrl: Any? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        companyName = parcel.readString()
        companyAdminName = parcel.readString()
        companyState = parcel.readString()
        companyCity = parcel.readString()
        companyZipcode = parcel.readString()
        companyAddress = parcel.readString()
        phone = parcel.readString()
        officialEmail = parcel.readString()
        role = parcel.readString()
        addedBy = parcel.readString()
        isCompanyVerified = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isEmailVerified = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        isActive = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        createdAt = parcel.readString()
        updatedAt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(companyName)
        parcel.writeString(companyAdminName)
        parcel.writeString(companyState)
        parcel.writeString(companyCity)
        parcel.writeString(companyZipcode)
        parcel.writeString(companyAddress)
        parcel.writeString(phone)
        parcel.writeString(officialEmail)
        parcel.writeString(role)
        parcel.writeString(addedBy)
        parcel.writeValue(isCompanyVerified)
        parcel.writeValue(isEmailVerified)
        parcel.writeValue(isActive)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomerDetail> {
        override fun createFromParcel(parcel: Parcel): CustomerDetail {
            return CustomerDetail(parcel)
        }

        override fun newArray(size: Int): Array<CustomerDetail?> {
            return arrayOfNulls(size)
        }
    }
}
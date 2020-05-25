package com.quickhandslogistics.data.customerSheet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomerSheetData {
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
}
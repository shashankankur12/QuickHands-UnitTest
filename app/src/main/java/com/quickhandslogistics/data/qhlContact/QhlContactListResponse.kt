package com.quickhandslogistics.data.qhlContact

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.lumperSheet.LumpersInfo
import com.quickhandslogistics.data.lumpers.EmployeeData

class QhlContactListResponse : BaseResponse() {

    @SerializedName("data")
    @Expose
    val qhlContactList: ArrayList<EmployeeData>? = null
        get() = if (!field.isNullOrEmpty()) field else ArrayList()
}